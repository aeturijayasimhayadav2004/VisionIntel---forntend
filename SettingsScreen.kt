package com.techmaina.visionintel.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.data.settings.ThemeMode
import com.techmaina.visionintel.data.settings.UserPreferences
import com.techmaina.visionintel.data.settings.UserPreferencesRepository
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIListRow
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import kotlinx.coroutines.launch
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class AppSettingsUiState(
    val title: String = "App Settings"
)

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onModelInfo: () -> Unit = {},
    onNetworkSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { UserPreferencesRepository.getInstance(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = UserPreferences())
    val scope = rememberCoroutineScope()
    val uiState = remember { AppSettingsUiState() }
    val showThemeDialog = remember { mutableStateOf(false) }
    val showLanguageDialog = remember { mutableStateOf(false) }
    val showInferenceModeDialog = remember { mutableStateOf(false) }

    if (showThemeDialog.value) {
        SelectionDialog(
            title = "Theme",
            options = ThemeMode.values().map { it.displayName() },
            current = preferences.themeMode.displayName(),
            onDismiss = { showThemeDialog.value = false },
            onSelect = { value ->
                val mode = ThemeMode.values().firstOrNull { it.displayName() == value } ?: ThemeMode.SYSTEM
                scope.launch { repository.updateThemeMode(mode) }
                showThemeDialog.value = false
            }
        )
    }

    if (showLanguageDialog.value) {
        SelectionDialog(
            title = "Language",
            options = listOf("English", "Spanish", "French", "Hindi"),
            current = preferences.language,
            onDismiss = { showLanguageDialog.value = false },
            onSelect = { value ->
                scope.launch { repository.updateLanguage(value) }
                showLanguageDialog.value = false
            }
        )
    }

    if (showInferenceModeDialog.value) {
        SelectionDialog(
            title = "Inference Mode",
            options = listOf("Auto", "Local Only", "Remote Only"),
            current = preferences.inferenceMode,
            onDismiss = { showInferenceModeDialog.value = false },
            onSelect = { value ->
                scope.launch { repository.updateInferenceMode(value) }
                showInferenceModeDialog.value = false
            }
        )
    }

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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
        ) {
            SettingsRow(
                label = "Theme",
                value = preferences.themeMode.displayName(),
                onClick = { showThemeDialog.value = true }
            )
            SettingsRow(
                label = "Language",
                value = preferences.language,
                onClick = { showLanguageDialog.value = true }
            )
            SettingsRow(
                label = "Model Information",
                value = null,
                onClick = onModelInfo,
                showChevron = true
            )
            SettingsRow(
                label = "Inference Mode",
                value = preferences.inferenceMode,
                onClick = { showInferenceModeDialog.value = true }
            )
            SettingsRow(
                label = "Network Settings",
                value = null,
                onClick = onNetworkSettings,
                showChevron = true
            )
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String?,
    onClick: () -> Unit,
    showChevron: Boolean = false
) {
    VIListRow(
        title = label,
        trailingText = value,
        onClick = onClick,
        showChevron = showChevron,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SelectionDialog(
    title: String,
    options: List<String>,
    current: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (option == current) {
                            Text(
                                text = "Selected",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    )
}

private fun ThemeMode.displayName(): String {
    return when (this) {
        ThemeMode.SYSTEM -> "Auto"
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    VisionIntelTheme {
        SettingsScreen()
    }
}
