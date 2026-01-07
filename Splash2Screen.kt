package com.techmaina.visionintel.ui.screens.splash

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.CameraConnectIllustration
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.theme.spacing
import com.techmaina.visionintel.ui.components.screenPadding

data class Splash2UiState(
    val header: String = "Quick Setup",
    val description: String = "Set up VisionIntel to analyze your videos and detect threats. " +
        "Follow the steps to get started.",
    val buttonLabel: String = "Next"
)

@Composable
fun Splash2Screen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    val uiState = remember { mutableStateOf(Splash2UiState()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Splash2Content(
            innerPadding = innerPadding,
            state = uiState.value,
            modifier = modifier,
            onBack = onBack,
            onNext = onNext
        )
    }
}

@Composable
private fun Splash2Content(
    innerPadding: PaddingValues,
    state: Splash2UiState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.section)
    ) {
        Text(
            text = state.header,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = state.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CameraConnectIllustration(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        VIPrimaryButton(
            text = state.buttonLabel,
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Splash2ScreenPreview() {
    VisionIntelTheme {
        Splash2Screen()
    }
}
