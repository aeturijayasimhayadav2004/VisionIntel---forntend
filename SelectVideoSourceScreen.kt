package com.techmaina.visionintel.ui.screens.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun SelectVideoSourceScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onUploadVideo: () -> Unit = {},
    onLiveCamera: () -> Unit = {},
    onDroneVideo: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = "Select Video Source",
                showBack = true,
                onBack = onBack,
                showSettings = true,
                onSettings = onSettings
            )
        },
        bottomBar = {
            AppBottomNav(
                selectedTab = HomeTab.HOME,
                onHome = onNavHome,
                onAlerts = onNavAlerts,
                onReports = onNavReports,
                onHistory = onNavHistory,
                onProfile = onNavProfile
            )
        }
    ) { innerPadding ->
        SelectVideoSourceContent(
            innerPadding = innerPadding,
            modifier = modifier,
            onUploadVideo = onUploadVideo,
            onLiveCamera = onLiveCamera,
            onDroneVideo = onDroneVideo
        )
    }
}

@Composable
private fun SelectVideoSourceContent(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onUploadVideo: () -> Unit,
    onLiveCamera: () -> Unit,
    onDroneVideo: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        VIPrimaryButton(
            text = "Upload Video",
            onClick = onUploadVideo,
            modifier = Modifier.fillMaxWidth()
        )
        VIPrimaryButton(
            text = "Live Camera",
            onClick = onLiveCamera,
            modifier = Modifier.fillMaxWidth()
        )
        VIPrimaryButton(
            text = "Drone Video",
            onClick = onDroneVideo,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

