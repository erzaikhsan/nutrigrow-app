package com.project.labs.nutrigrow.activity.validation

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.project.labs.nutrigrow.ui.component.topbar.NutriTopBar
import com.project.labs.nutrigrow.ui.screen.validation.ValidationScreen
import com.project.labs.nutrigrow.ui.theme.NutriGrowTheme

class ValidationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NutriGrowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val activity = (LocalContext.current as Activity)

                    Scaffold(
                        topBar = {
                            NutriTopBar(
                                title = "Uji Validasi Z-Score",
                                onBack = {
                                    setResult(RESULT_OK)
                                    activity.finish()
                                },
                            )
                        },
                    ) { innerPadding ->
                        ValidationScreen(
                            modifier = Modifier.padding(innerPadding),
                            redirectToHome = {
                                setResult(RESULT_OK)
                                activity.finish()
                            },
                        )
                    }
                }
            }
        }
    }
}
