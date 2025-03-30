package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.room.enums.ScoreType

@Entity(
    tableName = "game_mode",
    foreignKeys = [ForeignKey(
        entity = GameModeConfig::class,
        parentColumns = ["gmc_id"],
        childColumns = ["gm_game_mode_config"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class GameMode(
    @ColumnInfo(name = "gm_id") @PrimaryKey(autoGenerate = true) var gmId: Int = 0,
    @ColumnInfo(name = "gm_game_mode_type") var gmType: GameModeType = GameModeType.CLASSIC,
    @ColumnInfo(name = "gm_game_mode_config") var gmConfig: Int = 0,
    @ColumnInfo(name = "gm_name") var gmName: String = "",
    @ColumnInfo(name = "gm_start_score") var gmStartScore: Int = 501,
    @ColumnInfo(name = "gm_score_type") var gmScoreType: ScoreType = ScoreType.POINT
)
