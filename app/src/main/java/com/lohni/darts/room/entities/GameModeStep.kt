package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType

@Entity(
    tableName = "game_mode_step",
    foreignKeys = [ForeignKey(
        entity = GameMode::class,
        parentColumns = ["gm_id"],
        childColumns = ["gms_gm_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class GameModeStep(
    @ColumnInfo(name = "gms_id") @PrimaryKey(autoGenerate = true) var gmsId: Int = 0,
    @ColumnInfo(name = "gms_gm_id") var gmsGameMode: Int = 0,
    @ColumnInfo(name = "gms_ordinal") var gmsOrdinal: Int = 0,
    @ColumnInfo(name = "gms_field") var gmsField: Field = Field.ALL,
    @ColumnInfo(name = "gms_field_type") var gmsFieldType: FieldType = FieldType.ALL
)

fun GameModeStep.requirementMet(field: Field, fieldType: FieldType): Boolean {
    return (gmsField == Field.ALL || field == gmsField) && (gmsFieldType == FieldType.ALL || gmsFieldType == fieldType)
}

fun GameModeStep.shortString(): String {
    val fieldType = if (this.gmsFieldType == FieldType.ALL && this.gmsField == Field.ALL) ""
    else if (this.gmsFieldType == FieldType.Single && this.gmsField == Field.ALL) "S"
    else if (this.gmsFieldType == FieldType.ALL) "A"
    else this.gmsFieldType.shortLabel

    return fieldType + this.gmsField.label
}

