package com.lohni.darts.room.enums

enum class SettingType(val stId: Int, val stName: String) {
    DARK_MODE(0, "Dark mode");

    companion object {
        private val map = SettingType.entries.associateBy(SettingType::stId)
        fun getSettingTypeByInt(id: Int): SettingType {
            return map.getOrDefault(id, DARK_MODE)
        }
    }
}