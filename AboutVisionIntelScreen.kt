package com.techmaina.visionintel.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.BuildConfig
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VISectionHeader
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class AboutSystemInfoUiState(
    val title: String = "About & System Info",
    val appVersionLabel: String = "App Version",
    val appVersionValue: String = "1.2.3",
    val modelVersionLabel: String = "ML Model Version",
    val modelVersionValue: String = "2.0",
    val legalTitle: String = "Legal",
    val termsLabel: String = "Terms of Service",
    val privacyLabel: String = "Privacy Policy"
)

@Composable
fun AboutVisionIntelScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onOpenMlSmokeTest: () -> Unit = {}
) {
    val uiState = remember {
        AboutSystemInfoUiState(
            appVersionValue = BuildConfig.VERSION_NAME,
        )
    }
    val taps = remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = uiState.title,
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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl)
        ) {
            InfoBlock(
                title = uiState.appVersionLabel,
                value = uiState.appVersionValue,
                modifier = Modifier.clickable {
                    if (!BuildConfig.DEBUG) return@clickable
                    taps.intValue++
                    if (taps.intValue >= 5) {
                        taps.intValue = 0
                        onOpenMlSmokeTest()
                    }
                }
            )
            InfoBlock(
                title = uiState.modelVersionLabel,
                value = uiState.modelVersionValue
            )

            VISectionHeader(text = uiState.legalTitle)
            Text(
                text = uiState.termsLabel,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = uiState.privacyLabel,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun InfoBlock(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutSystemInfoScreenPreview() {
    VisionIntelTheme {
        AboutVisionIntelScreen()
    }
}
