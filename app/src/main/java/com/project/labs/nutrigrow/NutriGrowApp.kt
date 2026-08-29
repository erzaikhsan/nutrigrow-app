package com.project.labs.nutrigrow

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.labs.nutrigrow.ui.component.bottombar.NutriBottomBar
import com.project.labs.nutrigrow.ui.navigation.Screen
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.screen.auth.CreateAccountScreen
import com.project.labs.nutrigrow.ui.screen.auth.ForgotPasswordScreen
import com.project.labs.nutrigrow.ui.screen.auth.LoginScreen
import com.project.labs.nutrigrow.ui.screen.auth.RegisterScreen
import com.project.labs.nutrigrow.ui.screen.auth.VerifyScreen
import com.project.labs.nutrigrow.ui.screen.child.list.ChildrenScreen
import com.project.labs.nutrigrow.ui.screen.event.EventScreen
import com.project.labs.nutrigrow.ui.screen.home.HomeScreen
import com.project.labs.nutrigrow.ui.screen.profile.ProfileScreen
import com.project.labs.nutrigrow.ui.screen.splash.SplashScreen
import com.project.labs.nutrigrow.ui.screen.welcome.WelcomeScreen
import com.project.labs.nutrigrow.ui.state.UiState

@Composable
fun NutriGrowApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var message by remember { mutableStateOf("") }
    val redirectToWelcome = { messageParam: String ->
        message = messageParam
        navController.navigate(Screen.Welcome.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    val checkAuth by viewModel.isAuthenticated

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAuthentication()
    }

    when (checkAuth) {
        is UiState.Loading -> {
            SplashScreen()
        }

        is UiState.Success -> {
            Scaffold(
                bottomBar = {
                    if (Screen.showsBottomBar(currentRoute)) {
                        NutriBottomBar(navController = navController)
                    }
                },
                modifier = modifier
            )
            { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = if ((checkAuth as UiState.Success<Boolean>).data) {
                        Screen.Home.route
                    } else {
                        Screen.Welcome.route
                    },
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            redirectToWelcome = { messageParam: String -> redirectToWelcome(messageParam) },
                        )
                    }
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(navController = navController, message = message)
                    }
                    composable(Screen.Login.route) {
                        LoginScreen(navController = navController, redirectToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        })
                    }
                    composable(Screen.ForgotPassword.route) {
                        ForgotPasswordScreen(navController = navController, redirectToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                                launchSingleTop = true
                            }
                        })
                    }
                    composable(Screen.CreateAccount.route) {
                        CreateAccountScreen(navController = navController, redirectToVerify = {
                            navController.navigate(Screen.Verify.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                                launchSingleTop = true
                            }
                        })
                    }
                    composable(Screen.Verify.route) {
                        VerifyScreen(navController = navController, redirectToRegister = {
                            navController.navigate(Screen.Register.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                            }
                        })
                    }
                    composable(Screen.Register.route) {
                        RegisterScreen(navController = navController, redirectToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = false
                                }
                            }
                        })
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            redirectToWelcome = { messageParam: String -> redirectToWelcome(messageParam) },
                        )
                    }
                    composable(Screen.Children.route) {
                        ChildrenScreen(
                            redirectToWelcome = { messageParam: String -> redirectToWelcome(messageParam) },
                        )
                    }
                    composable(Screen.Event.route) {
                        EventScreen(
                            redirectToHome = { messageParam: String -> redirectToWelcome(messageParam) },
                        )
                    }
                }
            }
        }

        else -> {}
    }
}