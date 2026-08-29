package com.project.labs.nutrigrow.ui.component.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.ui.navigation.NavigationItem
import com.project.labs.nutrigrow.ui.navigation.Screen
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextSecondary

@Composable
fun NutriBottomBar(navController: NavHostController, modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = SurfaceCard,
        tonalElevation = 0.dp,
        modifier = modifier,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.home_nav),
                icon = Icons.Rounded.Home,
                screen = Screen.Home,
            ),
            NavigationItem(
                title = stringResource(R.string.children_nav),
                icon = Icons.Rounded.Face,
                screen = Screen.Children,
            ),
            NavigationItem(
                title = stringResource(R.string.event_nav),
                icon = Icons.Rounded.DateRange,
                screen = Screen.Event,
            ),
            NavigationItem(
                title = stringResource(R.string.profile_nav),
                icon = Icons.Rounded.AccountCircle,
                screen = Screen.Profile,
            ),
        )

        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                    )
                },
                selected = currentRoute == item.screen.route,
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandGreen,
                    selectedTextColor = BrandGreen,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = BrandGreenSoft,
                ),
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
