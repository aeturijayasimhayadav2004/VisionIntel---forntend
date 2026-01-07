package com.techmaina.visionintel.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.R
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.components.VideoAnalysisIllustration
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.theme.spacing

data class Splash1UiState(
    val appName: String = "VisionIntel",
    val headline: String = "AI-Powered Video Threat\nIntelligence",
    val buttonLabel: String = "Get Started",
    val terms: String = "By continuing, you agree to our Terms & Privacy Policy"
)

@Composable
fun Splash1Screen(
    modifier: Modifier = Modifier,
    onGetStarted: () -> Unit = {}
) {
    val uiState = remember { mutableStateOf(Splash1UiState()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Splash1Content(
            innerPadding = innerPadding,
            state = uiState.value,
            modifier = modifier,
            onGetStarted = onGetStarted
        )
    }
}

@Composable
private fun Splash1Content(
    innerPadding: PaddingValues,
    state: Splash1UiState,
    modifier: Modifier = Modifier,
    onGetStarted: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.visionintel_logo),
            contentDescription = "VisionIntel logo",
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = state.appName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = state.headline,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        VideoAnalysisIllustration(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        VIPrimaryButton(
            text = state.buttonLabel,
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = state.terms,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = MaterialTheme.spacing.xs)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Splash1ScreenPreview() {
    VisionIntelTheme {
        Splash1Screen()
    }
}
