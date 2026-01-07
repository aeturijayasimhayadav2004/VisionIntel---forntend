package com.techmaina.visionintel.ui.screens.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.techmaina.visionintel.data.analysis.VideoJobAlert
import com.techmaina.visionintel.data.analysis.VideoJobReport
import com.techmaina.visionintel.data.analysis.VideoJobSnapshot
import com.techmaina.visionintel.data.analysis.VideoJobSummary
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.ThreatSummaryCard
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VICard
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.VISecondaryButton
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun AnalysisResultScreen(
    modifier: Modifier = Modifier,
    report: VideoJobReport? = null,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onBackToHome: () -> Unit = {},
    onViewReport: ((String) -> Unit)? = null,
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
                title = "Analysis Result",
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
        if (report != null) {
            AnalysisResultWithReport(
                innerPadding = innerPadding,
                report = report,
                modifier = modifier,
                onBackToHome = onBackToHome,
                onViewReport = onViewReport
            )
        } else {
            AnalysisResultPlaceholder(
                innerPadding = innerPadding,
                modifier = modifier,
                onBackToHome = onBackToHome
            )
        }
    }
}

@Composable
private fun AnalysisResultWithReport(
    innerPadding: PaddingValues,
    report: VideoJobReport,
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit,
    onViewReport: ((String) -> Unit)?
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        // Threat Summary Card
        ThreatSummaryCard(
            summary = report.summary,
            snapshots = report.snapshots
        )
        
        // Alerts section
        if (report.alerts.isNotEmpty()) {
            Text(
                text = "Alerts (${report.alerts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            report.alerts.take(5).forEach { alert ->
                AlertItem(alert = alert)
            }
        }
        
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        
        // Action buttons
        if (report.reportId != null && onViewReport != null) {
            VIPrimaryButton(
                text = "View Full Report",
                onClick = { onViewReport(report.reportId) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        VISecondaryButton(
            text = "Back to Home",
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
    }
}

@Composable
private fun AlertItem(alert: VideoJobAlert) {
    VICard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = when (alert.severity.uppercase()) {
            "HIGH" -> MaterialTheme.colorScheme.errorContainer
            "MEDIUM" -> MaterialTheme.colorScheme.tertiaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Text(
            text = alert.message,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        if (alert.createdAt != null) {
            Text(
                text = alert.createdAt,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AnalysisResultPlaceholder(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No threats found",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "The video analysis did not detect any threats.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Your environment is secure.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        VIPrimaryButton(
            text = "Back to Home",
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnalysisResultScreenPreview() {
    VisionIntelTheme {
        AnalysisResultScreen(
            report = VideoJobReport(
                jobId = "test-123",
                reportId = "1",
                historyId = "1",
                summary = VideoJobSummary(
                    threatLevel = "HIGH",
                    maxViolenceProbability = 0.92,
                    threshold = 0.5,
                    threatScore = 0.92,
                    eventLabels = listOf("violence", "weapon"),
                    notes = null,
                    annotatedVideoUrl = null,
                    processingMs = 15000,
                    durationS = 30.0,
                    boxesCount = 5,
                    motionMax = null,
                    motionMean = null,
                    fireMaxRatio = null,
                    fireScore = null,
                    smokeMaxRatio = null,
                    smokeScore = null
                ),
                alerts = listOf(
                    VideoJobAlert("1", "HIGH", "Violence detected in video", "2026-01-06T12:00:00Z")
                ),
                snapshots = emptyList(),
                detectionsCount = 5
            )
        )
    }
}
