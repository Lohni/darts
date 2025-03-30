package com.lohni.darts.checkout

import com.lohni.darts.data.checkoutMap
import com.lohni.darts.data.createPlayerView
import com.lohni.darts.game.CheckoutState
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import org.junit.Assert
import org.junit.Test

class CheckoutStateClassicTest {

    @Test
    fun testNoThrows() {
        val checkoutState = CheckoutState(emptyMap(), checkoutMap(), emptyList())
        checkoutState.getSuggestion(createPlayerView(171f)).assert("-", "-", "-")
        checkoutState.getSuggestion(createPlayerView(20f)).assert("D10", "-", "-")
        checkoutState.getSuggestion(createPlayerView(3f)).assert("1", "D1", "-")
        checkoutState.getSuggestion(createPlayerView(1f)).assert("-", "-", "-")
        checkoutState.getSuggestion(createPlayerView(170f)).assert("T20", "T20", "D25")
    }

    @Test
    fun testOneThrow() {
        val checkoutState = CheckoutState(emptyMap(), checkoutMap(), emptyList())
        checkoutState.getSuggestion(
            createPlayerView(
                10f,
                fieldOne = Field.TEN,
                fieldTypeOne = FieldType.Single
            )
        ).assert("D10", "D5", "-")
    }

    @Test
    fun testTwoThrows() {
        val checkoutState = CheckoutState(emptyMap(), checkoutMap(), emptyList())
        checkoutState.getSuggestion(
            createPlayerView(
                8f,
                fieldOne = Field.SIXTEEN,
                fieldTypeOne = FieldType.Single,
                fieldTwo = Field.EIGHT,
                fieldTypeTwo = FieldType.Single,
            )
        ).assert("D16", "D8", "D4")
        checkoutState.getSuggestion(
            createPlayerView(
                160f,
                fieldOne = Field.FIVE,
                fieldTypeOne = FieldType.Single,
                fieldTwo = Field.FIVE,
                fieldTypeTwo = FieldType.Single,
            )
        ).assert("T20", "-", "-")
        checkoutState.getSuggestion(
            createPlayerView(
                24f,
                fieldOne = Field.NINETEEN,
                fieldTypeOne = FieldType.Triple,
                fieldTwo = Field.TWENTY,
                fieldTypeTwo = FieldType.Triple,
            )
        ).assert("T20", "T20", "D12")
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