package com.project.labs.nutrigrow.ui.navigation

sealed class Screen(val route: String, val showsBottomBar: Boolean = false) {
    data object Home : Screen("home", showsBottomBar = true)
    data object Children : Screen("children", showsBottomBar = true)
    data object Event : Screen("event", showsBottomBar = true)
    data object Profile : Screen("profile", showsBottomBar = true)

    data object Login : Screen("login")
    data object CreateAccount : Screen("create_account")
    data object Verify : Screen("verify")
    data object Register : Screen("register")
    data object Welcome : Screen("welcome")
    data object ForgotPassword : Screen("forgot_password")

    companion object {
        private val all = listOf(
            Home, Children, Event, Profile,
            Login, CreateAccount, Verify, Register, Welcome, ForgotPassword,
        )

        fun showsBottomBar(route: String?): Boolean =
            all.any { it.route == route && it.showsBottomBar }
    }
}
