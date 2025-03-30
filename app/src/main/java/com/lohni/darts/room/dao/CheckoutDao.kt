package com.lohni.darts.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.enums.FieldType
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckoutDao {

    @Query("SELECT * FROM CHECKOUT_TABLE " +
            "WHERE ct_check_out_type = :type " +
            "AND ct_player = :pId " +
            "ORDER BY ct_score ASC")
    fun getCheckoutTables(type: FieldType, pId: Int): Flow<List<CheckoutTable>>

    @Query("SELECT PLAYER.* FROM PLAYER " +
            "JOIN CHECKOUT_TABLE on ct_player = p_id " +
            "WHERE ct_check_out_type = :type " +
            "GROUP BY p_id")
    fun getPlayersForCheckoutType(type: FieldType): Flow<List<Player>>

    @Query("SELECT PLAYER.* FROM PLAYER " +
            "LEFT JOIN CHECKOUT_TABLE ON ct_player = p_id AND ct_check_out_type = :type " +
            "WHERE ct_player is null " +
            "GROUP BY p_id")
    fun getPlayersWithoutSuggestions(type: FieldType): Flow<List<Player>>

    @Query("INSERT INTO CHECKOUT_TABLE " +
            "SELECT ct_score, :pId, :type, ct_field_one, ct_field_type_one, ct_field_two, ct_field_type_two, ct_field_three, ct_field_type_three " +
            "FROM CHECKOUT_TABLE " +
            "WHERE ct_check_out_type = :type AND ct_player = -2")
    fun createSuggestionsForPlayer(type: FieldType, pId: Int)

    @Query("DELETE FROM CHECKOUT_TABLE WHERE ct_check_out_type = :type and ct_player = :pId")
    fun deleteSuggestionForPlayer(type: FieldType, pId: Int)

    @Update
    fun updateCheckoutTable(checkoutTable: CheckoutTable)
}