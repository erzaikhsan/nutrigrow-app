package com.project.labs.nutrigrow.activity

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.project.labs.nutrigrow.NutriGrowApp
import com.project.labs.nutrigrow.ui.theme.NutriGrowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()

        installSplashScreen()

        setContent {
            NutriGrowTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NutriGrowApp()
                }
            }
        }
    }

    // Fungsi untuk meminta izin notifikasi jika Android 13+
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    // (Opsional) Terima hasil permintaan izin
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission Notification", "Izin notifikasi diberikan")
            } else {
                Log.d("Permission Notification", "Izin notifikasi ditolak")
            }
        }
    }
}