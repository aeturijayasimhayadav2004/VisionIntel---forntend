package com.techmaina.visionintel.ui.screens.alerts

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VICard
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.core.export.ReportExporter
import com.techmaina.visionintel.di.LocalAppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class ExportReportUiState(
    val title: String = "Export Report",
    val pdfLabel: String = "PDF",
    val csvLabel: String = "CSV",
    val shareLabel: String = "Share"
)

private data class ExportPayload(
    val uri: Uri,
    val mimeType: String,
    val title: String
)

@Composable
fun ExportReportScreen(
    modifier: Modifier = Modifier,
    reportId: String? = null,
    jobId: String? = null,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val uiState = ExportReportUiState()
    val context = LocalContext.current
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val lastExport = remember { mutableStateOf<ExportPayload?>(null) }
    
    val csvLines = remember {
        listOf(
            "event,time,severity",
            "Threat Detected,Analysis Time,HIGH",
            "Object Identified,Analysis Time,MEDIUM"
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = uiState.title,
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .screenPadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            // Summary Card
            VICard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    Text(
                        text = "📊 Threat Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    if (!jobId.isNullOrBlank()) {
                        Text(
                            text = "Analysis ID: $jobId",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    
                    Text(
                        text = "Export your analysis report in PDF or CSV format.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            
            // Export Options
            Text(
                text = "📥 Export Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            ExportRow(
                icon = Icons.Outlined.PictureAsPdf, 
                label = uiState.pdfLabel,
                onClick = {
                    scope.launch {
                        val targetReportId = reportId
                            ?.takeIf { it.isNotBlank() }
                            ?: runCatching { container.reportsRepository.getReportHistory().firstOrNull()?.id }
                                .getOrNull()
                                ?.takeIf { it.isNotBlank() }
                        if (targetReportId == null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "No reports available to export.", Toast.LENGTH_SHORT).show()
                            }
                            return@launch
                        }

                        val uri = runCatching {
                            val bytes = container.reportsRepository.downloadReportPdf(targetReportId)
                            ReportExporter.saveBytesToDownloads(
                                context = context,
                                bytes = bytes,
                                mimeType = "application/pdf",
                                fileName = "visionintel-report-$targetReportId.pdf"
                            )
                        }.getOrNull()
                        withContext(Dispatchers.Main) {
                            if (uri == null) {
                                Toast.makeText(context, "PDF export failed.", Toast.LENGTH_SHORT).show()
                            } else {
                                lastExport.value = ExportPayload(uri, "application/pdf", "Share PDF")
                                Toast.makeText(context, "Saved to Downloads.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
            
            ExportRow(
                icon = Icons.Outlined.TableChart, 
                label = uiState.csvLabel,
                onClick = {
                    scope.launch {
                        val uri = ReportExporter.exportCsvToDownloads(context, csvLines)
                        withContext(Dispatchers.Main) {
                            if (uri == null) {
                                Toast.makeText(context, "CSV export failed.", Toast.LENGTH_SHORT).show()
                            } else {
                                lastExport.value = ExportPayload(uri, "text/csv", "Share CSV")
                                Toast.makeText(context, "Saved to Downloads.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
            
            ExportRow(
                icon = Icons.Outlined.Share, 
                label = uiState.shareLabel,
                onClick = {
                    val payload = lastExport.value
                    if (payload != null) {
                        ReportExporter.shareFile(context, payload.uri, payload.mimeType, payload.title)
                    } else {
                        Toast.makeText(context, "Export a PDF or CSV first.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
        }
    }
}

@Composable
private fun ExportRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = MaterialTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExportReportScreenPreview() {
    VisionIntelTheme {
        ExportReportScreen()
    }
}
