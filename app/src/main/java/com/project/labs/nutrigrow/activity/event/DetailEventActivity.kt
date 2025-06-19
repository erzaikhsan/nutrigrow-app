package com.project.labs.nutrigrow.activity.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.project.labs.nutrigrow.activity.MainActivity
import com.project.labs.nutrigrow.ui.screen.event.detail.DetailEventScreen
import com.project.labs.nutrigrow.ui.theme.NutriGrowTheme

@OptIn(ExperimentalMaterial3Api::class)
class DetailEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val eventId = intent.getStringExtra("id")
        val isFromNotification = intent.getBooleanExtra("from_notification", false)

        setContent {
            NutriGrowTheme {
                val activity = (LocalContext.current as Activity)

                BackHandler {
                    if (isFromNotification) {
                        val homeIntent = Intent(activity, MainActivity::class.java)
                        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        activity.startActivity(homeIntent)
                    } else {
                        activity.setResult(RESULT_OK)
                        activity.finish()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val activity = (LocalContext.current as Activity)

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color(0xFF00BF63),
                                    navigationIconContentColor = MaterialTheme.colorScheme.background,
                                    titleContentColor = MaterialTheme.colorScheme.background
                                ),
                                title = {
                                    Text(
                                        text = "Detail Kegiatan",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            if (isFromNotification) {
                                                val homeIntent = Intent(activity, MainActivity::class.java)
                                                homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                activity.startActivity(homeIntent)
                                            } else {
                                                setResult(RESULT_OK)
                                                activity.finish()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                },
                            )
                        },
                    )
                    { innerPadding ->
                        if (eventId != null) {
                            DetailEventScreen( id = eventId, modifier = Modifier.padding(innerPadding), redirectToHome = {
                                if (isFromNotification) {
                                    val homeIntent = Intent(activity, MainActivity::class.java)
                                    homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    activity.startActivity(homeIntent)
                                } else {
                                    setResult(RESULT_OK)
                                    activity.finish()
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}