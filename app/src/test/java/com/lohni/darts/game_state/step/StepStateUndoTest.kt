package com.lohni.darts.game_state.step

import com.lohni.darts.data.assertUiState
import com.lohni.darts.data.createGameConfiguration
import com.lohni.darts.data.createShortHalveIt
import com.lohni.darts.game.StepState
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.StepWinCondition
import org.junit.Assert
import org.junit.Test

class StepStateUndoTest {
    @Test
    fun testStepStateUndoHalveItOnePlayer() {
        var winner = -1
        val state = StepState(
            0,
            createGameConfiguration(),
            0,
            listOf(Player(1, "Test")),
            onFinish = { ls, p -> winner = p.pId }
        )

        state.getGameView().assertUiState(1, 40f)

        state.onThrow(Field.FIFTEEN, FieldType.Single)
        state.onThrow(Field.FIFTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 70f)

        state.undo()
        state.getGameView().assertUiState(1, 70f, 15f, 15f)
        state.undo()
        state.getGameView().assertUiState(1, 55f, 15f)

        state.onThrow(Field.FIFTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 70f)

        state.onThrow(Field.SIXTEEN, FieldType.Single)
        state.onThrow(Field.FIFTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 86f)

        //halve it
        state.onThrow(Field.SIXTEEN, FieldType.Single)
        state.onThrow(Field.FIFTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 43f)

        state.undo()
        state.getGameView().assertUiState(1, 86f, 0f, 0f)
        state.undo()
        state.getGameView().assertUiState(1, 86f, 0f)

        state.onThrow(Field.FIFTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 43f)

        state.onThrow(Field.SEVENTEEN, FieldType.Triple)
        state.onThrow(Field.FIFTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 94f)

        state.onThrow(Field.SEVENTEEN, FieldType.Triple)
        state.onThrow(Field.EIGHTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 112f)

        state.undo()
        state.undo()
        state.undo()
        state.getGameView().assertUiState(1, 94f)

        state.onThrow(Field.SEVENTEEN, FieldType.Triple)
        state.onThrow(Field.EIGHTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 112f)

        //halve it
        state.onThrow(Field.SEVENTEEN, FieldType.Double)
        state.onThrow(Field.EIGHTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 56f, 0f, 0f, -56f)

        Assert.assertEquals(1, winner)
    }

    @Test
    fun testStepStateUndoHalveItThreePlayer() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(winCondition = StepWinCondition.HIGHEST_SCORE),
            0,
            listOf(Player(1, "Test"), Player(2, "Test2"), Player(3, "Test3")),
            onFinish = { ls, p -> winner = p.pId }
        )

        state.getGameView().assertUiState(1, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(2, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(3, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.undo()
        state.getGameView().assertUiState(3, 78f, 19f, 19f)
        state.undo()
        state.getGameView().assertUiState(3, 59f, 19f)
        state.undo()
        state.getGameView().assertUiState(3, 40f)
        state.undo()

        state.getGameView().assertUiState(2, 78f, 19f, 19f)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(3, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(1, 78f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(2, 78f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.ONE, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(3, 78f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.ONE, FieldType.Single)
        state.onThrow(Field.FIVE, FieldType.Single)

        state.getGameView().assertUiState(1, 138f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(2, 118f)
        state.undo()
        state.undo()
        state.getGameView().assertUiState(1, 138f, 0f)
        state.undo()
        state.undo()

        state.getGameView().assertUiState(3, 98f, 20f, 0f)
        state.onThrow(Field.FIVE, FieldType.Single)

        state.getGameView().assertUiState(1, 138f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(2, 118f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.ONE, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(3, 98f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.ONE, FieldType.Single)
        state.onThrow(Field.FIVE, FieldType.Single)

        Assert.assertEquals(1, winner)
    }

    @Test
    fun testStepStateUndoHalveItTwoPlayersImmediateProceed() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(
                winCondition = StepWinCondition.FIRST_TO_FINISH,
                immediatelyProceed = true
            ),
            0,
            listOf(Player(1, "Test"), Player(2, "Test2")),
            onFinish = { ls, p -> winner = p.pId }
        )

        state.getGameView().assertUiState(1, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.undo()
        state.getGameView().assertUiState(1, 59f, 19f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.getGameView().assertUiState(1, 59f, 19f, 0f)
        state.onThrow(Field.NINETEEN, FieldType.Single)

        state.getGameView().assertUiState(2, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(1, 29.5f)
        state.onThrow(Field.NINETEEN, FieldType.Triple)

        Assert.assertEquals(1, winner)
    }

    @Test
    fun testStepStateUndoHalveItTwoPlayersImmediateProceedAndRepeatOnFailure() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(
                winCondition = StepWinCondition.FIRST_TO_FINISH,
                immediatelyProceed = true,
                repeatOnFail = true
            ),
            0,
            listOf(Player(1, "Test"), Player(2, "Test2")),
            onFinish = { ls, p -> winner = p.pId }
        )

        state.getGameView().assertUiState(1, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single) //Halves

        state.getGameView().assertUiState(2, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)

        state.undo()
        state.getGameView().assertUiState(2, 40f)

        state.undo()
        state.getGameView().assertUiState(1, 79f, 19f, 20f)
        state.undo()
        state.getGameView().assertUiState(1, 59f, 19f)
        state.undo()
        state.getGameView().assertUiState(1, 40f)
        state.undo()
        state.getGameView().assertUiState(1, 40f)

        state.getGameView().assertUiState(1, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(2, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single) //Halves it

        state.getGameView().assertUiState(1, 39.5f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(2, 29.5f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single) //Halves it

        state.getGameView().assertUiState(1, 19.75f)
        state.onThrow(Field.NINETEEN, FieldType.Triple)

        Assert.assertEquals(1, winner)
    }

    @Test
    fun testStepStateUndoHalveItTwoPlayersRepeatOnFailure() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(
                winCondition = StepWinCondition.FIRST_TO_FINISH,
                repeatOnFail = true
            ),
            0,
            listOf(Player(1, "Test"), Player(2, "Test2")),
            onFinish = { ls, p -> winner = p.pId }
        )

        state.getGameView().assertUiState(1, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(2, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(1, 59f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(2, 59f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(1, 29.5f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(2, 29.5f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(1, 14.75f)
        state.undo()
        state.undo()
        state.undo()
        state.getGameView().assertUiState(2, 29.5f)
        state.undo()
        state.undo()
        state.undo()
        state.getGameView().assertUiState(1, 29.5f)
        state.undo()
        state.undo()
        state.undo()
        state.getGameView().assertUiState(2, 59f)
        state.undo()
        state.undo()

        state.getGameView().assertUiState(1, 59f, 0f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(2, 59f)
        state.undo()
        state.getGameView().assertUiState(1, 59f, 0f, 0f)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(2, 59f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(1, 79f)
        state.onThrow(Field.NINETEEN, FieldType.Triple)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        Assert.assertEquals(1, winner)
    }
}