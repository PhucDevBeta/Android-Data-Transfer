package com.example.android_data_transfer.utils.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.android_data_transfer.AppMainActivity
import com.example.android_data_transfer.AppMainViewModel
import com.example.android_data_transfer.ui.main.MainScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavHost(
    navController: NavHostController,
    navAction: NavAction,
    startRouter: BaseRoute,
    context: AppMainActivity,
) {
    val appMainViewModel: AppMainViewModel = hiltViewModel()
    var selectedIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        appMainViewModel.appNav.navigationEvents.collect { intent ->
            when (intent) {
                is NavIntent.To -> {
                    navController.navigate(intent.route) {
                        if (intent.clearBackStack) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                }

                is NavIntent.Back -> {
                    navController.popBackStack()
                }

                is NavIntent.PopUpTo -> {
                    navController.navigate(intent.destination) {
                        popUpTo(intent.route) {
                            inclusive = intent.inclusive
                        }
                    }
                }

                is NavIntent.Tab -> {
                    selectedIndex = intent.index
                }
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = startRouter
    ) {
        composable<MainRoute> {
            MainScreen(
                currentIndexTab = selectedIndex,
            )
        }
    }
}