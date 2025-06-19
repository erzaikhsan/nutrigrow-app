package com.project.labs.nutrigrow.activity.parent

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
import com.project.labs.nutrigrow.ui.screen.parent.ParentProfileScreen
import com.project.labs.nutrigrow.ui.theme.NutriGrowTheme

@OptIn(ExperimentalMaterial3Api::class)
class ParentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parentId = intent.getStringExtra("id")

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
                                        text = "Profil Orang Tua",
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
                        if (parentId != null) {
                            ParentProfileScreen( id = parentId, modifier = Modifier.padding(innerPadding), redirectToHome = {
                                setResult(Activity.RESULT_OK)
                                activity.finish()
                            })
                        }
                    }
                }
            }
        }
    }
}