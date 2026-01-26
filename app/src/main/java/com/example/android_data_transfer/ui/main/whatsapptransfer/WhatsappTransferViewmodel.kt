package com.example.android_data_transfer.ui.main.whatsapptransfer

import androidx.lifecycle.ViewModel
import com.example.android_data_transfer.utils.nav.AppNav
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WhatsappTransferViewmodel @Inject constructor(
    private val appNav: AppNav
): ViewModel() {
}