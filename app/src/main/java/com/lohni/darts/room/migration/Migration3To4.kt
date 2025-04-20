package com.lohni.darts.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4= object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add GameMode Halve It
        val halveItScoreCalcSuccess = """
            INSERT INTO score_calculation ("sc_id", "sc_by_type", "sc_by_type_modifier", "sc_by_value", "sc_by_value_modifier") 
            VALUES 
            (-1, 0, 1, 0, 0),
            (-2, 0, 0, 2, 4);
        """.trimIndent()
        db.execSQL(halveItScoreCalcSuccess)

        val halveItGameModeConfig = """
            INSERT INTO game_mode_config ("gmc_id", "gmc_check_in_field_type", "gmc_check_out_field_type", "gmc_random_step_order", "gmc_repeat_step_on_failure", "gmc_immediate_proceed_on_success", "gmc_step_win_condition", "gmc_success_score_calculation", "gmc_failure_score_calculation") 
            VALUES (-2, 0, 0, 0, 0, 0, 1, -1, -2);
        """.trimIndent()
        db.execSQL(halveItGameModeConfig)

        val halveItGameMode = """
            INSERT INTO game_mode ("gm_id", "gm_game_mode_type", "gm_game_mode_config", "gm_name", "gm_start_score", "gm_score_type") 
            VALUES (-2, 1, -2, 'Halve It', 40, 0);
        """.trimIndent()
        db.execSQL(halveItGameMode)

        val halveItGameModeSteps = """
            INSERT INTO game_mode_step ("gms_id", "gms_gm_id", "gms_ordinal", "gms_field", "gms_field_type") 
            VALUES 
            (-1, -2, 0, 15, 0),
            (-2, -2, 1, 16, 0),
            (-3, -2, 2, -1, 2),
            (-4, -2, 3, 17, 0),
            (-5, -2, 4, 18, 0),
            (-6, -2, 5, -1, 3),
            (-7, -2, 6, 19, 0),
            (-8, -2, 7, 20, 0),
            (-9, -2, 8, 25, 0);
        """.trimIndent()
        db.execSQL(halveItGameModeSteps)

        //301 Classic
        val gameModeConfig = """
            INSERT INTO game_mode_config ("gmc_id", "gmc_check_in_field_type", "gmc_check_out_field_type", "gmc_random_step_order", "gmc_repeat_step_on_failure", "gmc_immediate_proceed_on_success", "gmc_step_win_condition", "gmc_success_score_calculation", "gmc_failure_score_calculation") 
            VALUES (-3, 0, 0, 0, 0, 0, 0, NULL, NULL);
        """
        db.execSQL(gameModeConfig)

        val gameMode = """
            INSERT INTO game_mode ("gm_id", "gm_game_mode_type", "gm_game_mode_config", "gm_name", "gm_start_score", "gm_score_type") 
            VALUES (-3, 0, -3, '301', 301, 0);
        """
        db.execSQL(gameMode)
    }
}
