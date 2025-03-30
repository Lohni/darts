package com.lohni.darts.room.enums

enum class GameModeType(val gmtId: Int, val label: String) {
    CLASSIC(0, "Classic"),
    STEP(1, "Step");

    companion object {
        private val map = entries.associateBy(GameModeType::gmtId)
        fun getGameModeTypeByInt(id: Int): GameModeType {
            return map.getOrDefault(id, CLASSIC)
        }
    }
}
