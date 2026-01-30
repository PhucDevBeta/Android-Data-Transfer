package com.example.android_data_transfer.ui.main.bottom_nav

import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.android_data_transfer.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BottomNavigator(
    modifier: Modifier = Modifier,
    typeSelected: BottomNav = BottomNav.HOME,
    onClick: (BottomNav) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(5f)
            .background(color = Color(0xFF0F172A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically
            ) {
                ItemBottomNav(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .weight(1f)
                        .fillMaxHeight(),
                    icon = if (typeSelected == BottomNav.HOME) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_background,
                    title = "Home",
                    selected = typeSelected == BottomNav.HOME,
                    onClick = {
                        onClick(BottomNav.HOME)
                    }
                )

                ItemBottomNav(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .weight(1f)
                        .fillMaxHeight(),
                    icon = if (typeSelected == BottomNav.PHONECLONE) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_background,
                    title = "Phone Clone",
                    selected = typeSelected == BottomNav.PHONECLONE,
                    onClick = {
                        onClick(BottomNav.PHONECLONE)
                    }
                )
                ItemBottomNav(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .weight(1f)
                        .fillMaxHeight(),
                    icon = if (typeSelected == BottomNav.SETTING) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_background,
                    title = "Setting",
                    selected = typeSelected == BottomNav.SETTING,
                    onClick = {
                        onClick(BottomNav.SETTING)
                    }
                )
            }
        }
    }
}


@Composable
@Preview
fun BottomNavP() {
    BottomNavigator(
        typeSelected = BottomNav.HOME,
        onClick = {}
    )
}

@Keep
enum class BottomNav {
    HOME, PHONECLONE, SETTING
}