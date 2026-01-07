package com.techmaina.visionintel.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class LanguageSettingsUiState(
    val title: String = "Language",
    val options: List<String> = listOf("English", "Spanish", "French", "Hindi")
)

@Composable
fun LanguageSettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val uiState = remember { mutableStateOf(LanguageSettingsUiState()) }
    val selected = remember { mutableStateOf(uiState.value.options.first()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = uiState.value.title,
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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.section)
        ) {
            uiState.value.options.forEach { option ->
                LanguageOptionRow(
                    label = option,
                    selected = selected.value == option,
                    onSelect = { selected.value = option }
                )
            }
        }
    }
}

@Composable
private fun LanguageOptionRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        RadioButton(selected = selected, onClick = onSelect)
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSettingsScreenPreview() {
    VisionIntelTheme {
        LanguageSettingsScreen()
    }
}
