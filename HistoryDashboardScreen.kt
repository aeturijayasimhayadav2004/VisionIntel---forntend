package com.techmaina.visionintel.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.model.HistoryItem
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIListRow
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun HistoryDashboardScreen(
    modifier: Modifier = Modifier,
    historyItems: List<HistoryItem>,
    onSnapshotGallery: () -> Unit = {},
    onSearch: () -> Unit = {},
    onSettings: () -> Unit = {},
    onHistorySelected: (String) -> Unit = {},
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
                title = "History",
                leadingIcon = Icons.Outlined.Search,
                onLeadingIcon = onSearch,
                leadingIconDescription = "Search",
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
        HistoryDashboardContent(
            innerPadding = innerPadding,
            historyItems = historyItems,
            modifier = modifier,
            onSnapshotGallery = onSnapshotGallery,
            onHistorySelected = onHistorySelected
        )
    }
}

@Composable
private fun HistoryDashboardContent(
    innerPadding: PaddingValues,
    historyItems: List<HistoryItem>,
    modifier: Modifier = Modifier,
    onSnapshotGallery: () -> Unit,
    onHistorySelected: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(
            horizontal = MaterialTheme.spacing.screenHorizontal,
            vertical = MaterialTheme.spacing.screenVertical
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                VIPrimaryButton(
                    text = "Snapshot Gallery",
                    onClick = onSnapshotGallery,
                    modifier = Modifier
                        .height(48.dp)
                        .padding(horizontal = MaterialTheme.spacing.md)
                )
            }
        }

        if (historyItems.isEmpty()) {
            item {
                Text(
                    text = "No history yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(historyItems) { item ->
                HistoryRow(
                    date = item.date,
                    status = item.status,
                    onClick = { onHistorySelected(item.id) }
                )
            }
        }
    }
}

@Composable
private fun HistoryRow(
    date: String,
    status: String,
    onClick: () -> Unit
) {
    VIListRow(
        title = date,
        subtitle = status,
        leadingIcon = Icons.Outlined.PlayArrow,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun HistoryDashboardScreenPreview() {
    VisionIntelTheme {
        HistoryDashboardScreen(
            historyItems = listOf(
                HistoryItem(
                    id = "2024-01-20",
                    date = "2024-01-20",
                    status = "No threats detected",
                    threatType = "None"
                ),
                HistoryItem(
                    id = "2024-01-19",
                    date = "2024-01-19",
                    status = "Threat detected: Suspicious activity",
                    threatType = "Suspicious Activity"
                )
            )
        )
    }
}
