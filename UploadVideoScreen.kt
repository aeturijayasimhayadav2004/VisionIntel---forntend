package com.techmaina.visionintel.ui.screens.analysis

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.VISecondaryButton
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class UploadVideoUiState(
    val title: String = "Upload Video",
    val description: String = "Select a video file to upload for analysis.",
    val supported: String = "Supported formats: mp4, avi, mkv",
    val dropTitle: String = "Select a video file",
    val dropSubtitle: String = "Tap here to browse your files and select a video for upload.",
    val browseLabel: String = "Browse Files",
    val startLabel: String = "Start Analysis"
)

@Composable
fun UploadVideoScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onStartAnalysis: (String) -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState = remember { mutableStateOf(UploadVideoUiState()) }
    val selectedUri = remember { mutableStateOf<Uri?>(null) }
    val message = remember { mutableStateOf<String?>(null) }
    val isAnalyzing = remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedUri.value = uri
            isAnalyzing.value = false
            message.value = null
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
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
        UploadVideoContent(
            innerPadding = innerPadding,
            state = uiState.value,
            selectedUri = selectedUri.value,
            message = message.value,
            isAnalyzing = isAnalyzing.value,
            modifier = modifier,
            onBrowse = { pickerLauncher.launch(arrayOf("video/*")) },
            onStart = {
                val uri = selectedUri.value
                if (uri == null) {
                    message.value = "Please select a video first."
                } else {
                    isAnalyzing.value = true
                    message.value = "Uploading and analyzing... (this may take a few minutes)"
                    onStartAnalysis(uri.toString())
                }
            }
        )
    }
}

@Composable
private fun UploadVideoContent(
    innerPadding: PaddingValues,
    state: UploadVideoUiState,
    selectedUri: Uri?,
    message: String?,
    isAnalyzing: Boolean,
    modifier: Modifier = Modifier,
    onBrowse: () -> Unit,
    onStart: () -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        Text(
            text = state.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = state.supported,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .drawBehind {
                    val stroke = Stroke(
                        width = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                    )
                    drawRoundRect(
                        color = borderColor,
                        style = stroke,
                        cornerRadius = CornerRadius(28f, 28f)
                    )
                }
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large)
                .clickable { onBrowse() }
                .padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.dropTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = state.dropSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = MaterialTheme.spacing.sm)
            )
            VISecondaryButton(
                text = state.browseLabel,
                onClick = onBrowse,
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.md)
                    .fillMaxWidth()
            )
        }

        selectedUri?.let {
            Text(
                text = "Selected: ${it.lastPathSegment ?: it}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        VIPrimaryButton(
            text = if (isAnalyzing) "Analyzing..." else state.startLabel,
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UploadVideoScreenPreview() {
    VisionIntelTheme {
        UploadVideoScreen()
    }
}
