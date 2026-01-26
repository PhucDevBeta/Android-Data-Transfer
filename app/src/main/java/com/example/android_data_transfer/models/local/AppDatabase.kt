package com.example.android_data_transfer.models.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android_data_transfer.models.local.dao.HistoryDao
import com.example.android_data_transfer.models.local.entity.TransferHistory

@Database(entities = [TransferHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
