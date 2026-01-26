package com.example.android_data_transfer.utils.system

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object SystemUiManager {
    fun applyImmersiveNavigation(activity: Activity) {
        hideNavigationBarOnly(activity.window)
        setupStickyUiListener(activity)
    }

    private fun hideNavigationBarOnly(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.navigationBars()) //  chỉ ẩn navigation
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    private fun setupStickyUiListener(activity: Activity) {
        @Suppress("DEPRECATION")
        activity.window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if ((visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                hideNavigationBarOnly(activity.window)
            }
        }
    }
}