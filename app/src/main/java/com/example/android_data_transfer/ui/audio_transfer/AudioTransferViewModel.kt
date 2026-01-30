package com.example.android_data_transfer.ui.audio_transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_data_transfer.models.local.dao.HistoryDao
import com.example.android_data_transfer.models.local.entity.TransferHistory
import com.example.android_data_transfer.utils.nav.AppNav
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class AudioTransferViewModel @Inject constructor(
    private val appNav: AppNav,
    private val historyDao: HistoryDao,
) : ViewModel() {

    val historyEntries = historyDao.getAllHistory()

    fun saveHistory(fileName: String, fileSize: Long, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.insertHistory(
                TransferHistory(fileName = fileName, fileSize = fileSize, type = type)
            )
        }
    }
}
