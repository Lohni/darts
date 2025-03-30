package com.lohni.darts.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.lohni.darts.game.CheckoutState
import com.lohni.darts.game.GameState
import com.lohni.darts.game.State
import com.lohni.darts.room.AppDatabase
import com.lohni.darts.room.dao.CheckoutDao
import com.lohni.darts.room.dao.GameDao
import com.lohni.darts.room.dao.GameModeDao
import com.lohni.darts.room.dao.PlayerDao
import com.lohni.darts.room.dao.SettingsDao
import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.Game
import com.lohni.darts.room.entities.GamePlayer
import com.lohni.darts.room.entities.Leg
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Set
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.SettingType
import com.lohni.darts.ui.screens.game.GameRoute
import com.lohni.darts.ui.view.GameView
import com.lohni.darts.ui.view.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameDao: GameDao,
    private val playerDao: PlayerDao,
    private val gameModeDao: GameModeDao,
    private val settingsDao: SettingsDao,
    private val checkoutDao: CheckoutDao,
    private val gameModeId: Int = 0,
    private val numSets: Int = 1,
    private val numLegs: Int = 1,
    private val playerIds: List<Int> = emptyList(),
    private val randomOrder: Boolean = false
) : ViewModel() {
    private lateinit var players: List<Player>
    private lateinit var gameState: GameState
    private lateinit var checkoutState: CheckoutState

    private var gameView = mutableStateOf<GameView?>(null)

    var game = mutableStateOf(Game(gSets = numSets, gLegs = numLegs, gGameMode = gameModeId))
    var isDarkMode = mutableStateOf(false)
    var gameModeName = mutableStateOf("")
    var playerView = mutableStateOf<PlayerView?>(null)
    var fieldModifier = mutableStateOf(FieldType.Single)
    var suggestion = mutableStateOf(CheckoutState.CheckoutSuggestion("-", "-", "-"))
    var stateToDisplay = mutableStateOf<State?>(null)
    var wasStateDisplayed = mutableStateOf(false)

    init {
        initializeDatabaseObjects()
    }

    fun onThrow(field: Field) {
        playerView.value = gameState.onThrow(field, fieldModifier.value)
        fieldModifier.value = FieldType.Single
        suggestion.value = checkoutState.getSuggestion(playerView.value)

        if (playerView.value?.throwThree != null) {
            viewModelScope.launch {
                delay(800)
                updateGameView()
            }
        }
    }

    fun undo() {
        gameState.undo()
        updateGameView()
        fieldModifier.value = FieldType.Single
    }

    fun changeFieldModifier(fieldType: FieldType) {
        if (fieldModifier.value == fieldType) {
            fieldModifier.value = FieldType.Single
        } else {
            fieldModifier.value = fieldType
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val GAME_KEY = object : CreationExtras.Key<GameRoute> {}
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                extras[GAME_KEY]?.let {
                    return GameViewModel(
                        AppDatabase.getInstance(application.applicationContext).gameDao(),
                        AppDatabase.getInstance(application.applicationContext).playerDao(),
                        AppDatabase.getInstance(application.applicationContext).gameModeDao(),
                        AppDatabase.getInstance(application.applicationContext).settingsDao(),
                        AppDatabase.getInstance(application.applicationContext).checkoutDao(),
                        it.gameMode,
                        it.numSets,
                        it.numLegs,
                        it.playerIds,
                        it.randomOrder
                    ) as T
                }
                return GameViewModel(
                    AppDatabase.getInstance(application.applicationContext).gameDao(),
                    AppDatabase.getInstance(application.applicationContext).playerDao(),
                    AppDatabase.getInstance(application.applicationContext).gameModeDao(),
                    AppDatabase.getInstance(application.applicationContext).settingsDao(),
                    AppDatabase.getInstance(application.applicationContext).checkoutDao()
                ) as T
            }
        }
    }

    private fun initializeDatabaseObjects() {
        viewModelScope.launch {
            gameModeDao.getGameConfigurationViewForGameMode(gameModeId).first { gmcView ->
                gameModeName.value = gmcView.gameMode.gameMode.gmName
                gmcView.gameModeConfig.gameModeConfig.gmcSuccessCalculation?.let {
                    gameModeDao.getScoreCalculationById(it).first { sc ->
                        gmcView.gameModeConfig.successCalculation = sc
                        true
                    }
                }
                gmcView.gameModeConfig.gameModeConfig.gmcFailureCalculation?.let {
                    gameModeDao.getScoreCalculationById(it).first { sc ->
                        gmcView.gameModeConfig.failureCalculation = sc
                        true
                    }
                }

                playerDao.getPlayersByIds(playerIds).first { p ->
                    players = p

                    gameState = GameState(
                        numSets,
                        numLegs,
                        gmcView,
                        players,
                        { state, winner -> onStateFinish(state, winner) },
                        randomOrder
                    )
                    true
                }
                true
            }

            settingsDao.getSetting(SettingType.DARK_MODE).first {
                isDarkMode.value = it.sValue.toBooleanStrict()
                true
            }
        }.invokeOnCompletion {
            viewModelScope.launch {
                val checkout =
                    gameState.gameConfigurationView.gameModeConfig.gameModeConfig.gmcCheckOut
                checkoutDao.getCheckoutTables(checkout, -2)
                    .first { defaultSuggestions ->
                        val defaultMap = defaultSuggestions.associateBy { it.ctScore }
                        val playerMap = mutableMapOf<Int, Map<Float, CheckoutTable>>()
                        for (player in players) {
                            checkoutDao.getCheckoutTables(checkout, player.pId)
                                .first { playerSuggestion ->
                                    if (playerSuggestion.isNotEmpty()) {
                                        playerMap[player.pId] =
                                            playerSuggestion.associateBy { it.ctScore }
                                    }
                                    true
                                }
                        }
                        checkoutState = CheckoutState(
                            playerMap,
                            defaultMap,
                            gameState.gameConfigurationView.gameMode.steps
                        )
                        updateGameView()
                        true
                    }
            }
        }
    }

    private fun updateGameView() {
        val view = gameState.getGameView()

        gameView.value = view
        playerView.value = view.currPlayerView
        suggestion.value = checkoutState.getSuggestion(view.currPlayerView)
    }

    private fun onStateFinish(state: State, winner: Player) {
        if (state is GameState) {
            endGame(state, winner)
        } else {
            stateToDisplay.value = state
            wasStateDisplayed.value = false
            updateGameView()
        }
    }

    private fun endGame(gameState: GameState, winner: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            val gameToInsert = game.value.copy(gWinner = winner.pId)
            val gameId = gameDao.insertGame(gameToInsert).toInt()
            gameState.state.forEach { (setPlayer, wonSets) ->
                gameDao.insertGamePlayer(GamePlayer(gameId, setPlayer.pId))
                wonSets.forEach { wonSet ->
                    val set = Set(
                        sGame = gameId,
                        sOrdinal = wonSet.ordinal,
                        sWinner = setPlayer.pId
                    )
                    val sId = gameDao.insertSet(set).toInt()

                    wonSet.state.forEach { (legPlayer, wonLegs) ->
                        wonLegs.forEach { wonLeg ->
                            val leg = Leg(
                                lSet = sId,
                                lOrdinal = wonLeg.ordinal,
                                lWinner = legPlayer.pId
                            )

                            val lId = gameDao.insertLeg(leg).toInt()

                            wonLeg.turnList.forEach { turn ->
                                turn.throwOne?.let {
                                    it.tLeg = lId
                                    it.tBust = turn.busted
                                    gameDao.insertThrow(it)
                                }
                                turn.throwTwo?.let {
                                    it.tLeg = lId
                                    it.tBust = turn.busted
                                    gameDao.insertThrow(it)
                                }
                                turn.throwThree?.let {
                                    it.tLeg = lId
                                    it.tBust = turn.busted
                                    gameDao.insertThrow(it)
                                }
                            }
                        }
                    }
                }
            }
            game.value = gameToInsert.copy(gId = gameId)
        }
    }
}