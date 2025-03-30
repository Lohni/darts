package com.lohni.darts.room.enums

enum class FieldType(val ftId: Int, val label: String, val shortLabel: String) {
    ALL(0, "All", ""),
    Single(1, "Single", ""),
    Double(2, "Double", "D"),
    Triple(3, "Triple", "T");

    companion object {
        private val map = entries.associateBy(FieldType::ftId)

        fun getFieldTypeByInt(id: Int): FieldType {
            return map.getOrDefault(id, ALL)
        }
    }
}
