package com.project.labs.nutrigrow.activity.growth

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import com.project.labs.nutrigrow.ui.screen.growth.update.UpdateGrowthScreen
import com.project.labs.nutrigrow.ui.theme.NutriGrowTheme

@OptIn(ExperimentalMaterial3Api::class)
class UpdateGrowthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val growthId = intent.getStringExtra("id")
        val childId = intent.getStringExtra("childId")

        setContent {
            NutriGrowTheme {
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
                                        text = "Ubah Data Penimbangan",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            activity.finish()
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
                        if (growthId != null && childId != null) {
                            UpdateGrowthScreen(
                                id = growthId,
                                childId = childId,
                                modifier = Modifier.padding(innerPadding),
                                redirectToHome = {
                                    setResult(RESULT_OK)
                                    activity.finish()
                                })
                        }
                    }
                }
            }
        }
    }
}