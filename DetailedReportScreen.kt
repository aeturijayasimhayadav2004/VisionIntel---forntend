package com.techmaina.visionintel.ui.screens.alerts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.techmaina.visionintel.ui.components.VICard
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.VISectionHeader
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun DetailedReportScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onExport: () -> Unit = {},
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
                title = "Detailed Report",
                showBack = true,
                onBack = onBack,
                showSettings = true,
                onSettings = onSettings
            )
        },
        bottomBar = {
            AppBottomNav(
                selectedTab = HomeTab.REPORTS,
                onHome = onNavHome,
                onAlerts = onNavAlerts,
                onReports = onNavReports,
                onHistory = onNavHistory,
                onProfile = onNavProfile
            )
        }
    ) { innerPadding ->
        DetailedReportContent(
            innerPadding = innerPadding,
            modifier = modifier,
            onExport = onExport
        )
    }
}

@Composable
private fun DetailedReportContent(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onExport: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
    ) {
        TimelineItem(title = "Suspicious Activity Detected", time = "10:15 AM")
        TimelineItem(title = "Object Identification", time = "10:16 AM")
        TimelineItem(title = "Threat Assessment", time = "10:17 AM")

        VISectionHeader(
            text = "Frame Snapshots",
            modifier = Modifier.padding(top = MaterialTheme.spacing.sm)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
            SnapshotCard(modifier = Modifier.weight(1f))
            SnapshotCard(modifier = Modifier.weight(1f))
            SnapshotCard(modifier = Modifier.weight(1f))
        }

        VISectionHeader(
            text = "AI Explanation",
            modifier = Modifier.padding(top = MaterialTheme.spacing.sm)
        )
        VICard(containerColor = MaterialTheme.colorScheme.surface) {
            Text(
                text = "Our AI algorithms identified unusual movement patterns within the monitored area. " +
                    "Further analysis revealed the presence of a potential intruder. The system " +
                    "automatically alerted security personnel and recorded the event for review.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))

        VIPrimaryButton(
            text = "Export Report",
            onClick = onExport,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TimelineItem(title: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_camera_placeholder),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SnapshotCard(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_camera_placeholder),
        contentDescription = "Snapshot",
        modifier = modifier
            .height(84.dp)
            .clip(MaterialTheme.shapes.medium),
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true)
@Composable
fun DetailedReportScreenPreview() {
    VisionIntelTheme {
        DetailedReportScreen()
    }
}
