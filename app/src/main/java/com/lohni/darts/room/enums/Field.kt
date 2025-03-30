package com.lohni.darts.room.enums

enum class Field(val fId: Int, val label: String) {
    NONE(-2, "-"),
    ALL(-1, "All"),
    ZERO(0, "0"),
    ONE(1, "1"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    ELEVEN(11, "11"),
    TWELVE(12, "12"),
    THIRTEEN(13, "13"),
    FOURTEEN(14, "14"),
    FIFTEEN(15, "15"),
    SIXTEEN(16, "16"),
    SEVENTEEN(17, "17"),
    EIGHTEEN(18, "18"),
    NINETEEN(19, "19"),
    TWENTY(20, "20"),
    TWENTYFIVE(25, "25");

    companion object {
        private val map = entries.associateBy(Field::fId)
        fun getFieldByInt(id: Int): Field {
            return map.getOrDefault(id, ZERO)
        }
    }
}
