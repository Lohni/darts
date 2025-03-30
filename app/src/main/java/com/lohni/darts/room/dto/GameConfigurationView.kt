package com.lohni.darts.room.dto

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.GameModeConfig
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.ScoreCalculation

data class GameConfigurationView(
    @Embedded val gameMode: GameModeView,
    @Embedded val gameModeConfig: GameModeConfigView,
)

data class GameModeView(
    @Embedded val gameMode: GameMode,
    @Relation(
        entity = GameModeStep::class,
        parentColumn = "gm_id",
        entityColumn = "gms_gm_id"
    )
    val steps: List<GameModeStep>
)

class GameModeConfigView @JvmOverloads constructor(
    @Embedded val gameModeConfig: GameModeConfig,
    @Ignore var successCalculation: ScoreCalculation? = null,
    @Ignore var failureCalculation: ScoreCalculation? = null,
)
