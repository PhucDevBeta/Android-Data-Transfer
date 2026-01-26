package com.example.android_data_transfer.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.TextUnit
import com.example.android_data_transfer.R


val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)


val fontLexend = FontFamily(
    Font(googleFont = GoogleFont("Quicksand"), fontProvider = provider)
)

val fontLexendBold = FontFamily(
    Font(
        googleFont = GoogleFont("Quicksand"),
        fontProvider = provider,
        weight = FontWeight.ExtraBold
    )
)

val fontLexendMedium = FontFamily(
    Font(googleFont = GoogleFont("Quicksand"), fontProvider = provider, weight = FontWeight.Medium)
)

val fontLexendLight = FontFamily(
    Font(googleFont = GoogleFont("Quicksand"), fontProvider = provider, weight = FontWeight.Light)
)

fun TextStyleCommon(
    color: Color = Color.Black,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = FontWeight.Medium,
    fontStyle: FontStyle? = null,
    fontSynthesis: FontSynthesis? = null,
    fontFeatureSettings: String? = null,
    fontFamily: FontFamily = fontLexend,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    baselineShift: BaselineShift? = null,
    textGeometricTransform: TextGeometricTransform? = null,
    localeList: LocaleList? = null,
    background: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    shadow: Shadow? = null,
    drawStyle: DrawStyle? = null,
    textAlign: TextAlign = TextAlign.Unspecified,
    textDirection: TextDirection = TextDirection.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    textIndent: TextIndent? = null,
    platformStyle: PlatformTextStyle? = null,
    lineHeightStyle: LineHeightStyle? = null,
    lineBreak: LineBreak = LineBreak.Unspecified,
    hyphens: Hyphens = Hyphens.Unspecified,
    textMotion: TextMotion? = null,
): TextStyle {
    return TextStyle(
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        fontSynthesis = fontSynthesis,
        fontFamily = fontFamily,
        fontFeatureSettings = fontFeatureSettings,
        letterSpacing = letterSpacing,
        baselineShift = baselineShift,
        textGeometricTransform = textGeometricTransform,
        localeList = localeList,
        background = background,
        textDecoration = textDecoration,
        shadow = shadow,
        drawStyle = drawStyle,
        textAlign = textAlign,
        textDirection = textDirection,
        lineHeight = lineHeight,
        textIndent = textIndent,
        lineHeightStyle = lineHeightStyle,
        lineBreak = lineBreak,
        hyphens = hyphens,
        textMotion = textMotion,
        platformStyle = platformStyle,
    )
}