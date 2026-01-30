package com.example.android_data_transfer.ui.main.home

import androidx.lifecycle.ViewModel
import com.example.android_data_transfer.utils.nav.AppNav
import com.example.android_data_transfer.utils.nav.AudioTransferRoute
import com.example.android_data_transfer.utils.nav.FilesTransferRoute
import com.example.android_data_transfer.utils.nav.PhotoTransferRoute
import com.example.android_data_transfer.utils.nav.VideoTransferRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appNav: AppNav
) : ViewModel() {
    fun ClickPhotoItem() {
        appNav.navigateTo(PhotoTransferRoute)
    }

    fun ClickVideoItem() {
        appNav.navigateTo(VideoTransferRoute)
    }

    fun ClickFileItem() {
        appNav.navigateTo(FilesTransferRoute)
    }

    fun ClickAudioItem() {
        appNav.navigateTo(AudioTransferRoute)
    }
}