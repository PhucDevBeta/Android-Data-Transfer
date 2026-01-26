package com.example.android_data_transfer.models.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfer_history")
data class TransferHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileSize: Long,
    val type: String,
    val timestamp: Long = System.currentTimeMillis()
)