package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.StepWinCondition

@Entity(
    tableName = "game_mode_config",
    foreignKeys = [ForeignKey(
        entity = ScoreCalculation::class,
        parentColumns = ["sc_id"],
        childColumns = ["gmc_success_score_calculation"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ScoreCalculation::class,
        parentColumns = ["sc_id"],
        childColumns = ["gmc_failure_score_calculation"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class GameModeConfig(
    @ColumnInfo(name = "gmc_id") @PrimaryKey(autoGenerate = true) var gmcId: Int = 0,
    @ColumnInfo(name = "gmc_check_in_field_type", defaultValue = "0") var gmcCheckIn: FieldType = FieldType.ALL,
    @ColumnInfo(name = "gmc_check_out_field_type", defaultValue = "0") var gmcCheckOut: FieldType = FieldType.ALL,
    @ColumnInfo(
        name = "gmc_random_step_order",
        defaultValue = "0"
    ) var gmcRandomStepOrder: Boolean = false,
    @ColumnInfo(
        name = "gmc_repeat_step_on_failure",
        defaultValue = "0"
    ) var gmcRepeatOnFailure: Boolean = false,
    @ColumnInfo(
        name = "gmc_immediate_proceed_on_success",
        defaultValue = "0"
    ) var gmcImmediateProceedOnSuccess: Boolean = false,
    @ColumnInfo(
        name = "gmc_step_win_condition",
        defaultValue = "0"
    ) var gmcStepWinCondition: StepWinCondition = StepWinCondition.HIGHEST_SCORE,
    @ColumnInfo(name = "gmc_success_score_calculation") var gmcSuccessCalculation: Int? = null,
    @ColumnInfo(name = "gmc_failure_score_calculation") var gmcFailureCalculation: Int? = null
)
