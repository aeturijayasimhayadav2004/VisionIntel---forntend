package com.techmaina.visionintel.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import com.techmaina.visionintel.ui.screens.home.HomeTab

@Composable
fun AppBottomNav(
    selectedTab: HomeTab,
    onHome: () -> Unit,
    onAlerts: () -> Unit,
    onReports: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit
) {
    VIBottomNav(
        selectedTab = selectedTab,
        onHome = onHome,
        onAlerts = onAlerts,
        onReports = onReports,
        onHistory = onHistory,
        onProfile = onProfile,
        homeIcon = Icons.Outlined.Home,
        alertsIcon = Icons.Outlined.Notifications,
        reportsIcon = Icons.Outlined.AutoGraph,
        historyIcon = Icons.Outlined.History,
        profileIcon = Icons.Outlined.Person
    )
}
