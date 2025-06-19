package com.project.labs.nutrigrow.activity.vaccine

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
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
import com.project.labs.nutrigrow.ui.screen.vaccine.add.AddVaccineScreen
import com.project.labs.nutrigrow.ui.theme.NutriGrowTheme

@OptIn(ExperimentalMaterial3Api::class)
class AddVaccineActivity : ComponentActivity() {
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
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color(0xFF00BF63),
                                    navigationIconContentColor = MaterialTheme.colorScheme.background,
                                    titleContentColor = MaterialTheme.colorScheme.background
                                ),
                                title = {
                                    Text(
                                        text = "Tambahkan Data Imunisasi",
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
                        if (childId != null) {
                            AddVaccineScreen(
                                id = childId,
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