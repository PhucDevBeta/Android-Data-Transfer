package com.example.android_data_transfer.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.android_data_transfer.ui.main.bottom_nav.BottomNav
import com.example.android_data_transfer.ui.main.bottom_nav.BottomNavigator
import com.example.android_data_transfer.ui.main.history.HistoryScreen
import com.example.android_data_transfer.ui.main.phoneclone.PhoneCloneScreen
import com.example.android_data_transfer.ui.main.phonetransfer.PhoneTransferSceen
import com.example.android_data_transfer.ui.main.whatsapptransfer.WhatsAppTransferScreen

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MainScreen(
    currentIndexTab: Int = 0,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(initialPage = currentIndexTab) { 4 }

    val selectedTab = when (currentIndexTab) {
        0 -> BottomNav.PHONETRANSFER
        1 -> BottomNav.PHONECLONE
        2 -> BottomNav.WHATSAPPTRANSFER
        3 -> BottomNav.HISTORY
        else -> BottomNav.PHONETRANSFER
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        // Nội dung các tab
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> PhoneTransferSceen()
                1 -> PhoneCloneScreen()
                2 -> WhatsAppTransferScreen()
                3 -> HistoryScreen()
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .background(Color.Transparent)
        ) {
            BottomNavigator(
                modifier = Modifier.fillMaxWidth(),
                onClick = { tab ->
                    val targetPage = when (tab) {
                        BottomNav.PHONETRANSFER -> 0
                        BottomNav.PHONECLONE -> 1
                        BottomNav.WHATSAPPTRANSFER -> 2
                        BottomNav.HISTORY -> 3
                    }
                    mainViewModel.selectTab(targetPage)
                },
                typeSelected = selectedTab,
            )
            Spacer(
                modifier = Modifier
                    .height(0.5.dp)
                    .fillMaxWidth()
                    .background(color = Color.Black)
            )
        }
    }

    LaunchedEffect(currentIndexTab) {
        pagerState.animateScrollToPage(currentIndexTab)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
@Preview(showSystemUi = true)
fun MainScreenP() {
    MainScreen(
        currentIndexTab = 0,
    )
}