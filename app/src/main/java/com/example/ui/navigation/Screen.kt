package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Map : Screen("map", "Map", Icons.Filled.Map, Icons.Outlined.Map)
    object Assistant : Screen("assistant", "Assistant", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat)
    object Notices : Screen("notices", "Notices", Icons.Filled.Campaign, Icons.Outlined.Campaign)
    object Timetable : Screen("timetable", "Timetable", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Faculty : Screen("faculty", "Faculty", Icons.Filled.People, Icons.Outlined.People)
    object Emergency : Screen("emergency", "Emergency", Icons.Filled.Emergency, Icons.Outlined.Emergency)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Dashboard, Map, Assistant, Notices, Timetable)
    }
}
