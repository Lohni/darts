package com.lohni.darts.checkout

import com.lohni.darts.data.checkoutMap
import com.lohni.darts.data.createPlayerView
import com.lohni.darts.game.CheckoutState
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import org.junit.Assert
import org.junit.Test

class CheckoutStateStepTest {

    @Test
    fun testStepSuggestionDoubleNumber() {
        val checkoutState = CheckoutState(
            emptyMap(),
            checkoutMap(),
            listOf(GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.Double))
        )
        checkoutState.getSuggestion(
            createPlayerView(
                8f,
                fieldOne = Field.SIXTEEN,
                fieldTypeOne = FieldType.Single,
                fieldTwo = Field.EIGHT,
                fieldTypeTwo = FieldType.Single,
                stepRequirement = "D20"
            )
        ).assert("D20", "D20", "D20")
    }

    @Test
    fun testStepSuggestionSingleNumber() {
        val checkoutState = CheckoutState(
            emptyMap(),
            checkoutMap(),
            listOf(GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.Single))
        )
        checkoutState.getSuggestion(
            createPlayerView(
                8f,
                fieldOne = Field.SIXTEEN,
                fieldTypeOne = FieldType.Single,
                fieldTwo = Field.EIGHT,
                fieldTypeTwo = FieldType.Single,
                stepRequirement = "20"
            )
        ).assert("20", "20", "20")
    }

    @Test
    fun testStepSuggestionAllNumber() {
        val checkoutState = CheckoutState(
            emptyMap(),
            checkoutMap(),
            listOf(GameModeStep(gmsField = Field.TWENTY, gmsFieldType = FieldType.ALL))
        )
        checkoutState.getSuggestion(
            createPlayerView(
                8f,
                fieldOne = Field.SIXTEEN,
                fieldTypeOne = FieldType.Single,
                fieldTwo = Field.EIGHT,
                fieldTypeTwo = FieldType.Single,
                stepRequirement = "A20"
            )
        ).assert("A20", "A20", "A20")
    }

    @Test
    fun testStepSuggestionSingleAll() {
        val checkoutState = CheckoutState(
            emptyMap(),
            checkoutMap(),
            listOf(GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Single))
        )
        checkoutState.getSuggestion(
            createPlayerView(
                8f,
                fieldOne = Field.SIXTEEN,
                fieldTypeOne = FieldType.Single,
                fieldTwo = Field.EIGHT,
                fieldTypeTwo = FieldType.Single,
                stepRequirement = "SAll"
            )
        ).assert("SAll", "SAll", "SAll")
    }

    @Test
    fun testStepSuggestionProcceedStepOnSuccess() {
        val checkoutState = CheckoutState(
            emptyMap(),
            checkoutMap(),
            listOf(
                GameModeStep(gmsId = 0, gmsField = Field.TWENTY, gmsFieldType = FieldType.ALL),
                GameModeStep(gmsId = 1, gmsField = Field.ALL, gmsFieldType = FieldType.ALL)
            )
        )
        checkoutState.getSuggestion(
            createPlayerView(
                8f,
                fieldOne = Field.NINETEEN,
                fieldTypeOne = FieldType.Single,
                fieldTwo = Field.TWENTY,
                fieldTypeTwo = FieldType.Single,
                stepRequirement = "All"
            )
        ).assert("A20", "A20", "All")
    }

    @Test
    fun testStepSuggestionDoubleAll() {
        val checkoutState = CheckoutState(
            emptyMap(),
            checkoutMap(),
            listOf(GameModeStep(gmsField = Field.ALL, gmsFieldType = FieldType.Double))
        )
        checkoutState.getSuggestion(
            createPlayerView(
                8f,
                fieldOne = Field.SIXTEEN,
                fieldTypeOne = FieldType.Single,
                fieldTwo = Field.EIGHT,
                fieldTypeTwo = FieldType.Single,
                stepRequirement = "DAll"
            )
        ).assert("DAll", "DAll", "DAll")
    }

    private fun CheckoutState.CheckoutSuggestion.assert(
        s1: String,
        s2: String,
        s3: String
    ) {
        Assert.assertEquals(s1, this.first)
        Assert.assertEquals(s2, this.second)
        Assert.assertEquals(s3, this.third)
    }
}