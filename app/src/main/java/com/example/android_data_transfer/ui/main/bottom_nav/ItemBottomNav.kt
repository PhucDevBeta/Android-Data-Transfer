package com.example.android_data_transfer.ui.main.bottom_nav

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_data_transfer.utils.system.clickableOnce

@Composable
fun ItemBottomNav(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickableOnce {
            onClick()
        }, horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.W700 else FontWeight.W400,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
    }
}