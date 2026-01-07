package com.techmaina.visionintel.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.model.HistoryItem
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VISectionHeader
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.theme.spacing

data class HistoryEvent(
    val title: String,
    val timestamp: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun HistoryDetailScreen(
    modifier: Modifier = Modifier,
    historyItem: HistoryItem?,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
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
                title = "History Detail",
                showBack = true,
                onBack = onBack,
                showSettings = true,
                onSettings = onSettings
            )
        },
        bottomBar = {
            AppBottomNav(
                selectedTab = HomeTab.HISTORY,
                onHome = onNavHome,
                onAlerts = onNavAlerts,
                onReports = onNavReports,
                onHistory = onNavHistory,
                onProfile = onNavProfile
            )
        }
    ) { innerPadding ->
        HistoryDetailContent(
            innerPadding = innerPadding,
            historyItem = historyItem,
            modifier = modifier
        )
    }
}

@Composable
private fun HistoryDetailContent(
    innerPadding: PaddingValues,
    historyItem: HistoryItem?,
    modifier: Modifier = Modifier
) {
    val events = remember {
        listOf(
            HistoryEvent("Motion Detected", "00:05", Icons.Outlined.Warning),
            HistoryEvent("Object Identified", "00:12", Icons.Outlined.Security),
            HistoryEvent("Suspicious Activity", "00:25", Icons.Outlined.Warning)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.section)
    ) {
        PreviewHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .screenPadding(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Text(
                text = historyItem?.date ?: "Unknown date",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = historyItem?.status ?: "Status unavailable",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            VISectionHeader(text = "Events")

            events.forEachIndexed { index, event ->
                EventRow(
                    event = event,
                    showConnector = index != events.lastIndex
                )
            }
        }
    }
}

@Composable
private fun PreviewHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF101820), Color(0xFF2A3945))
                )
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier.size(56.dp)
        )
    }
}

@Composable
private fun EventRow(
    event: HistoryEvent,
    showConnector: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = event.icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.onSurface
            )
            if (showConnector) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(1.dp))
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = event.timestamp,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryDetailScreenPreview() {
    VisionIntelTheme {
        HistoryDetailScreen(
            historyItem = HistoryItem(
                id = "2024-01-20",
                date = "2024-01-20",
                status = "Threat detected: Suspicious activity",
                threatType = "Suspicious Activity"
            )
        )
    }
}
