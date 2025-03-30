package com.lohni.darts.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.lohni.darts.room.dto.GameSummaryShort
import com.lohni.darts.room.dto.ShortSummary
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query(
        "SELECT g_id as gameId, g_credat as date, gm_name as gameMode, g_num_sets as sets, g_num_legs as legs, count(gp_player) as playerCount, player.* FROM GAME " +
                "JOIN GAME_MODE on gm_id = g_game_mode " +
                "JOIN GAME_PLAYER on gp_game = g_id " +
                "JOIN PLAYER on g_winner = p_id " +
                "GROUP BY g_id " +
                "ORDER BY g_id desc"
    )
    fun getShortGameSummaries(): Flow<List<GameSummaryShort>>

    @Query(
        "SELECT g_id as gameId, g_credat as date, gm_name as gameMode, g_num_sets as sets, g_num_legs as legs, count(gp_player) as playerCount, player.* FROM GAME " +
                "JOIN GAME_MODE on gm_id = g_game_mode " +
                "JOIN GAME_PLAYER on gp_game = g_id " +
                "JOIN PLAYER on g_winner = p_id " +
                "WHERE gp_player = :pId " +
                "GROUP BY g_id " +
                "ORDER BY g_id desc"
    )
    fun getShortGameSummariesForPlayer(pId: Int): Flow<List<GameSummaryShort>>

    @Query(
        "SELECT g_id as gameId, g_credat as date, gm_name as gameMode, g_num_sets as sets, g_num_legs as legs, count(gp_player) as playerCount, player.* FROM GAME " +
                "JOIN GAME_MODE on gm_id = g_game_mode " +
                "JOIN GAME_PLAYER on gp_game = g_id " +
                "JOIN PLAYER on g_winner = p_id " +
                "WHERE g_id = :gameId " +
                "GROUP BY g_id "
    )
    fun getShortGameSummary(gameId: Int): Flow<GameSummaryShort>

    @Query(
        "SELECT s_id as id, s_ordinal as ordinal, " +
                "cast(sum(t_score) as float) / count(t_score)*3 as winnerAvg, " +
                "player.* " +
                "FROM 'SET' " +
                "JOIN LEG ON l_set = s_id " +
                "JOIN THROW ON t_leg = l_id and t_player = s_winner " +
                "JOIN PLAYER on p_id = s_winner " +
                "WHERE s_game = :gameId " +
                "GROUP BY s_id " +
                "ORDER BY s_id ASC"
    )
    fun getShortSetSummary(gameId: Int): Flow<List<ShortSummary>>

    @Query(
        "SELECT l_id as id, l_ordinal as ordinal, " +
                "cast(sum(t_score) as float) / count(t_score)*3 as winnerAvg, " +
                "player.* " +
                "FROM LEG " +
                "JOIN THROW ON t_leg = l_id and t_player = l_winner " +
                "JOIN PLAYER on p_id = l_winner " +
                "WHERE l_set = :setId " +
                "GROUP BY l_id " +
                "ORDER BY l_ordinal ASC"
    )
    fun getShortLegSummary(setId: Int): Flow<List<ShortSummary>>

    @Query(
        "SELECT * FROM PLAYER " +
                "JOIN GAME_PLAYER ON gp_player = p_id " +
                "WHERE gp_game = :gId"
    )
    fun getPlayers(gId: Int): Flow<List<Player>>

    @Query("SELECT * FROM THROW WHERE t_leg = :lId ORDER BY t_ordinal asc")
    fun getThrowsByLeg(lId: Int): Flow<List<Throw>>

    @Query(
        "SELECT * FROM GAME_MODE " +
                "JOIN GAME on g_game_mode = gm_id " +
                "WHERE g_id = :gId"
    )
    fun getGameModeByGameId(gId: Int): Flow<GameMode>
}