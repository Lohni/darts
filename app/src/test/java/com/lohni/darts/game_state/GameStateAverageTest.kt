package com.lohni.darts.game_state

import com.lohni.darts.data.assertAverage
import com.lohni.darts.data.createClassicGameConfiguration
import com.lohni.darts.data.createPlayers
import com.lohni.darts.game.ClassicState
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import org.junit.Assert
import org.junit.Test

class GameStateAverageTest {
    @Test
    fun onePlayer301() {
        var winningPlayer: Int? = null
        val gameState = ClassicState(
            0,
            createClassicGameConfiguration(),
            0,
            createPlayers(1),
            onFinish = { ls, p -> winningPlayer = p.pId }
        )

        //Player0
        gameState.onThrow(Field.TWENTY, FieldType.Triple).assertAverage(0, 241f, 180f)
        gameState.onThrow(Field.TWENTY, FieldType.Single).assertAverage(0, 221f, 120f)
        gameState.onThrow(Field.TWENTY, FieldType.Single).assertAverage(0, 201f, 100f)
        //Player0
        gameState.onThrow(Field.TWENTY, FieldType.Triple).assertAverage(0, 141f, 120f)
        gameState.onThrow(Field.TWENTY, FieldType.Double).assertAverage(0, 101f, 120f)
        gameState.onThrow(Field.ZERO, FieldType.Single).assertAverage(0, 101f, 100f)
        //Player0
        gameState.onThrow(Field.SEVENTEEN, FieldType.Triple).assertAverage(0, 50f, 107.57f)
        gameState.onThrow(Field.TWENTYFIVE, FieldType.Double).assertAverage(0, 0f, 112.88f)
        Assert.assertEquals(0, winningPlayer)
    }

    @Test
    fun twoPlayer301() {
        var winningPlayer: Int? = null
        val gameState = ClassicState(
            0,
            createClassicGameConfiguration(),
            0,
            createPlayers(2),
            onFinish = { ls, p -> winningPlayer = p.pId }
        )

        //Player0
        gameState.onThrow(Field.TWENTY, FieldType.Triple).assertAverage(0, 241f, 180f)
        gameState.onThrow(Field.TWENTY, FieldType.Single).assertAverage(0, 221f, 120f)
        gameState.onThrow(Field.TWENTY, FieldType.Single).assertAverage(0, 201f, 100f)
        //Player1
        gameState.onThrow(Field.TWENTY, FieldType.Triple).assertAverage(1, 241f, 180f)
        gameState.onThrow(Field.TWENTY, FieldType.Single).assertAverage(1, 221f, 120f)
        gameState.onThrow(Field.TWENTY, FieldType.Single).assertAverage(1, 201f, 100f)
        //Player0
        gameState.onThrow(Field.TWENTY, FieldType.Triple).assertAverage(0, 141f, 120f)
        gameState.onThrow(Field.TWENTY, FieldType.Double).assertAverage(0, 101f, 120f)
        gameState.onThrow(Field.ZERO, FieldType.Single).assertAverage(0, 101f, 100f)
        //Player1
        gameState.onThrow(Field.TWENTY, FieldType.Triple).assertAverage(1, 141f, 120f)
        gameState.onThrow(Field.TWENTY, FieldType.Double).assertAverage(1, 101f, 120f)
        gameState.onThrow(Field.ZERO, FieldType.Single).assertAverage(1, 101f, 100f)
        //Player0
        gameState.onThrow(Field.SEVENTEEN, FieldType.Triple).assertAverage(0, 50f, 107.57f)
        gameState.onThrow(Field.TWENTYFIVE, FieldType.Double).assertAverage(0, 0f, 112.88f)
        Assert.assertEquals(0, winningPlayer)
    }

    @Test
    fun onePlayer301WithBust() {
        var winningPlayer: Int? = null
        val gameState = ClassicState(
            0,
            createClassicGameConfiguration(),
            0,
            createPlayers(1),
            onFinish = { ls, p -> winningPlayer = p.pId }
        )

        //Player0
        gameState.onThrow(Field.TWENTY, FieldType.Triple).assertAverage(0, 241f, 180f)
        gameState.onThrow(Field.TWENTY, FieldType.Single).assertAverage(0, 221f, 120f)
        gameState.onThrow(Field.TWENTY, FieldType.Single).assertAverage(0, 201f, 100f)
        //Player0
        gameState.onThrow(Field.TWENTY, FieldType.Triple).assertAverage(0, 141f, 120f)
        gameState.onThrow(Field.TWENTY, FieldType.Double).assertAverage(0, 101f, 120f)
        gameState.onThrow(Field.ZERO, FieldType.Single).assertAverage(0, 101f, 100f)
        //Player0
        gameState.onThrow(Field.SEVENTEEN, FieldType.Triple).assertAverage(0, 50f, 107.57f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player0
        gameState.onThrow(Field.SEVENTEEN, FieldType.Triple).assertAverage(0, 50f, 75.3f)
        gameState.onThrow(Field.TWENTY, FieldType.Triple)
        //Player0
        gameState.onThrow(Field.SEVENTEEN, FieldType.Triple).assertAverage(0, 50f, 57.92f)
        gameState.onThrow(Field.TWENTYFIVE, FieldType.Double).assertAverage(0, 0f, 64.5f)
        Assert.assertEquals(0, winningPlayer)
    }
}