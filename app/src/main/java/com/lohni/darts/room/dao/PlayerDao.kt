package com.lohni.darts.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lohni.darts.room.dto.PlayerActivityView
import com.lohni.darts.room.entities.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM PLAYER WHERE p_archive = 0")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT player.*, count(gp_player) as games_played, count(t_player) darts_thrown FROM PLAYER " +
            "JOIN GAME_PLAYER on gp_player = p_id " +
            "JOIN GAME ON g_id = gp_game " +
            "JOIN `SET` ON s_id = s_game " +
            "JOIN LEG ON l_set = s_id " +
            "JOIN THROW ON t_leg = l_id " +
            "WHERE p_archive = 0 " +
            "GROUP BY gp_player, t_player ")
    fun getAllPlayersWithActivity(): Flow<List<PlayerActivityView>>

    @Query("SELECT * FROM PLAYER WHERE p_id = :id")
    fun getPlayerById(id: Int): Flow<Player>

    @Query("SELECT * FROM PLAYER WHERE p_id in (:ids)")
    fun getPlayersByIds(ids: List<Int>): Flow<List<Player>>

    @Query("SELECT PLAYER.* FROM PLAYER JOIN GAME_PLAYER ON GP_PLAYER = P_ID WHERE p_archive = 0 and gp_game in (SELECT g_id FROM GAME ORDER BY g_id DESC LIMIT 1)")
    fun getLastUsedPlayers(): Flow<List<Player>>

    @Update
    fun updatePlayer(player: Player)

    @Insert
    fun createPlayer(player: Player)

    @Delete
    fun deletePlayer(player: Player)

}