package com.example.android_data_transfer.ui.main.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.android_data_transfer.R
import com.example.android_data_transfer.component.topbar_common.TopBarCommon

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val imageList = listOf(
        R.drawable.photo_transfer,
        R.drawable.audio_transfer,
        R.drawable.video_transfer,
        R.drawable.files_transfer,
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarCommon(
            title = stringResource(R.string.app_name),
            logoRes = R.drawable.ic_premium
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(imageList) { imageRes ->
                GlideImage(
                    model = imageRes,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(horizontal = 20.dp)
                        .clickable {
                            when (imageRes) {
                                R.drawable.photo_transfer -> homeViewModel.ClickPhotoItem()
                                R.drawable.video_transfer -> homeViewModel.ClickVideoItem()
                                R.drawable.files_transfer -> homeViewModel.ClickFileItem()
                                R.drawable.audio_transfer -> homeViewModel.ClickAudioItem()
                            }
                        }
                )
            }
        }
    }
}
