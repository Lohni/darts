package com.lohni.darts

import com.lohni.darts.room.entities.ScoreCalculation
import com.lohni.darts.room.entities.calculateScore
import com.lohni.darts.room.enums.ScoreModifier
import com.lohni.darts.room.enums.ScoreType
import org.junit.Assert
import org.junit.Test

class ScoreCalculationTest {

    @Test
    fun testBasicCalculationAdd() {
        Assert.assertEquals(
            20f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.ADD,
                scByValue = 0,
                scByValueModifier = ScoreModifier.ADD
            ).calculateScore(0f, 20f, 3f)
        )
        Assert.assertEquals(
            22f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.ADD,
                scByValue = 2,
                scByValueModifier = ScoreModifier.ADD
            ).calculateScore(0f, 20f, 3f)
        )
        Assert.assertEquals(
            20f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.ADD,
                scByValue = 0,
                scByValueModifier = ScoreModifier.ADD
            ).calculateScore(20f, 20f, 3f)
        )
    }

    @Test
    fun testNoModifier() {
        Assert.assertEquals(
            20f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.NONE,
                scByValue = 20,
                scByValueModifier = ScoreModifier.ADD
            ).calculateScore(0f, 20f, 3f)
        )
        Assert.assertEquals(
            -2f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.NONE,
                scByValue = 2,
                scByValueModifier = ScoreModifier.SUBTRACT
            ).calculateScore(0f, 20f, 3f)
        )
        Assert.assertEquals(
            0f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.NONE,
                scByValue = 2,
                scByValueModifier = ScoreModifier.MULTIPLY
            ).calculateScore(0f, 20f, 3f)
        )
        Assert.assertEquals(
            -10f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.NONE,
                scByValue = 2,
                scByValueModifier = ScoreModifier.DIVIDE
            ).calculateScore(20f, 20f, 3f)
        )
    }

    @Test
    fun testNoValue() {
        Assert.assertEquals(
            20f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.ADD,
                scByValue = 20,
                scByValueModifier = ScoreModifier.NONE
            ).calculateScore(0f, 20f, 3f)
        )
        Assert.assertEquals(
            -20f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.SUBTRACT,
                scByValue = 2,
                scByValueModifier = ScoreModifier.NONE
            ).calculateScore(0f, 20f, 3f)
        )
        Assert.assertEquals(
            18f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.MULTIPLY,
                scByValue = 2,
                scByValueModifier = ScoreModifier.NONE
            ).calculateScore(2f, 10f, 3f)
        )
        Assert.assertEquals(
            -19f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.DIVIDE,
                scByValue = 2,
                scByValueModifier = ScoreModifier.NONE
            ).calculateScore(20f, 20f, 3f)
        )
    }

    @Test
    fun testTypeAndValue() {
        Assert.assertEquals(
            60f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.ADD,
                scByValue = 3,
                scByValueModifier = ScoreModifier.MULTIPLY
            ).calculateScore(0f, 20f, 3f)
        )
        Assert.assertEquals(
            40f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.MULTIPLY,
                scByValue = 2,
                scByValueModifier = ScoreModifier.DIVIDE
            ).calculateScore(10f, 10f, 3f)
        )
        Assert.assertEquals(
            16f,
            ScoreCalculation(
                scByType = ScoreType.POINT,
                scByTypeModifier = ScoreModifier.MULTIPLY,
                scByValue = 2,
                scByValueModifier = ScoreModifier.SUBTRACT
            ).calculateScore(2f, 10f, 3f)
        )
    }
}