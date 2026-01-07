package com.techmaina.visionintel.ui.screens.analysis

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.techmaina.visionintel.data.analysis.AnalysisRepository
import com.techmaina.visionintel.data.analysis.VideoJobReport
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VICard
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun JobReportScreen(
    analysisRepository: AnalysisRepository,
    jobId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onExport: (String) -> Unit = {},
    onExportReport: (String) -> Unit = {},  // Navigate to export report screen
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val report = remember { mutableStateOf<VideoJobReport?>(null) }
    val error = remember { mutableStateOf<String?>(null) }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(jobId) {
        if (jobId.isBlank()) {
            error.value = "Missing job id"
            return@LaunchedEffect
        }
        isLoading.value = true
        error.value = null
        report.value = null
        analysisRepository.getJobReport(jobId)
            .onSuccess { report.value = it }
            .onFailure { err -> error.value = err.message ?: "Failed to load report" }
        isLoading.value = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = "Report",
                showBack = true,
                onBack = onBack
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
        JobReportContent(
            innerPadding = innerPadding,
            modifier = modifier,
            jobId = jobId,
            report = report.value,
            isLoading = isLoading.value,
            error = error.value,
            onExport = onExport,
            onExportReport = onExportReport
        )
    }
}

@Composable
private fun JobReportContent(
    innerPadding: PaddingValues,
    modifier: Modifier,
    jobId: String,
    report: VideoJobReport?,
    isLoading: Boolean,
    error: String?,
    onExport: (String) -> Unit,
    onExportReport: (String) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        // Removed Job ID text as requested

        if (isLoading) {
            Text(
                text = "Loading report...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            return
        }

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.weight(1f))
            return
        }

        val r = report ?: run {
            Text(
                text = "No report data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            return
        }

        val threatLevel = when {
            r.alerts.isNotEmpty() && (r.summary.threatLevel.isNullOrBlank() || r.summary.threatLevel.equals("LOW", true)) -> "THREAT"
            else -> r.summary.threatLevel ?: if (r.alerts.isNotEmpty()) "THREAT" else "UNKNOWN"
        }
        val labels = r.summary.eventLabels.joinToString(", ").ifBlank { "None" }
        val maxProb = r.summary.maxViolenceProbability
        val threshold = r.summary.threshold
        val score = r.summary.threatScore
        val fireScore = r.summary.fireScore
        val fireMax = r.summary.fireMaxRatio
        val smokeScore = r.summary.smokeScore
        val smokeMax = r.summary.smokeMaxRatio

        VICard(containerColor = MaterialTheme.colorScheme.surface) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Threat level: $threatLevel",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Labels: $labels",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (maxProb != null && threshold != null) {
                    Text(
                        text = "Violence p=${"%.4f".format(maxProb)} (threshold=${"%.2f".format(threshold)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (score != null) {
                    Text(
                        text = "Threat score: ${"%.4f".format(score)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (fireScore != null || fireMax != null) {
                    Text(
                        text = "Fire score: ${fireScore?.let { "%.3f".format(it) } ?: "-"} (max_ratio=${fireMax?.let { "%.3f".format(it) } ?: "-"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (smokeScore != null || smokeMax != null) {
                    Text(
                        text = "Smoke score: ${smokeScore?.let { "%.3f".format(it) } ?: "-"} (max_ratio=${smokeMax?.let { "%.3f".format(it) } ?: "-"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (r.summary.durationS != null) {
                    Text(
                        text = "Duration: ${"%.1f".format(r.summary.durationS)}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!r.summary.notes.isNullOrBlank()) {
                    Text(
                        text = r.summary.notes.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!r.summary.annotatedVideoUrl.isNullOrBlank()) {
            VIPrimaryButton(
                text = "Open annotated video",
                onClick = {
                    val url = r.summary.annotatedVideoUrl ?: return@VIPrimaryButton
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(url), "video/*")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    runCatching { context.startActivity(intent) }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (r.snapshots.isNotEmpty()) {
            Text(
                text = "Snapshots",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                items(r.snapshots.take(12)) { s ->
                    AsyncImage(
                        model = s.url,
                        contentDescription = null,
                        modifier = Modifier.height(84.dp)
                    )
                }
            }
        }

        Text(
            text = "Threats",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (r.alerts.isEmpty()) {
            Text(
                text = "No threats detected.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                r.alerts.take(10).forEach { a ->
                    VICard(containerColor = MaterialTheme.colorScheme.surface) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = a.severity.ifBlank { "Alert" },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = a.message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (!a.createdAt.isNullOrBlank()) {
                                Text(
                                    text = a.createdAt,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Export buttons - properly aligned
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        
        VIPrimaryButton(
            text = "📄 Export Detailed Report",
            onClick = { onExportReport(jobId) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )
        
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!r.reportId.isNullOrBlank()) {
                VIPrimaryButton(
                    text = "Export PDF",
                    onClick = { onExport(r.reportId) },
                    modifier = Modifier.weight(0.6f).height(44.dp)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
            }
            Text(
                text = "📊 Detections: ${r.detectionsCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.4f)
            )
        }

        // Complete Report Section - Show ALL threats with timestamps
        if (r.alerts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            
            VICard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                    Text(
                        text = "📋 Complete Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "Total Threats Found: ${r.alerts.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    // Show ALL alerts with full details
                    r.alerts.forEachIndexed { index, alert ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "${index + 1}. ${alert.severity.ifBlank { "ALERT" }}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = when(alert.severity.uppercase()) {
                                    "HIGH" -> MaterialTheme.colorScheme.error
                                    "MEDIUM" -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            )
                            Text(
                                text = alert.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (!alert.createdAt.isNullOrBlank()) {
                                Text(
                                    text = "⏰ Time: ${alert.createdAt}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    
                    // Summary
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                    Text(
                        text = "📊 Analysis Summary:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = buildString {
                            append("• Threat Level: $threatLevel\n")
                            append("• Events Detected: $labels\n")
                            append("• Total Snapshots: ${r.snapshots.size}\n")
                            if (r.summary.durationS != null) {
                                append("• Video Duration: ${"%.1f".format(r.summary.durationS)}s\n")
                            }
                            if (score != null) {
                                append("• Overall Threat Score: ${"%.2f".format(score * 100)}%")
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
