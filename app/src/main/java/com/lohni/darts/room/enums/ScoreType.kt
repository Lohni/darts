package com.lohni.darts.room.enums

enum class ScoreType(val stId: Int, val label: String) {
    POINT(0, "Points"),
    DART(1, "Darts");

    companion object {
        private val map = ScoreType.entries.associateBy(ScoreType::stId)
        fun getScoreTypeByInt(id: Int): ScoreType {
            return map.getOrDefault(id, POINT)
        }
    }
}