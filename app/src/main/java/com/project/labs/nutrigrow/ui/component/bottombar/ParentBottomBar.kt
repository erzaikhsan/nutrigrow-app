package com.project.labs.nutrigrow.ui.component.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.ui.navigation.NavigationItem
import com.project.labs.nutrigrow.ui.navigation.Screen

@Composable
fun ParentBottomBar(navController: NavHostController, modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 50.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.home_nav),
                icon = Icons.Rounded.Home,
                screen = Screen.Home
            ),
            NavigationItem(
                title = stringResource(R.string.profile_nav),
                icon = Icons.Rounded.AccountCircle,
                screen = Screen.Profile
            ),
        )
        navigationItems.map { item ->
            var isSelected: Boolean = currentRoute == item.screen.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                selected = isSelected,
                label = { Text(item.title) },
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}