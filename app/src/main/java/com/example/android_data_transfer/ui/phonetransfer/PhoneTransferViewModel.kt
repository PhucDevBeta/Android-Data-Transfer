package com.example.android_data_transfer.ui.phonetransfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_data_transfer.utils.nav.AppNav
import com.example.android_data_transfer.models.local.dao.HistoryDao
import com.example.android_data_transfer.models.local.entity.TransferHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneTransferViewModel @Inject constructor(
    private val appNav: AppNav,
    private val historyDao: HistoryDao,
) : ViewModel() {
    fun saveHistory(fileName: String, fileSize: Long, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.insertHistory(
                TransferHistory(fileName = fileName, fileSize = fileSize, type = type)
            )
        }
    }
}
