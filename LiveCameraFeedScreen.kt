package com.techmaina.visionintel.ui.screens.analysis

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VideoCameraFront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.techmaina.visionintel.core.model.AnalysisResult
import com.techmaina.visionintel.data.analysis.AnalysisRepository
import com.techmaina.visionintel.data.analysis.MockAnalysisRepository
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VICard
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.theme.spacing
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LiveCameraUiState(
    val title: String = "Live Camera Feed",
    val buttonLabel: String = "Start Analysis"
)

@Composable
fun LiveCameraFeedScreen(
    modifier: Modifier = Modifier,
    analysisRepository: AnalysisRepository = MockAnalysisRepository(),
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onSessionFinished: (AnalysisResult) -> Unit = {},
    onNetworkError: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val uiState = remember { mutableStateOf(LiveCameraUiState()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission.value = granted
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = uiState.value.title,
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
        LiveCameraContent(
            innerPadding = innerPadding,
            state = uiState.value,
            modifier = modifier,
            hasCameraPermission = hasPermission.value,
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            analysisRepository = analysisRepository,
            onSessionFinished = onSessionFinished,
            onNetworkError = onNetworkError,
            launch = { block -> scope.launch { block() } }
        )
    }
}

@Composable
private fun LiveCameraContent(
    innerPadding: PaddingValues,
    state: LiveCameraUiState,
    modifier: Modifier = Modifier,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    analysisRepository: AnalysisRepository,
    onSessionFinished: (AnalysisResult) -> Unit,
    onNetworkError: () -> Unit,
    launch: (suspend () -> Unit) -> Unit
) {
    val errorMessage = remember { mutableStateOf<String?>(null) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    val isRunning = remember { mutableStateOf(false) }
    val sessionId = remember { mutableStateOf<String?>(null) }
    val threatsFound = remember { mutableStateOf(false) }
    val liveStatus = remember { mutableStateOf<String?>(null) }
    val threatStreak = remember { mutableStateOf(0) }
    val normalStreak = remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        if (hasCameraPermission) {
            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large),
                onPreviewReady = { previewView = it },
                onError = { message -> errorMessage.value = message }
            )
        } else {
            VICard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VideoCameraFront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Text(
                    text = "Camera access is required to show the live feed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
                VIPrimaryButton(
                    text = "Grant Camera Access",
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        LaunchedEffect(isRunning.value, hasCameraPermission) {
            if (!isRunning.value || !hasCameraPermission) return@LaunchedEffect

            val start = SystemClock.elapsedRealtime()
            liveStatus.value = "Monitoring..."
            errorMessage.value = null
            threatStreak.value = 0
            normalStreak.value = 0

            while (isActive && isRunning.value) {
                val bitmap = previewView?.bitmap
                if (bitmap != null) {
                    val bytes = withContext(Dispatchers.Default) {
                        bitmap.toJpegBytesOrNull(quality = 85, maxWidth = 640)
                    }
                    if (bytes != null && bytes.isNotEmpty()) {
                        val tsMs = SystemClock.elapsedRealtime() - start
                        val frame = analysisRepository
                            .inferLiveFrame(bytes, sessionId.value, tsMs)
                            .getOrElse { err ->
                                errorMessage.value = err.message ?: "Live inference failed"
                                isRunning.value = false
                                onNetworkError()
                                return@LaunchedEffect
                            }

                        if (sessionId.value.isNullOrBlank()) {
                            sessionId.value = frame.jobId
                        }

                        val threshold = frame.threshold
                        val prob = frame.violentProb
                        val isThreatFrame = frame.threat && (threshold == null || prob == null || prob >= threshold)

                        if (isThreatFrame) {
                            threatsFound.value = true
                            threatStreak.value += 1
                            normalStreak.value = 0
                        } else {
                            normalStreak.value += 1
                            threatStreak.value = 0
                        }

                        // Smoothing to reduce false positives:
                        // - require 3 consecutive threat frames to show banner
                        // - clear after 5 consecutive normal frames
                        liveStatus.value = when {
                            threatStreak.value >= 3 -> "THREAT DETECTED"
                            normalStreak.value >= 5 -> "Monitoring..."
                            else -> liveStatus.value
                        }
                    }
                }
                delay(900)
            }
        }

        if (!liveStatus.value.isNullOrBlank()) {
            Text(
                text = liveStatus.value.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (threatsFound.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (errorMessage.value != null) {
            Text(
                text = errorMessage.value.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        VIPrimaryButton(
            text = if (isRunning.value) "Stop Analysis" else state.buttonLabel,
            onClick = {
                if (!hasCameraPermission) {
                    errorMessage.value = "Camera permission is required to start analysis."
                    return@VIPrimaryButton
                }

                if (!isRunning.value) {
                    sessionId.value = null
                    threatsFound.value = false
                    liveStatus.value = null
                    errorMessage.value = null
                    threatStreak.value = 0
                    normalStreak.value = 0
                    isRunning.value = true
                    return@VIPrimaryButton
                }

                isRunning.value = false
                val jobId = sessionId.value
                if (jobId.isNullOrBlank()) {
                    onSessionFinished(
                        AnalysisResult(
                            hasThreat = threatsFound.value,
                            alerts = emptyList(),
                            reportId = null,
                            sessionId = null
                        )
                    )
                    return@VIPrimaryButton
                }

                launch {
                    analysisRepository.finishLiveSession(jobId)
                        .onSuccess { onSessionFinished(it) }
                        .onFailure { err ->
                            errorMessage.value = err.message ?: "Failed to finalize live session"
                            onNetworkError()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onPreviewReady: (PreviewView) -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val onPreviewReadyState by rememberUpdatedState(onPreviewReady)
    DisposableEffect(previewView) {
        onPreviewReadyState(previewView)
        onDispose {}
    }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(lifecycleOwner) {
        val executor = ContextCompat.getMainExecutor(context)
        val listener = Runnable {
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = CameraPreview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            } catch (_: Exception) {
                onError("Unable to start camera preview.")
            }
        }
        cameraProviderFuture.addListener(listener, executor)
        onDispose {
            if (cameraProviderFuture.isDone) {
                runCatching { cameraProviderFuture.get().unbindAll() }
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

private fun Bitmap.toJpegBytesOrNull(quality: Int, maxWidth: Int): ByteArray? {
    val scaled = if (maxWidth > 0 && width > maxWidth) {
        val targetH = (height * (maxWidth.toFloat() / width.toFloat())).toInt().coerceAtLeast(1)
        Bitmap.createScaledBitmap(this, maxWidth, targetH, true)
    } else {
        null
    }

    val bitmap = scaled ?: this
    val bytes = runCatching {
        ByteArrayOutputStream().use { out ->
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), out)) return null
            out.toByteArray()
        }
    }.getOrNull()

    if (scaled != null && !scaled.isRecycled) {
        scaled.recycle()
    }
    return bytes
}

@Preview(showBackground = true)
@Composable
fun LiveCameraFeedScreenPreview() {
    VisionIntelTheme {
        LiveCameraFeedScreen()
    }
}
