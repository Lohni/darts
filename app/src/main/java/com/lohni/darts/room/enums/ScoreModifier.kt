package com.lohni.darts.room.enums

enum class ScoreModifier(val smId: Int, val label: String, val calc: Float.(Float) -> Float) {
    NONE(0, "None", { this }),
    ADD(1, "+", { byValue -> this.plus(byValue) }),
    SUBTRACT(2, "-", { byValue -> this.minus(byValue) }),
    MULTIPLY(3, "*", { byValue -> this.times(byValue) }),
    DIVIDE(4, "/", { byValue -> this.div(byValue) });

    companion object {
        private val map = ScoreModifier.entries.associateBy(ScoreModifier::smId)
        fun getScoreModifierByInt(id: Int): ScoreModifier {
            return map.getOrDefault(id, NONE)
        }
    }
}
