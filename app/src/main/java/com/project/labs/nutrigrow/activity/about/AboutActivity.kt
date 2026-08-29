package com.project.labs.nutrigrow.activity.about

import android.app.Activity
import android.content.Intent
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
import com.project.labs.nutrigrow.ui.screen.about.AboutScreen
import com.project.labs.nutrigrow.ui.theme.NutriGrowTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val role = intent.getStringExtra("role") ?: ""

        setContent {
            NutriGrowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val activity = (context as Activity)

                    Scaffold(
                        topBar = {
                            NutriTopBar(
                                title = "About NutriGrow",
                                onBack = {
                                    setResult(RESULT_OK)
                                    activity.finish()
                                },
                            )
                        },
                    ) { innerPadding ->
                        AboutScreen(
                            role = role,
                            onOpenTopic = { id ->
                                context.startActivity(
                                    Intent(context, AboutDetailActivity::class.java).apply {
                                        putExtra("topic", id)
                                    }
                                )
                            },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}
