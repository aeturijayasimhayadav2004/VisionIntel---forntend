package com.techmaina.visionintel.ui.screens.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.techmaina.visionintel.core.model.AnalysisResult
import com.techmaina.visionintel.core.model.AnalysisSourceType
import com.techmaina.visionintel.data.analysis.AnalysisRepository
import com.techmaina.visionintel.core.storage.AppPrefs
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.VISecondaryButton
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProcessingVideoAnalysisScreen(
    analysisRepository: AnalysisRepository,
    sourceType: AnalysisSourceType,
    uri: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onFinished: (AnalysisResult) -> Unit = {},
    onNetworkStatus: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val statusText = remember { mutableStateOf("Starting...") }
    val progressPct = remember { mutableStateOf<Int?>(null) }
    val error = remember { mutableStateOf<String?>(null) }
    val jobId = remember { mutableStateOf<String?>(null) }
    val queuedSinceMs = remember { mutableStateOf<Long?>(null) }
    val retrying = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext

    suspend fun pollJob(currentJobId: String) {
        var consecutiveErrors = 0
        while (true) {
            val stResult = analysisRepository.getJobStatus(currentJobId)
            if (stResult.isFailure) {
                consecutiveErrors += 1
                val msg = stResult.exceptionOrNull()?.message ?: "Failed to poll job"
                if (consecutiveErrors < 5) {
                    error.value = null
                    statusText.value = "Waiting for server..."
                    delay(2000)
                    continue
                }
                error.value = msg
                statusText.value = "Failed"
                return
            }
            val st = stResult.getOrThrow()
            consecutiveErrors = 0
            error.value = null

            statusText.value = st.status
            progressPct.value = st.progressPct

            when (st.status) {
                "COMPLETED" -> {
                    val result = analysisRepository.getCompletedJobResult(currentJobId).getOrElse { err ->
                        error.value = err.message ?: "Failed to load results"
                        statusText.value = "Failed"
                        return
                    }
                    onFinished(result)
                    return
                }

                "FAILED" -> {
                    error.value = st.errorMessage ?: "Job failed"
                    statusText.value = "FAILED"
                    return
                }
            }

            if (st.status == "QUEUED") {
                val since = queuedSinceMs.value
                if (since != null && System.currentTimeMillis() - since > 20_000L) {
                    statusText.value = "QUEUED (waiting for worker...)"
                }
            } else {
                queuedSinceMs.value = null
            }

            delay(1500)
        }
    }

    LaunchedEffect(sourceType, uri) {
        error.value = null
        statusText.value = "Preparing..."
        progressPct.value = null
        queuedSinceMs.value = null

        val lastJobId = AppPrefs.getLastAnalysisJobId(appContext)
        val lastUri = AppPrefs.getLastAnalysisUri(appContext)
        val lastSource = AppPrefs.getLastAnalysisSource(appContext)
        val shouldResume = !lastJobId.isNullOrBlank() &&
            (uri.isBlank() || lastUri == uri) &&
            (lastSource.isNullOrBlank() || lastSource == sourceType.name)

        if (shouldResume) {
            statusText.value = "Resuming..."
            jobId.value = lastJobId
            val st = analysisRepository.getJobStatus(lastJobId).getOrElse { err ->
                error.value = err.message ?: "Failed to resume job"
                statusText.value = "Failed"
                return@LaunchedEffect
            }

            statusText.value = st.status
            progressPct.value = st.progressPct

            when (st.status) {
                "COMPLETED" -> {
                    val result = analysisRepository.getCompletedJobResult(lastJobId).getOrElse { err ->
                        error.value = err.message ?: "Failed to load results"
                        statusText.value = "Failed"
                        return@LaunchedEffect
                    }
                    onFinished(result)
                    return@LaunchedEffect
                }
                "FAILED" -> {
                    error.value = st.errorMessage ?: "Job failed"
                    statusText.value = "FAILED"
                    return@LaunchedEffect
                }
                else -> {
                    if (st.status == "QUEUED") {
                        queuedSinceMs.value = System.currentTimeMillis()
                    }
                    pollJob(lastJobId)
                    return@LaunchedEffect
                }
            }
        }

        statusText.value = "Uploading..."
        progressPct.value = 0

        val start = analysisRepository.startVideoAnalysisJob(
            uri = uri,
            sourceType = sourceType,
            onUploadProgressPct = { pct ->
                statusText.value = "Uploading..."
                progressPct.value = pct.coerceIn(0, 100)
            }
        )
            .getOrElse { err ->
                error.value = err.message ?: "Failed to start analysis"
                statusText.value = "Failed"
                return@LaunchedEffect
            }
        jobId.value = start

        statusText.value = "Queued"
        progressPct.value = 0
        queuedSinceMs.value = System.currentTimeMillis()

        pollJob(start)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = "Processing Video",
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
        ProcessingVideoAnalysisContent(
            innerPadding = innerPadding,
            modifier = modifier,
            status = statusText.value,
            progressPct = progressPct.value,
            jobId = jobId.value,
            error = error.value,
            showRetry = statusText.value.equals("FAILED", ignoreCase = true),
            retryEnabled = !retrying.value,
            onRetry = {
                val existingJobId = jobId.value ?: return@ProcessingVideoAnalysisContent
                if (retrying.value) return@ProcessingVideoAnalysisContent
                scope.launch {
                    retrying.value = true
                    try {
                        error.value = null
                        statusText.value = "Retrying..."
                        progressPct.value = 0
                        queuedSinceMs.value = System.currentTimeMillis()

                        val retry = analysisRepository.retryVideoJob(existingJobId).getOrElse { err ->
                            error.value = err.message ?: "Failed to retry"
                            statusText.value = "Failed"
                            return@launch
                        }

                        val newJobId = retry.jobId.ifBlank { existingJobId }
                        jobId.value = newJobId
                        statusText.value = retry.status.ifBlank { "PROCESSING" }
                        progressPct.value = retry.progressPct

                        pollJob(newJobId)
                    } finally {
                        retrying.value = false
                    }
                }
            },
            onNetworkStatus = onNetworkStatus
        )
    }
}

@Composable
private fun ProcessingVideoAnalysisContent(
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    status: String,
    progressPct: Int?,
    jobId: String?,
    error: String?,
    showRetry: Boolean,
    retryEnabled: Boolean,
    onRetry: () -> Unit,
    onNetworkStatus: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
    ) {
        Text(
            text = "Analyzing video...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Status: $status",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (!jobId.isNullOrBlank()) {
            Text(
                text = "Job: $jobId",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (progressPct != null) {
            LinearProgressIndicator(
                progress = { (progressPct.coerceIn(0, 100) / 100f) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Progress: $progressPct%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
            if (showRetry && !jobId.isNullOrBlank()) {
                VISecondaryButton(
                    text = "Retry Analysis",
                    onClick = onRetry,
                    enabled = retryEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            VIPrimaryButton(
                text = "Open Network Status",
                onClick = onNetworkStatus,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Tip: For long videos, keep the phone screen on and ensure ML service is running on the PC.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
