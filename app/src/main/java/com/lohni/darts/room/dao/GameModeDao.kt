package com.lohni.darts.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lohni.darts.room.dto.GameConfigurationView
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.GameModeConfig
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.ScoreCalculation
import kotlinx.coroutines.flow.Flow

@Dao
interface GameModeDao {
    @Query("SELECT * FROM GAME_MODE")
    fun getGameModes(): Flow<List<GameMode>>

    @Query("SELECT * FROM GAME_MODE where gm_id = :id")
    fun getGameModeById(id: Int): Flow<GameMode>

    @Query("SELECT * FROM GAME_MODE_CONFIG where gmc_id = :id")
    fun getGameModeConfigById(id: Int): Flow<GameModeConfig>

    @Query("SELECT * FROM SCORE_CALCULATION WHERE SC_ID = :id")
    fun getScoreCalculationById(id: Int): Flow<ScoreCalculation>

    @Query("SELECT * FROM GAME_MODE_STEP WHERE gms_gm_id = :id")
    fun getGameModeStepsForGameMode(id: Int): Flow<List<GameModeStep>>

    @Query("SELECT game_mode.* FROM GAME_MODE JOIN GAME on g_game_mode = gm_id order by g_id desc limit 1")
    fun getLastPlayedGameMode(): Flow<GameMode?>

    @Query("SELECT count(*) from GAME where g_game_mode = :gmId")
    fun getGameCountForGameMode(gmId: Int): Flow<Int>


    @Query(
        """
        SELECT * FROM GAME_MODE
        LEFT JOIN GAME_MODE_STEP on gms_gm_id = gm_id
        JOIN GAME_MODE_CONFIG on gmc_id = gm_game_mode_config
        WHERE gm_id = :gmId
        """
    )
    @Transaction
    fun getGameConfigurationViewForGameMode(gmId: Int): Flow<GameConfigurationView>


    @Update
    fun updateScoreCalculation(calculation: ScoreCalculation)

    @Insert
    fun createScoreCalculation(calculation: ScoreCalculation): Long

    @Delete
    fun deleteScoreCalculation(calculation: ScoreCalculation)

    @Update
    fun updateGameModeConfig(config: GameModeConfig)

    @Insert
    fun createGameModeConfig(config: GameModeConfig): Long

    @Delete
    @Transaction
    fun deleteGameModeConfig(gameModeConfig: GameModeConfig)

    @Update
    fun updateGameMode(gameMode: GameMode)

    @Insert
    fun createGameMode(gameMode: GameMode): Long

    @Delete
    @Transaction
    fun deleteGameMode(gameMode: GameMode)

    @Update
    fun updateGameModeStep(gms: GameModeStep)

    @Insert
    fun createGameModeStep(gms: GameModeStep)

    @Delete
    fun deleteGameModeStep(gms: GameModeStep)


    fun deleteGameMode(gmId: Int) {
    }
}