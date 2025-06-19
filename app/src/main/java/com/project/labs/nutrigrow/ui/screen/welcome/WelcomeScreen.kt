package com.project.labs.nutrigrow.ui.screen.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    message: String,
    navController: NavHostController = rememberNavController(),
) {
    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = message) {
        if (message.isNotEmpty()) {
            coroutineScope.launch {
                snackState.showSnackbar(message)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Image(
                painter = painterResource(id = R.drawable.nutrigrow_logo_nobg),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(45.dp))
            Text(
                text = "Selamat Datang Di NutriGrow",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.secondaryContainer,
            )
            Text(
                text = "Mari mulai perjalanan si kecil membangun generasi sehat!",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Masuk",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.CreateAccount.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary,),
                modifier = Modifier
                    .width(250.dp)
                    .height(45.dp)
            ) {
                Text(
                    "Registrasi",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            hostState = snackState
        ) { snackbarData: SnackbarData ->
            CustomSnackBar(
                drawableRes = R.drawable.baseline_error_outline_24,
                message = snackbarData.visuals.message,
                containerColor = MaterialTheme.colorScheme.error,
            )
        }
    }
}