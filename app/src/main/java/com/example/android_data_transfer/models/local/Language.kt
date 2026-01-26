package com.example.android_data_transfer.models.local

import androidx.annotation.Keep

@Keep
data class Language(
    val id: Int = 0,
    val name: String = "",
    val code: LANG = LANG.EN,
    val selected: Boolean = false,
    val icon: String = ""
)

@Keep
enum class LANG {
    AR, CS, DE, ES, FR, HI, IN, IT, JA, KO, MS, PHI, PL, PT, RU, TR, VI, ZH, EN, TH, TW, EN_US, BR
}