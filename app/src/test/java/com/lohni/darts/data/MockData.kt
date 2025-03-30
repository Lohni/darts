package com.lohni.darts.data

import com.lohni.darts.game.GameState
import com.lohni.darts.room.dto.GameConfigurationView
import com.lohni.darts.room.dto.GameModeConfigView
import com.lohni.darts.room.dto.GameModeView
import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.Game
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.GameModeConfig
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.ScoreCalculation
import com.lohni.darts.room.entities.Throw
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.room.enums.ScoreModifier
import com.lohni.darts.room.enums.StepWinCondition
import com.lohni.darts.ui.view.GameView
import com.lohni.darts.ui.view.PlayerView
import org.junit.Assert


fun GameView.assertUiState(
    pId: Int,
    score: Float,
    throwOneScore: Float? = null,
    throwTwoScore: Float? = null,
    throwThreeScore: Float? = null,
    legsWon: Int = 0,
    setsWon: Int = 0,
) {
    val currPlayer = this.currPlayerView
    Assert.assertEquals(pId, currPlayer.player.pId)
    Assert.assertEquals(score, currPlayer.score)
    Assert.assertEquals(throwOneScore, currPlayer.throwOne?.tScore)
    Assert.assertEquals(throwTwoScore, currPlayer.throwTwo?.tScore)
    Assert.assertEquals(throwThreeScore, currPlayer.throwThree?.tScore)
    Assert.assertEquals(legsWon, currPlayer.legsWon)
    Assert.assertEquals(setsWon, currPlayer.setsWon)
}

fun createGameMode(): GameMode {
    return GameMode(
        gmType = GameModeType.CLASSIC,
        gmStartScore = 301
    )
}

fun createGameModeConfig(checkOut: FieldType = FieldType.ALL): GameModeConfig {
    return GameModeConfig(
        gmcCheckOut = checkOut
    )
}

fun createPlayers(num: Int): List<Player> {
    val players = mutableListOf<Player>()
    for (i in 0..<num) {
        players.add(Player(pId = i, pName = "Player $i"))
    }
    return players
}

fun finishTurn301(gameState: GameState, checkOut: FieldType, numPlayers: Int, winner: Int) {
    for (i in 0..numPlayers) {
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.NINETEEN, FieldType.Triple)
    }

    while (winner != gameState.getGameView().currPlayerView.player.pId) {
        gameState.onThrow(Field.ZERO, FieldType.Single)
        gameState.onThrow(Field.ZERO, FieldType.Single)
        gameState.onThrow(Field.ZERO, FieldType.Single)
    }

    //124
    gameState.onThrow(Field.TWENTY, FieldType.Triple)
    gameState.onThrow(Field.TWENTY, FieldType.Double)

    if (checkOut == FieldType.Triple) gameState.onThrow(Field.EIGHT, FieldType.Triple)
    else gameState.onThrow(Field.TWELVE, FieldType.Double)
}

fun createPlayerView(
    score: Float,
    fieldOne: Field = Field.NONE,
    fieldTypeOne: FieldType = FieldType.Single,
    fieldTwo: Field = Field.NONE,
    fieldTypeTwo: FieldType = FieldType.Single,
    fieldThree: Field = Field.NONE,
    fieldTypeThree: FieldType = FieldType.Single,
    stepRequirement: String = "",
    stepOne: Int = 0,
    stepTwo: Int = 0,
    stepThree: Int = 0,
): PlayerView {
    return PlayerView(
        Player(-1, ""),
        score,
        -1f,
        0,
        if (fieldOne != Field.NONE) Throw(
            tGameModeStep = stepOne,
            tPlayer = -1,
            tField = fieldOne,
            tFieldType = fieldTypeOne,
            tScore = (fieldOne.fId * fieldTypeOne.ftId).toFloat()
        ) else null,
        if (fieldTwo != Field.NONE) Throw(
            tGameModeStep = stepTwo,
            tPlayer = -1,
            tField = fieldTwo,
            tFieldType = fieldTypeTwo,
            tScore = (fieldTwo.fId * fieldTypeTwo.ftId).toFloat()
        ) else null,
        if (fieldThree != Field.NONE) Throw(
            tGameModeStep = stepThree,
            tPlayer = -1,
            tField = fieldThree,
            tFieldType = fieldTypeThree,
            tScore = (fieldThree.fId * fieldTypeThree.ftId).toFloat()
        ) else null,
        stepRequirement = stepRequirement,
    )
}

fun checkoutMap(): Map<Float, CheckoutTable> {
    val map = mutableMapOf<Float, CheckoutTable>()
    map[2f] = CheckoutTable(ctScore = 2f, ctFieldOne = Field.ONE, ctFieldTypeOne = FieldType.Double)
    map[3f] = CheckoutTable(
        ctScore = 3f,
        ctFieldOne = Field.ONE,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.ONE,
        ctFieldTypeTwo = FieldType.Double
    )
    map[4f] = CheckoutTable(ctScore = 4f, ctFieldOne = Field.TWO, ctFieldTypeOne = FieldType.Double)
    map[5f] = CheckoutTable(
        ctScore = 5f,
        ctFieldOne = Field.ONE,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.TWO,
        ctFieldTypeTwo = FieldType.Double
    )
    map[6f] =
        CheckoutTable(ctScore = 6f, ctFieldOne = Field.THREE, ctFieldTypeOne = FieldType.Double)
    map[7f] = CheckoutTable(
        ctScore = 7f,
        ctFieldOne = Field.THREE,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.TWO,
        ctFieldTypeTwo = FieldType.Double
    )
    map[8f] =
        CheckoutTable(ctScore = 8f, ctFieldOne = Field.FOUR, ctFieldTypeOne = FieldType.Double)
    map[9f] = CheckoutTable(
        ctScore = 9f,
        ctFieldOne = Field.ONE,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.FOUR,
        ctFieldTypeTwo = FieldType.Double
    )
    map[10f] =
        CheckoutTable(ctScore = 10f, ctFieldOne = Field.FIVE, ctFieldTypeOne = FieldType.Double)
    map[11f] = CheckoutTable(
        ctScore = 11f,
        ctFieldOne = Field.THREE,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.FOUR,
        ctFieldTypeTwo = FieldType.Double
    )
    map[12f] =
        CheckoutTable(ctScore = 12f, ctFieldOne = Field.SIX, ctFieldTypeOne = FieldType.Double)
    map[13f] = CheckoutTable(
        ctScore = 13f,
        ctFieldOne = Field.FIVE,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.FOUR,
        ctFieldTypeTwo = FieldType.Double
    )
    map[14f] =
        CheckoutTable(ctScore = 14f, ctFieldOne = Field.SEVEN, ctFieldTypeOne = FieldType.Double)
    map[15f] = CheckoutTable(
        ctScore = 15f,
        ctFieldOne = Field.SEVEN,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.FOUR,
        ctFieldTypeTwo = FieldType.Double
    )
    map[16f] =
        CheckoutTable(ctScore = 16f, ctFieldOne = Field.EIGHT, ctFieldTypeOne = FieldType.Double)
    map[17f] = CheckoutTable(
        ctScore = 17f,
        ctFieldOne = Field.ONE,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.EIGHT,
        ctFieldTypeTwo = FieldType.Double
    )
    map[18f] =
        CheckoutTable(ctScore = 18f, ctFieldOne = Field.NINE, ctFieldTypeOne = FieldType.Double)
    map[19f] = CheckoutTable(
        ctScore = 19f,
        ctFieldOne = Field.THREE,
        ctFieldTypeOne = FieldType.Single,
        ctFieldTwo = Field.EIGHT,
        ctFieldTypeTwo = FieldType.Double
    )
    map[20f] =
        CheckoutTable(ctScore = 20f, ctFieldOne = Field.TEN, ctFieldTypeOne = FieldType.Double)
    map[32f] =
        CheckoutTable(ctScore = 32f, ctFieldOne = Field.SIXTEEN, ctFieldTypeOne = FieldType.Double)
    map[170f] = CheckoutTable(
        ctScore = 170f,
        ctFieldOne = Field.TWENTY,
        ctFieldTypeOne = FieldType.Triple,
        ctFieldTwo = Field.TWENTY,
        ctFieldTypeTwo = FieldType.Triple,
        ctFieldThree = Field.TWENTYFIVE,
        ctFieldTypeThree = FieldType.Double
    )
    map[141f] = CheckoutTable(
        ctScore = 141f,
        ctFieldOne = Field.TWENTY,
        ctFieldTypeOne = FieldType.Triple,
        ctFieldTwo = Field.NINETEEN,
        ctFieldTypeTwo = FieldType.Triple,
        ctFieldThree = Field.TWELVE,
        ctFieldTypeThree = FieldType.Double
    )
    map[84f] = CheckoutTable(
        ctScore = 84f,
        ctFieldOne = Field.TWENTY,
        ctFieldTypeOne = FieldType.Triple,
        ctFieldTwo = Field.TWELVE,
        ctFieldTypeTwo = FieldType.Double,
    )
    map[24f] = CheckoutTable(
        ctScore = 24f,
        ctFieldOne = Field.TWELVE,
        ctFieldTypeOne = FieldType.Double,
    )
    return map
}

fun createClassicGameConfiguration(checkOut: FieldType = FieldType.ALL): GameConfigurationView {
    return GameConfigurationView(
        GameModeView(
            GameMode(gmType = GameModeType.CLASSIC, gmStartScore = 301),
            listOf()
        ),
        GameModeConfigView(
            GameModeConfig(gmcCheckOut = checkOut),
            null,
            null
        )
    )
}

fun createGameConfiguration(): GameConfigurationView {
    return GameConfigurationView(
        GameModeView(
            GameMode(gmType = GameModeType.STEP, gmStartScore = 40),
            createHalveItSteps()
        ),
        GameModeConfigView(
            GameModeConfig(gmcStepWinCondition = StepWinCondition.HIGHEST_SCORE),
            ScoreCalculation(),
            ScoreCalculation(
                scByTypeModifier = ScoreModifier.NONE,
                scByValueModifier = ScoreModifier.DIVIDE,
                scByValue = 2
            )
        )
    )
}

fun createShortHalveIt(
    winCondition: StepWinCondition,
    repeatOnFail: Boolean = false,
    immediatelyProceed: Boolean = false
): GameConfigurationView {
    return GameConfigurationView(
        GameModeView(
            GameMode(gmType = GameModeType.STEP, gmStartScore = 40),
            createShortHalveItSteps()
        ),
        GameModeConfigView(
            GameModeConfig(
                gmcStepWinCondition = winCondition,
                gmcRepeatOnFailure = repeatOnFail,
                gmcImmediateProceedOnSuccess = immediatelyProceed
            ),
            ScoreCalculation(),
            ScoreCalculation(
                scByTypeModifier = ScoreModifier.NONE,
                scByValueModifier = ScoreModifier.DIVIDE,
                scByValue = 2
            )
        )
    )
}

private fun createHalveItSteps(): List<GameModeStep> {
    return listOf(
        GameModeStep(1, gmsField = Field.FIFTEEN, gmsFieldType = FieldType.ALL),
        GameModeStep(2, gmsField = Field.SIXTEEN, gmsFieldType = FieldType.ALL),
        GameModeStep(3, gmsField = Field.ALL, gmsFieldType = FieldType.Double),
        GameModeStep(4, gmsField = Field.SEVENTEEN, gmsFieldType = FieldType.ALL),
        GameModeStep(5, gmsField = Field.EIGHTEEN, gmsFieldType = FieldType.ALL),
        GameModeStep(6, gmsField = Field.ALL, gmsFieldType = FieldType.Triple),
    )
}

private fun createShortHalveItSteps(): List<GameModeStep> {
    return listOf(
        GameModeStep(4, gmsField = Field.NINETEEN, gmsFieldType = FieldType.ALL),
        GameModeStep(5, gmsField = Field.TWENTY, gmsFieldType = FieldType.ALL),
        GameModeStep(6, gmsField = Field.ALL, gmsFieldType = FieldType.Triple),
    )
}
