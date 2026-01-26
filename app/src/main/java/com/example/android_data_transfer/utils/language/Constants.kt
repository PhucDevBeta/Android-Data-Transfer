package com.example.android_data_transfer.utils.language

import com.example.android_data_transfer.models.local.LANG
import com.example.android_data_transfer.models.local.Language

object Constants {
    val listLanguageApp = listOf(
        Language(
            id = 1,
            name = "English", // English tự để English
            code = LANG.EN,
            icon = "🇬🇧"
        ),
        Language(
            id = 2,
            name = "English (US)", // English Mỹ
            code = LANG.EN_US,
            icon = "🇺🇸"
        ),
        Language(
            id = 3,
            name = "Bahasa Indonesia", // giữ nguyên
            code = LANG.IN,
            icon = "🇮🇩"
        ),
        Language(
            id = 4,
            name = "हिन्दी", // Hindi viết chuẩn theo tiếng Hindi
            code = LANG.HI,
            icon = "🇮🇳"
        ),
        Language(
            id = 5,
            name = "Bahasa Melayu", // tên chuẩn thay vì Malaysia
            code = LANG.MS,
            icon = "🇲🇾"
        ),
        Language(
            id = 6,
            name = "Filipino", // giữ nguyên
            code = LANG.PHI,
            icon = "🇵🇭"
        ),
        Language(
            id = 7,
            name = "한국어", // Korean chuẩn
            code = LANG.KO,
            icon = "🇰🇷"
        ),
        Language(
            id = 8,
            name = "Español", // Spanish
            code = LANG.ES,
            icon = "🇪🇸"
        ),
        Language(
            id = 9,
            name = "Türkçe", // Turkish
            code = LANG.TR,
            icon = "🇹🇷"
        ),
        Language(
            id = 10,
            name = "Português", // Portuguese
            code = LANG.PT,
            icon = "🇵🇹"
        ),
        Language(
            id = 11,
            name = "Deutsch", // German
            code = LANG.DE,
            icon = "🇩🇪"
        ),
        Language(
            id = 12,
            name = "Italiano", // Italian
            code = LANG.IT,
            icon = "🇮🇹"
        ),
        Language(
            id = 13,
            name = "Polski", // Polish
            code = LANG.PL,
            icon = "🇵🇱"
        ),

        Language(
            id = 14,
            name = "Português (Brasil)",
            code = LANG.BR,
            icon = "🇧🇷"
        ),

        Language(
            id = 15,
            name = "Français", // French
            code = LANG.FR,
            icon = "🇫🇷"
        ),
        Language(
            id = 16,
            name = "Tiếng Việt", // Vietnamese
            code = LANG.VI,
            icon = "🇻🇳"
        ),
        Language(
            id = 17,
            name = "العربية", // Arabic
            code = LANG.AR,
            icon = "🇸🇦"
        ),
        Language(
            id = 18,
            name = "Русский", // Russian
            code = LANG.RU,
            icon = "🇷🇺"
        ),
        Language(
            id = 19,
            name = "日本語", // Japanese
            code = LANG.JA,
            icon = "🇯🇵"
        ),
        Language(
            id = 20,
            name = "简体中文", // Chinese Simplified
            code = LANG.ZH,
            icon = "🇨🇳"
        ),
        Language(
            id = 21,
            name = "繁體中文", // Chinese Traditional
            code = LANG.TW,
            icon = "🇹🇼"
        ),
        Language(
            id = 22,
            name = "Čeština", // Czech
            code = LANG.CS,
            icon = "🇨🇿"
        ),
        Language(
            id = 23,
            name = "ไทย", // Thai
            code = LANG.TH,
            icon = "🇹🇭"
        )
    )
}