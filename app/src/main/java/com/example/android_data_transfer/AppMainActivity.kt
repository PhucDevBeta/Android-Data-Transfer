package com.example.android_data_transfer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.android_data_transfer.utils.nav.AppNavHost
import com.example.android_data_transfer.utils.nav.MainRoute
import com.example.android_data_transfer.utils.nav.NavAction
import com.example.android_data_transfer.utils.system.SystemUiManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppMainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navAction = remember(navController) { NavAction(navController) }

            AppNavHost(
                navController = navController,
                navAction = navAction,
                startRouter = MainRoute,
                context = this
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            SystemUiManager.applyImmersiveNavigation(this)
        }
    }
}