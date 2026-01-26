package com.example.android_data_transfer

import androidx.lifecycle.ViewModel
import com.example.android_data_transfer.utils.nav.AppNav
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppMainViewModel @Inject constructor(
    val appNav: AppNav
) : ViewModel() {
}