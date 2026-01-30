package com.example.android_data_transfer.models

import androidx.compose.ui.graphics.Color
import com.example.android_data_transfer.R

data class FileCategory(
    val name: String,
    val color: Color,
    val iconRes: Int
)

fun getFileCategory(fileName: String): FileCategory {
    val ext = fileName.substringAfterLast(".", "").lowercase()
    return when (ext) {
        "jpg", "jpeg", "png", "gif", "webp", "heic", "heif" -> FileCategory(
            "Image",
            Color(0xFFFB923C),
            R.drawable.ic_photo
        )

        "mp4", "mkv", "mov", "avi", "3gp", "webm" -> FileCategory(
            "Video",
            Color(0xFFF87171),
            R.drawable.ic_video
        )

        "mp3", "wav", "ogg", "m4a", "flac", "aac" -> FileCategory(
            "Audio",
            Color(0xFF818CF8),
            R.drawable.ic_audio
        )

        "pdf" -> FileCategory("PDF", Color(0xFFEF4444), R.drawable.ic_pdf)
        "doc", "docx", "txt", "rtf" -> FileCategory(
            "Document",
            Color(0xFF3B82F6),
            R.drawable.ic_word
        )

        "xls", "xlsx", "csv" -> FileCategory("Excel", Color(0xFF22C55E), R.drawable.ic_word)
        "ppt", "pptx" -> FileCategory("Powerpoint", Color(0xFFF97316), R.drawable.ic_word)
        "zip", "rar", "7z", "tar", "gz" -> FileCategory(
            "Archive",
            Color(0xFFA855F7),
            R.drawable.ic_copy
        )

        "apk", "aab" -> FileCategory("App", Color(0xFF34D399), R.drawable.ic_copy)
        else -> FileCategory("File", Color(0xFF94A3B8), R.drawable.ic_copy)
    }
}