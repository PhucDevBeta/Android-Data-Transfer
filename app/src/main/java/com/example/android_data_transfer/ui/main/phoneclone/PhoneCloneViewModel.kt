package com.example.android_data_transfer.ui.main.phoneclone

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_data_transfer.R
import com.example.android_data_transfer.models.CloneCategory
import com.example.android_data_transfer.models.CloneType
import com.example.android_data_transfer.utils.DataFetcher
import com.example.android_data_transfer.utils.nav.AppNav
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PhoneCloneViewModel @Inject constructor(
    private val appNav: AppNav,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CloneCategory>>(emptyList())
    val categories: StateFlow<List<CloneCategory>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDataInfo()
    }

    fun loadDataInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val contacts = DataFetcher.fetchContacts(context)
            val sms = DataFetcher.fetchSMS(context)
            val photos = DataFetcher.fetchPhotos(context)
            val docs = DataFetcher.fetchDocuments(context)

            val categoryList = listOf(
                CloneCategory(
                    "contacts", "Số điện thoại", R.drawable.ic_copy, // Replace with proper icon
                    contacts.size, contacts.size * 100L, type = CloneType.CONTACTS
                ),
                CloneCategory(
                    "photos", "Ảnh trong máy", R.drawable.ic_upload, // Replace with proper icon
                    photos.size, photos.sumOf { it.length() }, type = CloneType.PHOTOS
                ),
                CloneCategory(
                    "docs", "Tài liệu", R.drawable.ic_copy, // Replace with proper icon
                    docs.size, docs.sumOf { it.length() }, type = CloneType.DOCUMENTS
                ),
                CloneCategory(
                    "sms", "Tin nhắn SMS", R.drawable.ic_copy, // Replace with proper icon
                    sms.size, sms.size * 200L, type = CloneType.SMS
                )
            )
            _categories.value = categoryList
            _isLoading.value = false
        }
    }

    fun toggleCategory(categoryId: String) {
        _categories.value = _categories.value.map {
            if (it.id == categoryId) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun getSelectedFiles(): List<File> {
        val selectedFiles = mutableListOf<File>()
        val gson = Gson()

        _categories.value.forEach { category ->
            if (category.isSelected) {
                when (category.type) {
                    CloneType.CONTACTS -> {
                        val contacts = DataFetcher.fetchContacts(context)
                        val file = File(context.cacheDir, "contacts.json")
                        file.writeText(gson.toJson(contacts))
                        selectedFiles.add(file)
                    }
                    CloneType.SMS -> {
                        val sms = DataFetcher.fetchSMS(context)
                        val file = File(context.cacheDir, "sms.json")
                        file.writeText(gson.toJson(sms))
                        selectedFiles.add(file)
                    }
                    CloneType.PHOTOS -> {
                        selectedFiles.addAll(DataFetcher.fetchPhotos(context))
                    }
                    CloneType.DOCUMENTS -> {
                        selectedFiles.addAll(DataFetcher.fetchDocuments(context))
                    }
                }
            }
        }
        return selectedFiles
    }
}