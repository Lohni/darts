package com.lohni.darts.game

import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.shortString
import com.lohni.darts.room.entities.stepCount
import com.lohni.darts.room.entities.toArray
import com.lohni.darts.ui.view.PlayerView
import com.lohni.darts.ui.view.getThrows

class CheckoutState(
    private val customCheckouts: Map<Int, Map<Float, CheckoutTable>>,
    private val defaultCheckouts: Map<Float, CheckoutTable>,
    private val steps: List<GameModeStep>
) {
    private val defaultSuggestion = arrayOf("", "", "")

    fun getSuggestion(playerView: PlayerView?): CheckoutSuggestion {
        return playerView?.let { view ->
            if (steps.isNotEmpty()) {
                return getSuggestionForSteps(view)
            }
            return getSuggestionForClassic(view)
        } ?: CheckoutSuggestion("-", "-", "-")
    }

    private fun getSuggestionForSteps(view: PlayerView): CheckoutSuggestion {
        val playerThrows = view.getThrows()
        val numberOfThrows = playerThrows.size
        val suggestionArray = defaultSuggestion
        playerThrows.forEachIndexed { idx, t ->
            val step = steps.first { it.gmsId == t.tGameModeStep }
            suggestionArray[idx] = step.shortString()
        }
        repeat(3 - numberOfThrows) {
            suggestionArray[numberOfThrows + it] = view.stepRequirement
        }
        return CheckoutSuggestion(suggestionArray[0], suggestionArray[1], suggestionArray[2])
    }

    private fun getSuggestionForClassic(view: PlayerView): CheckoutSuggestion {
        var currentScore = view.score
        val tThrows = view.getThrows()
        currentScore += tThrows.sumOf { it.tScore.toDouble() }.toFloat()

        val suggestionArray = defaultSuggestion.copyOf()
        var baseSuggestion =
            getCheckoutTable(3, currentScore, view.player.pId).toArray()
        var idxBase = 0
        tThrows.forEachIndexed { idx, t ->
            suggestionArray[idx] = baseSuggestion[idxBase]
            currentScore -= t.tScore
            if (t.shortString() != baseSuggestion[idx]) {
                baseSuggestion =
                    getCheckoutTable(3 - (idx + 1), currentScore, view.player.pId).toArray()
                idxBase = 0
            } else {
                idxBase += 1
            }
        }
        (0..<3 - tThrows.size).forEach {
            suggestionArray[tThrows.size + it] = baseSuggestion[idxBase + it]
        }
        return CheckoutSuggestion(suggestionArray[0], suggestionArray[1], suggestionArray[2])
    }

    private fun getCheckoutTable(availableThrows: Int, score: Float, pId: Int): CheckoutTable {
        var checkout = customCheckouts[pId]?.let { it[score] }
        if (checkout == null || checkout.stepCount() > availableThrows) {
            checkout = defaultCheckouts[score]
        }
        return if (checkout == null || checkout.stepCount() > availableThrows) CheckoutTable() else checkout
    }

    data class CheckoutSuggestion(
        val first: String,
        val second: String,
        val third: String
    )
}
