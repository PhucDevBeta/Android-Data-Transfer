package com.example.android_data_transfer.utils.nav

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNav @Inject constructor() {
    private val _navigationEvents = MutableSharedFlow<NavIntent>(extraBufferCapacity = 64)
    val navigationEvents = _navigationEvents.asSharedFlow()

    // --- Biến StateFlow lưu lịch sử điều hướng ---
    private val _navHistory = MutableStateFlow<List<BaseRoute>>(emptyList())
    val navHistory = _navHistory.asStateFlow()

    private var lastNavTime = 0L
    private val navThreshold = 500L

    fun navigateTo(route: BaseRoute, clearBackStack: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNavTime >= navThreshold) {
            lastNavTime = currentTime

            // Cập nhật lịch sử
            if (clearBackStack) {
                _navHistory.value = listOf(route)
            } else {
                _navHistory.value = _navHistory.value + route
            }

            _navigationEvents.tryEmit(NavIntent.To(route, clearBackStack))
        }
    }

    fun popBackStack() {
        // Cập nhật lịch sử: xóa phần tử cuối cùng
        val currentHistory = _navHistory.value
        if (currentHistory.isNotEmpty()) {
            _navHistory.value = currentHistory.dropLast(1)
        }

        _navigationEvents.tryEmit(NavIntent.Back)
    }

    fun navigateAndPopUpTo(destination: BaseRoute, popUpTo: BaseRoute, inclusive: Boolean = true) {
        // Logic cập nhật history phức tạp hơn cho PopUpTo
        val currentHistory = _navHistory.value
        val index = currentHistory.indexOfLast { it == popUpTo }

        if (index != -1) {
            val newHistory = if (inclusive) {
                currentHistory.subList(0, index) + destination
            } else {
                currentHistory.subList(0, index + 1) + destination
            }
            _navHistory.value = newHistory
        } else {
            // Nếu không tìm thấy popUpTo trong history, coi như cộng dồn
            _navHistory.value = currentHistory + destination
        }

        _navigationEvents.tryEmit(NavIntent.PopUpTo(popUpTo, destination, inclusive))
    }

    fun selectTab(index: Int) {
        _navigationEvents.tryEmit(NavIntent.Tab(index))
    }
}

sealed class NavIntent {
    data class To(val route: BaseRoute, val clearBackStack: Boolean = false) : NavIntent()
    data object Back : NavIntent()
    data class PopUpTo(val route: BaseRoute, val destination: BaseRoute, val inclusive: Boolean) :
        NavIntent()

    data class Tab(val index: Int) : NavIntent()
}