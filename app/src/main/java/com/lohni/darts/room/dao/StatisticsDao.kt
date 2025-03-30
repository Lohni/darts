package com.lohni.darts.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.lohni.darts.room.dto.CompetitiveSummary
import com.lohni.darts.room.dto.GameSetView
import com.lohni.darts.room.dto.LegGroupedThrows
import com.lohni.darts.room.dto.PlayerComparisonView
import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.GameModeConfig
import com.lohni.darts.room.enums.FieldType
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {
    @Query("SELECT * FROM GAME_MODE")
    fun getAllGameModes(): Flow<List<GameMode>>

    //@Query("SELECT game_mode.* FROM GAME JOIN GAME_MODE ON gm_id = g_game_mode ORDER BY g_id DESC LIMIT 1")
    @Query("SELECT game_mode.* FROM GAME_MODE JOIN GAME ON gm_id = g_game_mode ORDER BY g_id DESC LIMIT 1")
    fun getLastGameMode(): Flow<GameMode?>

    @Query(
        "SELECT * FROM GAME_MODE_CONFIG " +
                "WHERE gmc_id = :gmcId"
    )
    fun getGameModeConfig(gmcId: Int): Flow<GameModeConfig>

    @Query(
        "SELECT * FROM CHECKOUT_TABLE " +
                "WHERE ct_player = -2 AND ct_check_out_type = :type"
    )
    fun getCheckoutTableByType(type: FieldType): Flow<List<CheckoutTable>>

    @Query(
        "SELECT count(t_score) FROM GAME " +
                "JOIN 'SET' ON s_game = g_id " +
                "JOIN LEG ON l_set = s_id " +
                "JOIN THROW on t_leg = l_id " +
                "WHERE g_game_mode = :gmId and t_player = :pId"
    )
    fun getPlayerDartsForGameMode(gmId: Int, pId: Int): Flow<Int>

    @Query(
        "SELECT CASE WHEN count(t_score) = 0 THEN 0 ELSE cast(sum(case when not t_bust then t_score else 0 end) as float) / count(t_score)*3 END " +
                "FROM GAME " +
                "JOIN 'SET' on s_game = g_id " +
                "JOIN LEG on l_set = s_id " +
                "JOIN THROW on t_leg = l_id " +
                "WHERE g_game_mode = :gmId and t_player = :pId"
    )
    fun getPlayerAverageForGameMode(gmId: Int, pId: Int): Flow<Float>

    @Query(
        """
            SELECT cast(count(t_score) as float) / count(distinct t_leg)
            FROM GAME
            JOIN 'SET' on s_game = g_id
            JOIN LEG on l_set = s_id
            JOIN THROW on t_leg = l_id
            WHERE g_game_mode = :gmId and t_player = :pId
        """
    )
    fun getAverageDartsPerLegForGameMode(gmId: Int, pId: Int): Flow<Float>

    @Query(
        "SELECT count(t_score) FROM GAME " +
                "JOIN 'SET' ON s_game = g_id " +
                "JOIN LEG ON l_set = s_id " +
                "JOIN THROW on t_leg = l_id " +
                "WHERE g_game_mode = :gmId and t_player = :pId and t_field_type = :type and t_bust = 0"
    )
    fun getPlayerThrowsForFieldTypeAndGameMode(gmId: Int, pId: Int, type: FieldType): Flow<Int>

    @Query(
        "SELECT count(t_score) FROM GAME " +
                "JOIN 'SET' ON s_game = g_id " +
                "JOIN LEG ON l_set = s_id " +
                "JOIN THROW on t_leg = l_id " +
                "WHERE g_game_mode = :gmId and t_player = :pId and (t_field = 0 or t_bust = 1)"
    )
    fun getPlayerMissesForGameMode(gmId: Int, pId: Int): Flow<Int>

    @Query(
        "select count(*) FROM (SELECT count(t_score) FROM GAME " +
                "JOIN 'SET' ON s_game = g_id " +
                "JOIN LEG ON l_set = s_id " +
                "JOIN THROW on t_leg = l_id " +
                "WHERE g_game_mode = :gmId and t_player = :pId " +
                "GROUP BY t_leg, t_turn " +
                "HAVING sum(t_score) >= :t)"
    )
    fun getPlayerThrowsForGameModeAboveScoreThreshold(gmId: Int, pId: Int, t: Float): Flow<Int>

    @Query(
        "SELECT sum(t_score) FROM " +
                "(SELECT t_leg as leg_id, max(t_turn) as max_turn FROM GAME " +
                "JOIN 'SET' ON s_game = g_id " +
                "JOIN LEG ON l_set = s_id AND l_winner = :pId " +
                "JOIN THROW on t_leg = l_id " +
                "WHERE g_game_mode = :gmId AND t_player = :pId " +
                "GROUP BY t_leg " +
                "HAVING t_turn = max(t_turn)) " +
                "JOIN THROW ON t_leg = leg_id AND t_turn = max_turn AND t_player = :pId " +
                "GROUP BY t_leg, t_turn " +
                "ORDER BY sum(t_score) DESC " +
                "LIMIT 1"
    )
    fun getPlayerHighestFinishForGameMode(gmId: Int, pId: Int): Flow<Float>

    @Query(
        "SELECT count(t_turn) FROM GAME " +
                "JOIN 'SET' ON s_game = g_id " +
                "JOIN LEG ON l_set = s_id " +
                "JOIN THROW on t_leg = l_id " +
                "WHERE g_game_mode = :gmId AND t_player = :pId " +
                "GROUP BY t_turn " +
                "HAVING NOT t_field*t_field_type"
    )
    fun getPlayerTrebblelessForGameMode(gmId: Int, pId: Int): Flow<Int>

    @Query(
        "SELECT count(g_id) as amount, " +
                "count(case g_winner when :pId then 1 else null end) as won " +
                "FROM GAME " +
                "JOIN game_player on gp_game = g_id " +
                "WHERE gp_player = :pId AND g_game_mode = :gmId"
    )
    fun getPlayerGameOverviewForGameMode(gmId: Int, pId: Int): Flow<CompetitiveSummary>

    @Query(
        "SELECT count(s_id) as amount, " +
                "count(case s_winner when :pId then 1 else null end) as won " +
                "FROM GAME " +
                "JOIN game_player on gp_game = g_id " +
                "JOIN 'SET' on s_game = g_id " +
                "WHERE gp_player = :pId AND g_game_mode = :gmId"
    )
    fun getPlayerSetOverviewForGameMode(gmId: Int, pId: Int): Flow<CompetitiveSummary>

    @Query(
        "SELECT count(l_id) as amount, " +
                "count(case l_winner when :pId then 1 else null end) as won " +
                "FROM GAME " +
                "JOIN game_player on gp_game = g_id " +
                "JOIN 'SET' on s_game = g_id " +
                "JOIN LEG on l_set = s_id " +
                "WHERE gp_player = :pId AND g_game_mode = :gmId"
    )
    fun getPlayerLegOverviewForGameMode(gmId: Int, pId: Int): Flow<CompetitiveSummary>

    @Query(
        """
            SELECT g_credat as game_date, case when l_winner = :pId then 1 else 0 end as has_won,  t_leg as leg, throw.* FROM THROW 
            JOIN LEG on l_id = t_leg 
            JOIN `SET` on s_id = l_set 
            JOIN GAME on g_id = s_game 
            WHERE g_game_mode = :gmId and t_player = :pId 
            GROUP BY t_leg 
            ORDER by g_credat ASC, t_ordinal ASC
        """
    )
    @Transaction
    fun getThrowsPerLegForPlayerAndGameMode(gmId: Int, pId: Int): Flow<List<LegGroupedThrows>>

    /**
     * Competitive
     */
    @Query(
        """
            select  
            count(distinct g_id) as games_total,
            (select count(g_id) from game where g_game_mode = :gmId and g_winner = :p1) as games_won,
            count(distinct s_id) as sets_total,
            (select count(s_id) from 'set' join game on s_game = g_id where g_game_mode = :gmId and s_winner = :p1) as sets_won,
            count(distinct l_id) as legs_total,
            (select count(s_id) from leg join 'set' on s_id = l_set join game on s_game = g_id where g_game_mode = :gmId and l_winner = :p1) as legs_won,
            player.*
            from game
            join game_player on gp_game = g_id and gp_player = :p1
            join player on p_id = :p1
            join 'set' on s_game = g_id
            join leg on l_set = s_id
            where g_game_mode = :gmId and gp_player = :p1
        """
    )
    fun getOverallCompetitiveStatistics(gmId: Int, p1: Int): Flow<PlayerComparisonView>

    @Query(
        """
            select game.*, 'set'.*, leg.* from game
            join 'set' on s_game = g_id
            join leg on l_set = s_id
            join game_player p1 on p1.gp_player = :p1 
            join game_player p2 on p2.gp_player = :p2
            join player on p_id = p2.gp_player
            where p1.gp_game = p2.gp_game and g_id = p1.gp_game and g_game_mode = :gmId
       """
    )
    @Transaction
    fun getGameSetLegForPlayerCombination(gmId: Int, p1: Int, p2: Int): Flow<List<GameSetView>>
}