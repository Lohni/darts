package com.lohni.darts.game

import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType

class TurnState(val player: Player, val step: GameModeStep? = null) {
    var finished = false
    var turnScore = 0f
    var throwOne: Throw? = null
    var throwTwo: Throw? = null
    var throwThree: Throw? = null
    var busted = false

    fun addThrow(t: Throw) {
        if (throwOne == null) throwOne = t
        else if (throwTwo == null) throwTwo = t
        else throwThree = t

        if (t.tBust) {
            if (throwTwo == null) {
                throwTwo = Throw(
                    tPlayer = player.pId,
                    tTurn = t.tTurn,
                    tOrdinal = throwOne!!.tOrdinal + 1,
                    tField = Field.ZERO,
                    tFieldType = FieldType.Single,
                    tScore = 0f,
                    tBust = true
                )
            }
            if (throwThree == null) {
                throwThree = Throw(
                    tPlayer = player.pId,
                    tTurn = t.tTurn,
                    tOrdinal = throwOne!!.tOrdinal + 2,
                    tField = Field.ZERO,
                    tFieldType = FieldType.Single,
                    tScore = 0f,
                    tBust = true
                )
            }
            busted = true
        } else {
            turnScore += t.tScore
        }

        finished = throwOne != null && throwTwo != null && throwThree != null
    }

    fun undo(): Float {
        if (getThrowsAsList().isEmpty()) return -1f

        val undoneThrow = undoThrow()
        val isBust = undoneThrow.tBust

        if (isBust) undoBustThrows()

        turnScore = getThrowsAsList().sumOf { it.tScore.toDouble() }.toFloat()

        return undoneThrow.tScore
    }

    fun countDarts(): Int {
        return getThrowsAsList().count()
    }

    fun getLastThrow(): Throw? {
        return getThrowsAsList().reversed().firstOrNull()
    }

    fun getThrowsAsList(): List<Throw> {
        val list = mutableListOf<Throw>()
        throwOne?.let { list.add(it) }
        throwTwo?.let { list.add(it) }
        throwThree?.let { list.add(it) }
        return list
    }

    private fun undoThrow(): Throw {
        val toUndo: Throw?
        if (throwThree != null) {
            toUndo = throwThree
            throwThree = null
        } else if (throwTwo != null) {
            toUndo = throwTwo
            throwTwo = null
        } else {
            toUndo = throwOne
            throwOne = null
        }
        return toUndo!!
    }

    private fun undoBustThrows() {
        if (throwTwo != null && throwTwo!!.tBust) throwTwo = null
        if (throwOne != null && throwOne!!.tBust) throwOne = null
    }
}