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

class StepStateTest {
    @Test
    fun testStepStateHalveItOnePlayer() {
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

        state.onThrow(Field.SIXTEEN, FieldType.Single)
        state.onThrow(Field.FIFTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 86f)

        //halve it
        state.onThrow(Field.SIXTEEN, FieldType.Single)
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

        //halve it
        state.onThrow(Field.SEVENTEEN, FieldType.Double)
        state.onThrow(Field.EIGHTEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)
        state.getGameView().assertUiState(1, 56f, 0f, 0f, -56f)

        Assert.assertEquals(1, winner)
    }

    @Test
    fun testStepStateHalveItThreePlayer() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(StepWinCondition.HIGHEST_SCORE),
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
    fun testStepStateHalveItThreePlayerFirstToFinish() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(StepWinCondition.FIRST_TO_FINISH),
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
        state.onThrow(Field.TEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(1, 78f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(2, 78f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.ONE, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(3, 59f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.ONE, FieldType.Single)
        state.onThrow(Field.FIVE, FieldType.Single)

        state.getGameView().assertUiState(1, 138f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        Assert.assertEquals(1, winner)
    }

    @Test
    fun testStepStateThreePlayerFirstToFinishRepeatOnFailure() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(StepWinCondition.FIRST_TO_FINISH, repeatOnFail = true),
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
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(3, 98f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(1, 69f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)

        state.getGameView().assertUiState(2, 59f)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.ONE, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Triple)

        Assert.assertEquals(2, winner)
    }

    @Test
    fun testStepStateThreePlayerFirstToFinishImmediatelyProceed() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(StepWinCondition.FIRST_TO_FINISH, immediatelyProceed = true),
            0,
            listOf(Player(1, "Test"), Player(2, "Test2"), Player(3, "Test3")),
            onFinish = { ls, p -> winner = p.pId }
        )

        state.getGameView().assertUiState(1, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        Assert.assertEquals(1, winner)
    }

    @Test
    fun testStepStateThreePlayerFirstToFinishImmediatelyProceedAndRepeatFail() {
        var winner = -1
        val state = StepState(
            0,
            createShortHalveIt(
                StepWinCondition.FIRST_TO_FINISH,
                immediatelyProceed = true,
                repeatOnFail = true
            ),
            0,
            listOf(Player(1, "Test"), Player(2, "Test2"), Player(3, "Test3")),
            onFinish = { ls, p -> winner = p.pId }
        )

        state.getGameView().assertUiState(1, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TWENTY, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(2, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(3, 40f)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.NINETEEN, FieldType.Single)
        state.onThrow(Field.TEN, FieldType.Single)

        state.getGameView().assertUiState(1, 39.5f)
        state.onThrow(Field.ONE, FieldType.Triple)

        Assert.assertEquals(1, winner)
    }
}