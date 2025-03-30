package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType

@Entity(
    tableName = "checkout_table",
    primaryKeys = ["ct_score", "ct_player", "ct_check_out_type"]
)
data class CheckoutTable(
    @ColumnInfo(name = "ct_score") val ctScore: Float = 0f,
    @ColumnInfo(name = "ct_player") val ctPlayer: Int = -2,
    @ColumnInfo(name = "ct_check_out_type") val ctCheckOutType: FieldType = FieldType.Single,
    @ColumnInfo(name = "ct_field_one") val ctFieldOne: Field = Field.NONE,
    @ColumnInfo(name = "ct_field_type_one") val ctFieldTypeOne: FieldType = FieldType.Single,
    @ColumnInfo(name = "ct_field_two") val ctFieldTwo: Field = Field.NONE,
    @ColumnInfo(name = "ct_field_type_two") val ctFieldTypeTwo: FieldType = FieldType.Single,
    @ColumnInfo(name = "ct_field_three") val ctFieldThree: Field = Field.NONE,
    @ColumnInfo(name = "ct_field_type_three") val ctFieldTypeThree: FieldType = FieldType.Single,
)

fun CheckoutTable.toArray(): Array<String> {
    return Array(3) {
        when (it) {
            0 -> this.ctFieldTypeOne.shortLabel + this.ctFieldOne.label
            1 -> this.ctFieldTypeTwo.shortLabel + this.ctFieldTwo.label
            else -> this.ctFieldTypeThree.shortLabel + this.ctFieldThree.label
        }
    }
}

fun CheckoutTable.stepCount(): Int {
    var count = 0
    if (this.ctFieldOne != Field.NONE) count += 1
    if (this.ctFieldTwo != Field.NONE) count += 1
    if (this.ctFieldThree != Field.NONE) count += 1
    return count
}