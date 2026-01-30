package com.example.android_data_transfer.component.topbar_common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBarCommon(
    title: String,
    @DrawableRes logoRes: Int,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
        color = Color.White
    ),
    logoSize: Int = 40
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = titleStyle,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "Logo $title",
            modifier = Modifier.size(logoSize.dp),
            contentScale = ContentScale.Fit
        )
    }
}