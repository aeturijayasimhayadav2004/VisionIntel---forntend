package com.techmaina.visionintel.ui.screens.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techmaina.visionintel.data.alerts.AlertsViewModel
import com.techmaina.visionintel.di.LocalAppContainer
import com.techmaina.visionintel.model.AlertItem
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.ThumbnailPlaceholder
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIListRow
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier,
    showBack: Boolean = true,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onDailySummary: () -> Unit = {},
    onAlertSelected: (AlertItem) -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val container = LocalAppContainer.current
    val alertsViewModel: AlertsViewModel = viewModel(factory = AlertsViewModel.Factory(container.alertsRepository))
    val alertItems = alertsViewModel.alerts.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = "Alerts",
                showBack = showBack,
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
        AlertsContent(
            innerPadding = innerPadding,
            alertItems = alertItems.value,
            modifier = modifier,
            onDailySummary = onDailySummary,
            onAlertSelected = onAlertSelected
        )
    }
}

@Composable
private fun AlertsContent(
    innerPadding: PaddingValues,
    alertItems: List<AlertItem>,
    modifier: Modifier = Modifier,
    onDailySummary: () -> Unit,
    onAlertSelected: (AlertItem) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(
            horizontal = MaterialTheme.spacing.screenHorizontal,
            vertical = MaterialTheme.spacing.screenVertical
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
    ) {
        item {
            DailySummaryCard(onClick = onDailySummary)
        }
        if (alertItems.isEmpty()) {
            item {
                Text(
                    text = "No alerts yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(alertItems) { item ->
                AlertRow(item = item, onClick = { onAlertSelected(item) })
            }
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DailySummaryCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF0F141A), Color(0xFF202831))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(MaterialTheme.spacing.xl)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
    ) {
        ThumbnailPlaceholder(
            modifier = Modifier.size(92.dp),
            icon = Icons.Outlined.Security,
            background = Brush.linearGradient(
                colors = listOf(Color(0xFF2D3945), Color(0xFF1B232C))
            )
        )
        Text(
            text = "Daily\nSummary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun AlertRow(
    item: AlertItem,
    onClick: () -> Unit
) {
    VIListRow(
        title = item.title,
        subtitle = "${item.time} - ${item.severity}",
        leadingIcon = Icons.Outlined.Security,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun AlertsScreenPreview() {
    VisionIntelTheme {
        AlertsScreen()
    }
}
