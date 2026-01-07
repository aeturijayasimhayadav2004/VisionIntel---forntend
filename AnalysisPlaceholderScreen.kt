package com.techmaina.visionintel.ui.screens.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.techmaina.visionintel.core.model.AnalysisRequest
import com.techmaina.visionintel.core.model.AnalysisSourceType
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun AnalysisPlaceholderScreen(
    modifier: Modifier = Modifier,
    request: AnalysisRequest = AnalysisRequest(AnalysisSourceType.UNKNOWN),
    onBack: () -> Unit = {},
    onReturnHome: (() -> Unit)? = null
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = "Threat Analysis",
                showBack = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .screenPadding(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Text(
                text = "Threat detection module is not yet integrated. This screen is a placeholder.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Source: ${request.sourceType.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Uri: ${request.uri ?: "-"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            if (onReturnHome != null) {
                VIPrimaryButton(
                    text = "Return Home",
                    onClick = onReturnHome,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalysisPlaceholderScreenPreview() {
    VisionIntelTheme {
        AnalysisPlaceholderScreen(
            request = AnalysisRequest(
                sourceType = AnalysisSourceType.UPLOAD_VIDEO,
                uri = "content://media/external/video/123"
            )
        )
    }
}
