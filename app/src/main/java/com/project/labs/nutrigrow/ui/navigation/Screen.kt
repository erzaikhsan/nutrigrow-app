package com.project.labs.nutrigrow.ui.navigation

sealed class Screen(val route: String) {
    //Home
    data object Home : Screen("home")

    //CheckUp

    // ChatBot
    data object Event : Screen("event")

    //Search
    data object Children : Screen("children")

    // Account
    data object Profile : Screen("profile")
    // Auth
    data object Login : Screen("login")
    data object CreateAccount : Screen("create_account")
    data object Verify : Screen("verify")
    data object Register : Screen("register")
    data object Welcome : Screen("welcome")
}