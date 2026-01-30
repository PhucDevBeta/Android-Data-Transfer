package com.example.android_data_transfer.ui.main.compoents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_data_transfer.ui.theme.TextStyleCommon
import com.example.android_data_transfer.ui.theme.fontLexendBold
import com.example.android_data_transfer.utils.system.clickableOnce

@Composable
fun TransferMethodButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconRes: Int,
    enabled: Boolean,
    primaryColor: Color,
    onClick: () -> Unit,
) {
    val alpha = if (enabled) 1f else 0.4f
    Column(
        modifier = modifier
            .graphicsLayer(alpha = alpha)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(
                1.dp,
                if (enabled) primaryColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(20.dp)
            )
            .clickableOnce(enabled = enabled) { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(primaryColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(primaryColor)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = TextStyleCommon(
                fontSize = 16.sp,
                fontFamily = fontLexendBold,
                color = Color.White
            )
        )
        Text(
            text = subtitle,
            style = TextStyleCommon(
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        )
    }
}
