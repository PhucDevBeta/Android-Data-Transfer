package com.example.android_data_transfer.utils.nav

import androidx.navigation.NavHostController

class NavAction(private val navController: NavHostController) {
    private fun <T : Any> navSingleToTop(route: T) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}