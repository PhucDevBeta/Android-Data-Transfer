package com.example.android_data_transfer.models.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.android_data_transfer.models.local.entity.TransferHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM transfer_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<TransferHistory>>

    @Insert
    suspend fun insertHistory(history: TransferHistory)
}