package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lohni.darts.room.enums.ScoreModifier
import com.lohni.darts.room.enums.ScoreType

@Entity(tableName = "score_calculation")
data class ScoreCalculation(
    @ColumnInfo(name = "sc_id") @PrimaryKey(autoGenerate = true) var scId: Int = 0,
    @ColumnInfo(name = "sc_by_type", defaultValue = "0") var scByType: ScoreType = ScoreType.POINT,
    @ColumnInfo(
        name = "sc_by_type_modifier",
        defaultValue = "1"
    ) var scByTypeModifier: ScoreModifier = ScoreModifier.ADD,
    @ColumnInfo(name = "sc_by_value", defaultValue = "0") var scByValue: Int = 0,
    @ColumnInfo(
        name = "sc_by_value_modifier",
        defaultValue = "1"
    ) var scByValueModifier: ScoreModifier = ScoreModifier.ADD
)

fun ScoreCalculation.calculateScore(
    score: Float,
    scoredPoints: Float,
    darts: Float
): Float {
    val typeValue = if (scByType == ScoreType.POINT) scoredPoints else darts

    val newScore = if (
        (scByTypeModifier == ScoreModifier.ADD || scByTypeModifier == ScoreModifier.SUBTRACT)
        && (scByValueModifier == ScoreModifier.MULTIPLY || scByValueModifier == ScoreModifier.DIVIDE)
    ) {
        scByTypeModifier.calc.invoke(
            score,
            scByValueModifier.calc.invoke(typeValue, scByValue.toFloat())
        )
    } else {
        scByValueModifier.calc.invoke(
            scByTypeModifier.calc.invoke(score, typeValue),
            scByValue.toFloat()
        )
    }

    return newScore - score
}
