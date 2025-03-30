package com.lohni.darts.game_state.classic

import com.lohni.darts.data.assertUiState
import com.lohni.darts.data.createClassicGameConfiguration
import com.lohni.darts.data.createPlayers
import com.lohni.darts.game.GameState
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import org.junit.Assert
import org.junit.Test

class ClassicStateScoreTest {
    @Test
    fun twoPlayerAllOut301() {
        var winningPlayer: Int? = null
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(),
            createPlayers(2),
            { _, p -> winningPlayer = p.pId })

        //Player0
        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player1
        gameState.getGameView().assertUiState(1, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player0
        gameState.getGameView().assertUiState(0, 201f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Double)
        gameState.getGameView().assertUiState(0, 101f, 60f, 40f, null, 0, 0)
        gameState.onThrow(Field.ZERO, FieldType.Single)
        //Player1
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player0
        gameState.onThrow(Field.SEVENTEEN, FieldType.Triple)
        gameState.onThrow(Field.TWENTYFIVE, FieldType.Double)
        Assert.assertEquals(0, winningPlayer)
    }

    @Test
    fun twoPlayerDoubleOutScore1Bust301() {
        var winningPlayer: Int? = null
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(checkOut = FieldType.Double),
            createPlayers(2),
            { _, p -> winningPlayer = p.pId })

        //Player0
        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player1
        gameState.getGameView().assertUiState(1, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player0
        gameState.getGameView().assertUiState(0, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.ZERO, FieldType.Single)
        //Player1
        gameState.getGameView().assertUiState(1, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player0
        gameState.getGameView().assertUiState(0, 81f)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player1
        gameState.getGameView().assertUiState(1, 121f)
        gameState.onThrow(Field.SEVENTEEN, FieldType.Triple)
        gameState.onThrow(Field.TEN, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Double)
        Assert.assertEquals(1, winningPlayer)
    }

    @Test
    fun twoPlayerDoubleOutScoreFieldTypeBust301() {
        var winningPlayer: Int? = null
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(checkOut = FieldType.Double),
            createPlayers(2),
            { _, p -> winningPlayer = p.pId })

        //Player0
        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player1
        gameState.getGameView().assertUiState(1, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player0
        gameState.getGameView().assertUiState(0, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player1
        gameState.getGameView().assertUiState(1, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player0
        gameState.getGameView().assertUiState(0, 21f)
        gameState.onThrow(Field.FIVE, FieldType.Single)
        gameState.onThrow(Field.FIFTEEN, FieldType.Single)
        Assert.assertNull(winningPlayer)
        //Player1
        gameState.getGameView().assertUiState(1, 61f)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.ONE, FieldType.Single)
        gameState.onThrow(Field.FIVE, FieldType.Single)
        //Player0 - busted before
        gameState.getGameView().assertUiState(0, 21f)
        gameState.onThrow(Field.FIVE, FieldType.Single)
        gameState.onThrow(Field.EIGHT, FieldType.Double)
        Assert.assertEquals(0, winningPlayer)
    }

    @Test
    fun bustOvershotOnePlayer() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(),
            createPlayers(1),
            { _, _ -> })

        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.getGameView().assertUiState(0, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.getGameView().assertUiState(0, 121f)
    }

    @Test
    fun bustOvershotDoubleOutOnePlayer() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(),
            createPlayers(1),
            { _, _ -> })

        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.getGameView().assertUiState(0, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.NINETEEN, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.getGameView().assertUiState(0, 121f)
    }

    @Test
    fun bustOneRemainingDoubleOut() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(checkOut = FieldType.Double),
            createPlayers(1),
            { _, _ -> })

        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.getGameView().assertUiState(0, 121f)
        //Bust 1
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.getGameView().assertUiState(0, 121f)

        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Double)
        gameState.onThrow(Field.NINETEEN, FieldType.Single)
        gameState.getGameView().assertUiState(0, 2f)
    }

    @Test
    fun bustOneAndTwoRemainingTripleOut() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(checkOut = FieldType.Triple),
            createPlayers(1),
            { _, _ -> })

        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.getGameView().assertUiState(0, 121f)
        //Bust 1
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.getGameView().assertUiState(0, 121f)
        //Bust 2
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Double)
        gameState.onThrow(Field.NINETEEN, FieldType.Single)
        gameState.getGameView().assertUiState(0, 121f)

        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Double)
        gameState.onThrow(Field.EIGHTEEN, FieldType.Single)
        gameState.getGameView().assertUiState(0, 3f)
    }
}