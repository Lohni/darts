package com.lohni.darts.viewmodel

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.lohni.darts.room.AppDatabase
import com.lohni.darts.room.dao.PlayerDao
import com.lohni.darts.room.dao.StatisticsDao
import com.lohni.darts.room.dto.LegGroupedThrows
import com.lohni.darts.room.dto.PlayerComparisonView
import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.GameModeType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class StatisticsViewModel(
    private val statistisDao: StatisticsDao,
    private val playerDao: PlayerDao
) : ViewModel() {

    var player = mutableStateOf(Player(-1, "Guest"))
    var playerToCompare = mutableStateOf(Player(-1, "Compare"))
    var gameMode = mutableStateOf(GameMode(gmName = "Select"))
    var availableGameModes = mutableStateListOf<GameMode>()
    var availablePlayers = mutableStateListOf<Player>()

    // Common stats
    var darts = mutableIntStateOf(0)
    var dartsPerLeg = mutableFloatStateOf(0f)
    var average = mutableFloatStateOf(0f)
    var triples = mutableIntStateOf(0)
    var doubles = mutableIntStateOf(0)
    var misses = mutableIntStateOf(0)
    var highestFinish = mutableFloatStateOf(0f)
    var eightyPlus = mutableIntStateOf(0)
    var hundredPlus = mutableIntStateOf(0)
    var hundredFourtyPlus = mutableIntStateOf(0)
    var hundredEightyPlus = mutableIntStateOf(0)

    var scoringDarts = mutableIntStateOf(0)
    var finishDarts = mutableIntStateOf(0)
    var scoringAverage = mutableFloatStateOf(0f)
    var finishAverage = mutableFloatStateOf(0f)

    //Competitive
    var competitiveStats = mutableStateListOf<PlayerComparisonView>()
    var directComparisonStats = mutableStateListOf<PlayerComparisonView>()
    var comparisonAverage = mutableStateListOf<Float>()

    //Data series for visual
    var firstPlayedGame = mutableStateOf(LocalDate.now())
    var dailyGroupedRunningAvg = mutableStateMapOf<LocalDate, Float>()
    var dailyGroupedRunningScoringAvg = mutableStateMapOf<LocalDate, Float>()
    var dailyGroupedRunningFinishAvg = mutableStateMapOf<LocalDate, Float>()

    var dailyGroupedAvg = mutableStateMapOf<LocalDate, Float>()
    var dailyGroupedScoringAvg = mutableStateMapOf<LocalDate, Float>()
    var dailyGroupedFinishAvg = mutableStateMapOf<LocalDate, Float>()

    init {
        viewModelScope.launch {
            playerDao.getAllPlayers().first {
                availablePlayers.addAll(it)
            }
            statistisDao.getAllGameModes().first {
                availableGameModes.addAll(it)
            }

            getSharedStatistics()

            // Classic stats
            if (gameMode.value.gmType == GameModeType.CLASSIC) {
                getClassicStatistics()
            }

            statistisDao.getLastGameMode().first {
                it?.let { changeGameMode(it) }
                true
            }
        }
    }

    fun changePlayer(p: Player) {
        player.value = p
        viewModelScope.launch {
            getSharedStatistics()
            if (gameMode.value.gmType == GameModeType.CLASSIC) {
                getClassicStatistics()
            }
        }
    }

    fun changeGameMode(gm: GameMode) {
        gameMode.value = gm
        viewModelScope.launch {
            getSharedStatistics()
            if (gm.gmType == GameModeType.CLASSIC) {
                getClassicStatistics()
            }
        }
    }

    fun changeComparingPlayer(p: Player) {
        playerToCompare.value = p
        viewModelScope.launch {
            getCompetitiveStatistics()
        }
    }

    private suspend fun getSharedStatistics() {
        if (gameMode.value.gmId == 0) return

        statistisDao.getPlayerDartsForGameMode(gameMode.value.gmId, player.value.pId).first {
            darts.intValue = it
            true
        }
        statistisDao.getPlayerAverageForGameMode(gameMode.value.gmId, player.value.pId).first {
            average.floatValue = it
            true
        }
        statistisDao.getAverageDartsPerLegForGameMode(gameMode.value.gmId, player.value.pId).first {
            dartsPerLeg.floatValue = it
            true
        }
        statistisDao.getThrowsPerLegForPlayerAndGameMode(gameMode.value.gmId, player.value.pId)
            .first {
                statistisDao.getGameModeConfig(gameMode.value.gmConfig).first { gmc ->
                    statistisDao.getCheckoutTableByType(gmc.gmcCheckOut).first { table ->
                        calculateScoringAndFinishStatistics(it, table)
                        true
                    }
                    true
                }
                true
            }

        getCompetitiveStatistics()
    }

    private suspend fun getCompetitiveStatistics() {
        if (gameMode.value.gmId == 0) return

        val players = mutableListOf(player.value)
        if (playerToCompare.value.pId > 0) players.add(playerToCompare.value)

        statistisDao.getGameSetLegForPlayerCombination(
            gameMode.value.gmId,
            player.value.pId,
            players.last().pId
        )
            .first { games ->
                val totalStats = mutableListOf<PlayerComparisonView>()
                val comparisonStats = mutableListOf<PlayerComparisonView>()
                val averages = mutableListOf<Float>()
                for (p in players) {
                    val sets = games.flatMap { it.sets }
                    val legs = sets.flatMap { it.legs }

                    comparisonStats.add(
                        PlayerComparisonView(
                            games.size,
                            games.map { it.game }.count { it.gWinner == p.pId },
                            sets.count(),
                            sets.count { it.set.sWinner == p.pId },
                            legs.count(),
                            legs.count { it.lWinner == p.pId },
                            p
                        )
                    )
                    statistisDao.getPlayerAverageForGameMode(gameMode.value.gmId, p.pId).first {
                        averages.add(it)
                    }
                    statistisDao.getOverallCompetitiveStatistics(gameMode.value.gmId, p.pId).first {
                        totalStats.add(it)
                    }
                }

                competitiveStats.clear()
                competitiveStats.addAll(totalStats)
                directComparisonStats.clear()
                directComparisonStats.addAll(comparisonStats)
                comparisonAverage.clear()
                comparisonAverage.addAll(averages)
            }
    }

    private suspend fun getClassicStatistics() {
        if (gameMode.value.gmId == 0) return

        statistisDao.getPlayerThrowsForFieldTypeAndGameMode(
            gameMode.value.gmId,
            player.value.pId,
            FieldType.Triple
        ).first {
            triples.intValue = it
            true
        }
        statistisDao.getPlayerThrowsForFieldTypeAndGameMode(
            gameMode.value.gmId,
            player.value.pId,
            FieldType.Double
        ).first {
            doubles.intValue = it
            true
        }
        statistisDao.getPlayerMissesForGameMode(gameMode.value.gmId, player.value.pId).first {
            misses.intValue = it
            true
        }
        statistisDao.getPlayerHighestFinishForGameMode(gameMode.value.gmId, player.value.pId)
            .first {
                highestFinish.floatValue = it
                true
            }
        statistisDao.getPlayerThrowsForGameModeAboveScoreThreshold(
            gameMode.value.gmId,
            player.value.pId,
            80f
        ).first {
            eightyPlus.intValue = it
            true
        }
        statistisDao.getPlayerThrowsForGameModeAboveScoreThreshold(
            gameMode.value.gmId,
            player.value.pId,
            100f
        ).first {
            hundredPlus.intValue = it
            true
        }
        statistisDao.getPlayerThrowsForGameModeAboveScoreThreshold(
            gameMode.value.gmId,
            player.value.pId,
            140f
        ).first {
            hundredFourtyPlus.intValue = it
            true
        }
        statistisDao.getPlayerThrowsForGameModeAboveScoreThreshold(
            gameMode.value.gmId,
            player.value.pId,
            180f
        ).first {
            hundredEightyPlus.intValue = it
            true
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return StatisticsViewModel(
                    AppDatabase.getInstance(application.applicationContext).statisticsDao(),
                    AppDatabase.getInstance(application.applicationContext).playerDao()
                ) as T
            }
        }
    }

    private fun calculateScoringAndFinishStatistics(
        legThrows: List<LegGroupedThrows>,
        suggestions: List<CheckoutTable>
    ) {
        resetVisualStats()

        //Get earliest date for Timeframe all
        firstPlayedGame.value = legThrows.firstOrNull()?.gameDate?.toLocalDate() ?: LocalDate.now()

        // throw are sorted t_ordinal ASC
        val totalAverageInfo = AverageStatistic()
        val aboveFinishInfo = AverageStatistic()
        val belowFinishInfo = AverageStatistic()

        var currDate = LocalDate.MIN
        val dateInfo = AverageStatistic()
        val dateScoringInfo = AverageStatistic()
        val dateFinishInfo = AverageStatistic()

        for (throws in legThrows) {
            if (currDate.isBefore(throws.gameDate.toLocalDate())) {
                if (currDate != LocalDate.MIN) {
                    dailyGroupedRunningAvg[currDate] = totalAverageInfo.calcAverage()
                    dailyGroupedRunningScoringAvg[currDate] = aboveFinishInfo.calcAverage()
                    dailyGroupedRunningFinishAvg[currDate] = belowFinishInfo.calcAverage()
                    dailyGroupedAvg[currDate] = dateInfo.calcAverage()
                    dailyGroupedScoringAvg[currDate] = dateScoringInfo.calcAverage()
                    dailyGroupedFinishAvg[currDate] = dateFinishInfo.calcAverage()
                }

                dateInfo.reset()
                dateScoringInfo.reset()
                dateFinishInfo.reset()
                currDate = throws.gameDate.toLocalDate()
            }

            var currScore = gameMode.value.gmStartScore.toFloat()
            var canFinish = false
            for (t in throws.throws) {
                if (!canFinish && suggestions.any { it.ctScore == currScore }) {
                    canFinish = true
                }
                currScore -= t.tScore

                if (!canFinish) {
                    aboveFinishInfo.addScore(t)
                    dateScoringInfo.addScore(t)
                } else if (throws.hasWon) {
                    belowFinishInfo.addScore(t)
                    dateFinishInfo.addScore(t)
                }
                dateInfo.addScore(t)
                totalAverageInfo.addScore(t)
            }
        }

        dailyGroupedRunningAvg[currDate] = totalAverageInfo.calcAverage()
        dailyGroupedRunningScoringAvg[currDate] = aboveFinishInfo.calcAverage()
        dailyGroupedRunningFinishAvg[currDate] = belowFinishInfo.calcAverage()
        dailyGroupedAvg[currDate] = dateInfo.calcAverage()
        dailyGroupedScoringAvg[currDate] = dateScoringInfo.calcAverage()
        dailyGroupedFinishAvg[currDate] = dateFinishInfo.calcAverage()

        finishDarts.intValue = belowFinishInfo.darts
        scoringDarts.intValue = aboveFinishInfo.darts

        finishAverage.floatValue = belowFinishInfo.calcAverage()
        scoringAverage.floatValue = aboveFinishInfo.calcAverage()
    }

    private fun resetVisualStats() {
        dailyGroupedRunningAvg.clear()
        dailyGroupedRunningScoringAvg.clear()
        dailyGroupedRunningFinishAvg.clear()
        dailyGroupedAvg.clear()
        dailyGroupedScoringAvg.clear()
        dailyGroupedFinishAvg.clear()
    }

    private class AverageStatistic {
        var darts = 0
        var score = 0f

        fun addScore(t: Throw) {
            score += if (t.tBust) 0f else t.tScore
            darts += 1
        }

        fun calcAverage(): Float {
            return if (darts == 0) 0f else (score / darts) * 3
        }

        fun reset() {
            darts = 0
            score = 0f
        }
    }
}
