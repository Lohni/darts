package com.lohni.darts.room.enums

enum class StepWinCondition(val swcId: Int, val swcName: String) {
    FIRST_TO_FINISH(0, "First to finish"),
    HIGHEST_SCORE(1, "Highest score"),
    LOWEST_SCORE(2, "Lowest score")
    ;

    companion object {
        private val map = StepWinCondition.entries.associateBy(StepWinCondition::swcId)
        fun getStepWinCondition(id: Int): StepWinCondition {
            return map.getOrDefault(id, HIGHEST_SCORE)
        }
    }
}