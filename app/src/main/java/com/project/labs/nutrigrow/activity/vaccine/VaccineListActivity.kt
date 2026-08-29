package com.project.labs.nutrigrow.activity.vaccine

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
import com.project.labs.nutrigrow.ui.screen.vaccine.list.VaccineListScreen
import com.project.labs.nutrigrow.ui.theme.NutriGrowTheme

class VaccineListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val childId = intent.getStringExtra("id")

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
                                title = "Imunisasi Balita",
                                onBack = {
                                    setResult(RESULT_OK)
                                    activity.finish()
                                },
                            )
                        },
                    ) { innerPadding ->
                        if (childId != null) {
                            VaccineListScreen(
                                id = childId,
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
}
