package com.techmaina.visionintel.ui.screens.alerts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.R
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun ThreatDetectedScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onViewDetails: () -> Unit = {},
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
                title = "Threat Detected",
                showBack = true,
                onBack = onBack,
                showSettings = true,
                onSettings = onSettings
            )
        },
        bottomBar = {
            AppBottomNav(
                selectedTab = HomeTab.ALERTS,
                onHome = onNavHome,
                onAlerts = onNavAlerts,
                onReports = onNavReports,
                onHistory = onNavHistory,
                onProfile = onNavProfile
            )
        }
    ) { innerPadding ->
        ThreatDetectedContent(
            innerPadding = innerPadding,
            modifier = modifier,
            onViewDetails = onViewDetails
        )
    }
}

@Composable
private fun ThreatDetectedContent(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onViewDetails: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.section),
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painter = painterResource(R.drawable.ic_alert_banner),
            contentDescription = "Alert banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Detected at 10:30 AM, 20th July 2024",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Confidence Level: 95%",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        VIPrimaryButton(
            text = "View Details",
            onClick = onViewDetails,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThreatDetectedScreenPreview() {
    VisionIntelTheme {
        ThreatDetectedScreen()
    }
}
