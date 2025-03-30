package com.lohni.darts.game_state

import com.lohni.darts.data.assertUiState
import com.lohni.darts.data.createClassicGameConfiguration
import com.lohni.darts.data.createPlayers
import com.lohni.darts.data.finishTurn301
import com.lohni.darts.game.GameState
import com.lohni.darts.room.enums.FieldType
import org.junit.Assert
import org.junit.Test

class GameStateSetTest {

    @Test
    fun testOneSet3LegsOnePlayer() {
        var winningPlayer: Int? = null
        val gameState = GameState(
            1,
            3,
            createClassicGameConfiguration(),
            createPlayers(1),
            { _, p -> winningPlayer = p.pId })

        gameState.getGameView().assertUiState(0, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 1, 0)
        gameState.getGameView().assertUiState(0, 301f, legsWon = 1, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 1, 0)
        gameState.getGameView().assertUiState(0, 301f, legsWon = 2, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 1, 0)
        Assert.assertEquals(0, winningPlayer)
    }

    @Test
    fun testOneSet3LegsTwoPlayer() {
        var winningPlayer: Int? = null
        val gameState = GameState(
            1,
            3,
            createClassicGameConfiguration(),
            createPlayers(2),
            { _, p -> winningPlayer = p.pId })

        gameState.getGameView().assertUiState(0, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 0)
        gameState.getGameView().assertUiState(1, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 0)
        gameState.getGameView().assertUiState(0, 301f, legsWon = 2, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 1)
        gameState.getGameView().assertUiState(1, 301f, legsWon = 1, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 1)
        gameState.getGameView().assertUiState(0, 301f, legsWon = 2, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 1)
        Assert.assertEquals(1, winningPlayer)
    }

    @Test
    fun testOneSet3LegsThreePlayer() {
        var winningPlayer: Int? = null
        val gameState = GameState(
            1,
            3,
            createClassicGameConfiguration(),
            createPlayers(3),
            { _, p -> winningPlayer = p.pId })

        gameState.getGameView().assertUiState(0, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 3, 0)
        gameState.getGameView().assertUiState(1, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 3, 0)
        gameState.getGameView().assertUiState(2, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 3, 1)
        gameState.getGameView().assertUiState(0, 301f, legsWon = 2, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 3, 2)
        gameState.getGameView().assertUiState(1, 301f, legsWon = 1, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 3, 2)
        gameState.getGameView().assertUiState(2, 301f, legsWon = 2, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 3, 0)
        Assert.assertEquals(0, winningPlayer)
    }

    @Test
    fun test2Set2LegsTwoPlayer() {
        var winningPlayer: Int? = null
        val gameState = GameState(
            2,
            2,
            createClassicGameConfiguration(),
            createPlayers(2),
            { _, p -> winningPlayer = p.pId })

        gameState.getGameView().assertUiState(0, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 0)
        gameState.getGameView().assertUiState(1, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 1)
        gameState.getGameView().assertUiState(0, 301f, legsWon = 1, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 1)
        //Next Set
        gameState.getGameView().assertUiState(1, 301f, legsWon = 0, setsWon = 1)
        finishTurn301(gameState, FieldType.Double, 2, 1)
        gameState.getGameView().assertUiState(0, 301f, legsWon = 0, setsWon = 0)
        finishTurn301(gameState, FieldType.Double, 2, 0)
        gameState.getGameView().assertUiState(1, 301f, legsWon = 1, setsWon = 1)
        finishTurn301(gameState, FieldType.Double, 2, 0)
        //Next Set
        gameState.getGameView().assertUiState(0, 301f, legsWon = 0, setsWon = 1)
        finishTurn301(gameState, FieldType.Double, 2, 1)
        gameState.getGameView().assertUiState(1, 301f, legsWon = 1, setsWon = 1)
        finishTurn301(gameState, FieldType.Double, 2, 1)
        Assert.assertEquals(1, winningPlayer)
    }
}