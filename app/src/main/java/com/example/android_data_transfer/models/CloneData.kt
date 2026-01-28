package com.example.android_data_transfer.models

import java.io.File

data class Contact(
    val name: String,
    val phoneNumber: String
)

data class SMS(
    val address: String,
    val body: String,
    val date: Long,
    val type: Int // 1 for inbox, 2 for sent
)

data class CloneCategory(
    val id: String,
    val name: String,
    val icon: Int,
    val count: Int,
    val size: Long,
    val isSelected: Boolean = false,
    val type: CloneType
)

enum class CloneType {
    CONTACTS,
    PHOTOS,
    DOCUMENTS,
    SMS
}
