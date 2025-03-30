package com.lohni.darts.game_state.classic

import com.lohni.darts.data.assertUiState
import com.lohni.darts.data.createClassicGameConfiguration
import com.lohni.darts.data.createPlayers
import com.lohni.darts.game.GameState
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import org.junit.Test

class ClassicStateUndoTest {
    @Test
    fun undoOnePayer() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(),
            createPlayers(1),
            { _, _ -> })

        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.getGameView().assertUiState(0, 201f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.getGameView().assertUiState(0, 101f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.getGameView().assertUiState(0, 1f)

        gameState.undo()
        gameState.getGameView().assertUiState(0, 21f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 41f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 101f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 121f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 141f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 201f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 221f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
    }

    @Test
    fun undoTwoPlayers() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(checkOut = FieldType.Double),
            createPlayers(2),
            { _, _ -> })

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
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player0
        gameState.getGameView().assertUiState(0, 21f)
        gameState.onThrow(Field.ONE, FieldType.Single)
        gameState.onThrow(Field.SIX, FieldType.Single)
        gameState.onThrow(Field.SEVEN, FieldType.Single)
        gameState.getGameView().assertUiState(1, 21f)

        gameState.undo()
        gameState.getGameView().assertUiState(0, 14f, 1f, 6f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 20f, 1f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 21f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 41f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 41f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 181f, 60f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 301f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 181f, 60f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
    }

    @Test
    fun undoThreePlayers() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(),
            createPlayers(3),
            { _, _ -> })
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
        //Player2
        gameState.getGameView().assertUiState(2, 301f)
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
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player2
        gameState.getGameView().assertUiState(2, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player0
        gameState.getGameView().assertUiState(0, 21f)
        gameState.onThrow(Field.ONE, FieldType.Single)
        gameState.getGameView().assertUiState(0, 20f, 1f)

        gameState.undo()
        gameState.getGameView().assertUiState(0, 21f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 41f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 41f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 41f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 181f, 60f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 301f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 181f, 60f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 301f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 181f, 60f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
    }

    @Test
    fun undoOnePlayersWithBust() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(checkOut = FieldType.Double),
            createPlayers(1),
            { _, _ -> })

        gameState.getGameView().assertUiState(0, 301f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.getGameView().assertUiState(0, 201f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.getGameView().assertUiState(0, 101f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Double)
        gameState.getGameView().assertUiState(0, 101f)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.getGameView().assertUiState(0, 41f)
        gameState.onThrow(Field.TWENTYFIVE, FieldType.Double)
        gameState.getGameView().assertUiState(0, 41f)
        gameState.onThrow(Field.TWENTY, FieldType.Single)

        gameState.undo()
        gameState.getGameView().assertUiState(0, 41f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 41f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 61f, 20f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 81f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 101f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 41f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 101f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 121f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 141f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 201f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 221f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
    }

    @Test
    fun undoTwoPlayersWithBust() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(checkOut = FieldType.Double),
            createPlayers(2),
            { _, _ -> })

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
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player1
        gameState.getGameView().assertUiState(1, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Double)
        //Player0
        gameState.getGameView().assertUiState(0, 121f)

        gameState.undo()
        gameState.getGameView().assertUiState(1, 41f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 181f, 60f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 301f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 181f, 60f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 241f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
    }

    @Test
    fun undoThreePlayersWithBust() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(),
            createPlayers(3),
            { _, _ -> })
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
        //Player2
        gameState.getGameView().assertUiState(2, 301f)
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
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player2
        gameState.getGameView().assertUiState(2, 121f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player0
        gameState.getGameView().assertUiState(0, 21f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player1
        gameState.getGameView().assertUiState(1, 21f)
        gameState.onThrow(Field.FIVE, FieldType.Single)
        gameState.onThrow(Field.TWENTY, FieldType.Single)
        //Player2
        gameState.getGameView().assertUiState(2, 21f)
        gameState.onThrow(Field.FIVE, FieldType.Single)
        gameState.onThrow(Field.EIGHT, FieldType.Single)
        gameState.onThrow(Field.EIGHTEEN, FieldType.Single)
        //Player0
        gameState.getGameView().assertUiState(0, 21f)

        gameState.undo()
        gameState.getGameView().assertUiState(2, 8f, 5f, 8f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 16f, 5f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 21f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 16f, 5f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 21f)
        gameState.undo()
        gameState.getGameView().assertUiState(0, 21f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 41f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(2, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 41f, 60f, 20f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 61f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 121f)
        gameState.undo()
        gameState.undo()
        gameState.undo()
        gameState.getGameView().assertUiState(0, 121f)
        gameState.undo()
        gameState.undo()
        gameState.undo()
        gameState.getGameView().assertUiState(2, 301f)
        gameState.undo()
        gameState.undo()
        gameState.undo()
        gameState.getGameView().assertUiState(1, 301f)
        gameState.undo()
        gameState.undo()
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
        gameState.undo()
        gameState.undo()
        gameState.undo()
        gameState.getGameView().assertUiState(0, 301f)
    }

    @Test
    fun undoTwoPlayersAndResume() {
        val gameState = GameState(
            1,
            1,
            createClassicGameConfiguration(checkOut = FieldType.Double),
            createPlayers(2),
            { _, _ -> })

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

        gameState.undo()
        gameState.getGameView().assertUiState(0, 121f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 181f, 60f, 60f)
        gameState.undo()
        gameState.getGameView().assertUiState(1, 241f, 60f)

        //Player1
        gameState.getGameView().assertUiState(1, 241f, 60f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)

        //Player0
        gameState.getGameView().assertUiState(0, 121f)
    }
}