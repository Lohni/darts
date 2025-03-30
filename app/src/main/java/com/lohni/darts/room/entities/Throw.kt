package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType

@Entity(
    tableName = "throw", primaryKeys = ["t_leg", "t_player", "t_turn", "t_ordinal"],
    indices = [Index("t_leg")],
    foreignKeys = [
        ForeignKey(
            entity = Leg::class,
            parentColumns = ["l_id"],
            childColumns = ["t_leg"],
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = GameModeStep::class,
            parentColumns = ["gms_id"],
            childColumns = ["t_game_mode_step"]
        ), ForeignKey(
            entity = Player::class,
            parentColumns = ["p_id"],
            childColumns = ["t_player"],
        )]
)
data class Throw(
    @ColumnInfo(name = "t_leg") var tLeg: Int = 0,
    @ColumnInfo(name = "t_player") val tPlayer: Int,
    @ColumnInfo(name = "t_turn") val tTurn: Int = 0,
    @ColumnInfo(name = "t_ordinal") var tOrdinal: Int = 0,
    @ColumnInfo(name = "t_field") val tField: Field,
    @ColumnInfo(name = "t_field_type") val tFieldType: FieldType,
    @ColumnInfo(name = "t_game_mode_step") var tGameModeStep: Int? = null,
    @ColumnInfo(name = "t_score") val tScore: Float,
    @ColumnInfo(name = "t_bust") var tBust: Boolean = false
)

fun Throw.shortString(): String {
    return this.tFieldType.shortLabel + this.tField.label
}
