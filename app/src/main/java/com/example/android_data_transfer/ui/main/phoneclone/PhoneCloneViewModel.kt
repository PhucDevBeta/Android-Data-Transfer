package com.example.android_data_transfer.ui.main.phoneclone

import androidx.lifecycle.ViewModel
import com.example.android_data_transfer.utils.nav.AppNav
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhoneCloneViewModel @Inject constructor(
    private val appNav: AppNav
) : ViewModel() {
}