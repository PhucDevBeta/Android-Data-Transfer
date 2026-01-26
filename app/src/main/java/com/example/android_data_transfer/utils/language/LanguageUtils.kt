package com.example.android_data_transfer.utils.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.android_data_transfer.models.local.LANG

fun changeLanguage(lang: LANG) {
    val localeCode = when (lang) {
        LANG.AR -> "ar"
        LANG.CS -> "cs"
        LANG.DE -> "de"
        LANG.ES -> "es"
        LANG.FR -> "fr"
        LANG.HI -> "hi"
        LANG.IN -> "id"
        LANG.IT -> "it"
        LANG.JA -> "ja"
        LANG.KO -> "ko"
        LANG.MS -> "ms"
        LANG.PHI -> "fil"
        LANG.PL -> "pl"
        LANG.PT -> "pt-PT"
        LANG.BR -> "pt-BR"
        LANG.RU -> "ru"
        LANG.TR -> "tr"
        LANG.VI -> "vi"
        LANG.ZH -> "zh-CN"
        LANG.TW -> "zh-TW"
        LANG.TH -> "th"
        LANG.EN, LANG.EN_US -> "en"
    }

    val localeList = LocaleListCompat.forLanguageTags(localeCode)
    AppCompatDelegate.setApplicationLocales(localeList)
    ManagerSaveLocal.setLanguageApp(lang)
}

fun getSelectLanguageTitle(lang: LANG): String {
    return when (lang) {
        LANG.AR -> "اختر اللغة"
        LANG.CS -> "Vyberte jazyk"
        LANG.DE -> "Sprache wählen"
        LANG.ES -> "Seleccionar idioma"
        LANG.FR -> "Sélectionner la langue"
        LANG.HI -> "भाषा चुनें"
        LANG.IN -> "Pilih Bahasa"
        LANG.IT -> "Seleziona lingua"
        LANG.JA -> "言語を選択"
        LANG.KO -> "언어 선택"
        LANG.MS -> "Pilih Bahasa"
        LANG.PHI -> "Pumili ng Wika"
        LANG.PL -> "Wybierz język"
        LANG.PT -> "Selecionar idioma"
        LANG.BR -> "Selecionar idioma"
        LANG.RU -> "Выберите язык"
        LANG.TR -> "Dil Seçin"
        LANG.VI -> "Chọn ngôn ngữ"
        LANG.ZH -> "选择语言"
        LANG.TW -> "選擇語言"
        LANG.TH -> "เลือกภาษา"
        LANG.EN, LANG.EN_US -> "Select Language"
    }
}

fun getDoneButtonText(lang: LANG): String {
    return when (lang) {
        LANG.AR -> "تم"
        LANG.CS -> "Hotovo"
        LANG.DE -> "Fertig"
        LANG.ES -> "Listo"
        LANG.FR -> "Terminé"
        LANG.HI -> "पूर्ण"
        LANG.IN -> "Selesai"
        LANG.IT -> "Fatto"
        LANG.JA -> "完了"
        LANG.KO -> "완료"
        LANG.MS -> "Selesai"
        LANG.PHI -> "Tapos"
        LANG.PL -> "Gotowe"
        LANG.PT -> "Concluído"
        LANG.BR -> "Concluído"
        LANG.RU -> "Готово"
        LANG.TR -> "Tamam"
        LANG.VI -> "Xong"
        LANG.ZH -> "完成"
        LANG.TW -> "完成"
        LANG.TH -> "เสร็จสิ้น"
        LANG.EN, LANG.EN_US -> "Done"
    }
}