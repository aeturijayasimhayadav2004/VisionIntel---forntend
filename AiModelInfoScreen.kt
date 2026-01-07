package com.techmaina.visionintel.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIListRow
import com.techmaina.visionintel.ui.components.VISectionHeader
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class AiModelInfoUiState(
    val title: String = "AI Model Info",
    val detailsTitle: String = "Model Details",
    val modelNameLabel: String = "Model Name",
    val modelNameValue: String = "Version 2.1",
    val confidenceLabel: String = "Confidence Range",
    val confidenceValue: String = "0.75 - 0.95",
    val updateLabel: String = "Update History",
    val updateValue: String = "Last Updated: 2024-01-20"
)

@Composable
fun AiModelInfoScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {}
) {
    val uiState = remember { AiModelInfoUiState() }

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
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .screenPadding(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
        ) {
            VISectionHeader(text = uiState.detailsTitle)

            ModelInfoRow(
                icon = Icons.Outlined.Psychology,
                label = uiState.modelNameLabel,
                value = uiState.modelNameValue
            )
            ModelInfoRow(
                icon = Icons.AutoMirrored.Outlined.ShowChart,
                label = uiState.confidenceLabel,
                value = uiState.confidenceValue
            )
            ModelInfoRow(
                icon = Icons.Outlined.Schedule,
                label = uiState.updateLabel,
                value = uiState.updateValue
            )
        }
    }
}

@Composable
private fun ModelInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    VIListRow(
        title = label,
        subtitle = value,
        leadingIcon = icon,
        showChevron = false,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun AiModelInfoScreenPreview() {
    VisionIntelTheme {
        AiModelInfoScreen()
    }
}
