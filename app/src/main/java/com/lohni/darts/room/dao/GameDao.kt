package com.lohni.darts.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lohni.darts.room.dto.GameSummary
import com.lohni.darts.room.entities.Game
import com.lohni.darts.room.entities.GamePlayer
import com.lohni.darts.room.entities.Leg
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Set
import com.lohni.darts.room.entities.Throw
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert
    fun insertGame(game: Game): Long

    @Insert
    fun insertSet(set: Set): Long

    @Insert
    fun insertLeg(leg: Leg): Long

    @Insert
    fun insertThrow(t: Throw)

    @Insert
    fun insertGamePlayer(gp: GamePlayer)

    @Query("SELECT player.*, " +
            "(select count(s_id) from `set` where s_game = :gId and s_winner = :pId) as setsWon, " +
            "(select count(l_id) from leg join `set` on s_id = l_set where s_game = :gId and l_winner = :pId) as legsWon, " +
            "case when gm_game_mode_type = 0 then gm_start_score - sum(case when not t_bust then t_score else 0 end) " +
            "else gm_start_score + sum(case when not t_bust then t_score else 0 end) end as score, " +
            "cast(sum(case when not t_bust then t_score else 0 end) as float) / count(t_score)*3 as avg " +
            "FROM GAME " +
            "JOIN GAME_MODE ON gm_id = g_game_mode " +
            "JOIN GAME_MODE_CONFIG ON gmc_id = gm_game_mode_config " +
            "JOIN GAME_PLAYER on gp_game = g_id " +
            "JOIN PLAYER on p_id = gp_player " +
            "JOIN `SET` on s_game = g_id " +
            "JOIN LEG on l_set = s_id " +
            "JOIN THROW on t_leg = l_id " +
            "WHERE g_id = :gId and t_player = :pId and gp_player = :pId")
    fun getGameSummary(gId: Int, pId: Int): Flow<GameSummary>

    @Query("SELECT player.*, " +
            "\"\" as setsWon, " +
            "(select count(l_id) from leg join `set` on s_id = l_set where s_id = :sId and l_winner = :pId) as legsWon, " +
            "\"\" as score ," +
            "cast(sum(case when not t_bust then t_score else 0 end) as float) / count(t_score)*3 as avg " +
            "FROM 'SET' " +
            "JOIN PLAYER on p_id = t_player " +
            "JOIN LEG on l_set = s_id " +
            "JOIN THROW on t_leg = l_id " +
            "WHERE s_id = :sId and t_player = :pId ")
    fun getGameSetSummary(sId: Int, pId: Int): Flow<GameSummary>

    @Query("SELECT player.*, " +
            "\"\" as setsWon, " +
            "\"\" as legsWon ," +
            "case when gm_game_mode_type = 0 then gm_start_score - sum(case when not t_bust then t_score else 0 end) " +
            "else gm_start_score + sum(case when not t_bust then t_score else 0 end) end as score, " +
            "cast(sum(case when not t_bust then t_score else 0 end) as float) / count(t_score)*3 as avg " +
            "FROM LEG " +
            "JOIN GAME on g_id = :gId " +
            "JOIN GAME_MODE on gm_id = g_game_mode " +
            "JOIN THROW on t_leg = l_id " +
            "JOIN PLAYER on p_id = t_player " +
            "WHERE l_id = :lId " +
            "GROUP BY t_player " +
            "ORDER BY legsWon asc")
    fun getGameLegSummary(gId: Int, lId: Int): Flow<List<GameSummary>>

    @Query("SELECT gp_player FROM GAME_PLAYER where gp_game = :gId")
    fun getGamePlayerIdsByGameId(gId: Int): Flow<List<Int>>

    @Query("SELECT * FROM PLAYER " +
            "JOIN GAME on g_winner = p_id " +
            "WHERE g_id = :gId")
    fun getWinningPlayerByGameId(gId: Int): Flow<Player>

    @Query("SELECT * FROM PLAYER " +
            "JOIN 'SET' ON s_winner = p_id " +
            "where s_id = :sId")
    fun getWinningPlayerBySetId(sId: Int): Flow<Player>

    @Query("SELECT * FROM GAME WHERE g_id = :gId")
    fun getGameById(gId: Int): Flow<Game>
}