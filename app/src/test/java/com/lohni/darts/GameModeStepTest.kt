package com.lohni.darts

import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.requirementMet
import com.lohni.darts.room.entities.shortString
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import org.junit.Assert
import org.junit.Test

class GameModeStepTest {

    @Test
    fun testRequirementMetTrue() {
        Assert.assertTrue(
            GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.ALL)
                .requirementMet(Field.TWENTY, FieldType.Triple)
        )
        Assert.assertTrue(
            GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.ALL)
                .requirementMet(Field.ONE, FieldType.Single)
        )
        Assert.assertTrue(
            GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.ALL)
                .requirementMet(Field.TWENTY, FieldType.Triple)
        )
        Assert.assertTrue(
            GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.ALL)
                .requirementMet(Field.TWENTY, FieldType.Single)
        )
        Assert.assertTrue(
            GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Double)
                .requirementMet(Field.TWENTY, FieldType.Double)
        )
        Assert.assertTrue(
            GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Double)
                .requirementMet(Field.TWENTYFIVE, FieldType.Double)
        )
        Assert.assertTrue(
            GameModeStep(gmsField = Field.FIFTEEN, gmsFieldType = FieldType.Double)
                .requirementMet(Field.FIFTEEN, FieldType.Double)
        )
        Assert.assertTrue(
            GameModeStep(gmsField = Field.TWENTYFIVE, gmsFieldType = FieldType.Double)
                .requirementMet(Field.TWENTYFIVE, FieldType.Double)
        )
    }

    @Test
    fun testRequirementMetFalse() {
        Assert.assertFalse(
            GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.ALL)
                .requirementMet(Field.NINETEEN, FieldType.Triple)
        )
        Assert.assertFalse(
            GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.ALL)
                .requirementMet(Field.TEN, FieldType.ALL)
        )
        Assert.assertFalse(
            GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Double)
                .requirementMet(Field.TWENTY, FieldType.Single)
        )
        Assert.assertFalse(
            GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Double)
                .requirementMet(Field.TWENTYFIVE, FieldType.Triple)
        )
        Assert.assertFalse(
            GameModeStep(gmsField = Field.FIFTEEN, gmsFieldType = FieldType.Double)
                .requirementMet(Field.FOURTEEN, FieldType.Double)
        )
        Assert.assertFalse(
            GameModeStep(gmsField = Field.TWENTYFIVE, gmsFieldType = FieldType.Double)
                .requirementMet(Field.TWENTYFIVE, FieldType.Single)
        )
    }

    @Test
    fun testShortString() {
        GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Triple)
            .shortString().assert("TAll")
        GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Double)
            .shortString().assert("DAll")
        GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Single)
            .shortString().assert("SAll")
        GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.ALL)
            .shortString().assert("All")
        GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.ALL)
            .shortString().assert("A20")
        GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.Single)
            .shortString().assert("20")
        GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.Double)
            .shortString().assert("D20")
        GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.Triple)
            .shortString().assert("T20")
    }
}

fun String.assert(expected: String) {
    Assert.assertEquals(expected, this)
}