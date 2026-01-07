package com.techmaina.visionintel.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.data.settings.UserPreferences
import com.techmaina.visionintel.data.settings.UserPreferencesRepository
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import kotlinx.coroutines.launch
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class NotificationSettingsUiState(
    val title: String = "Notification Settings"
)

@Composable
fun NotificationSettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val uiState = remember { NotificationSettingsUiState() }
    val context = LocalContext.current
    val repository = remember { UserPreferencesRepository.getInstance(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = UserPreferences())
    val scope = rememberCoroutineScope()

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
            SettingToggleRow(
                label = "Push Notifications",
                description = "Receive push notifications for new threat detections",
                checked = preferences.notifications.pushEnabled,
                onCheckedChange = { checked ->
                    scope.launch {
                        repository.updateNotificationPrefs(
                            pushEnabled = checked,
                            soundEnabled = preferences.notifications.soundEnabled,
                            vibrationEnabled = preferences.notifications.vibrationEnabled
                        )
                    }
                }
            )
            SettingToggleRow(
                label = "Sound",
                description = "Play a sound when a new threat is detected",
                checked = preferences.notifications.soundEnabled,
                onCheckedChange = { checked ->
                    scope.launch {
                        repository.updateNotificationPrefs(
                            pushEnabled = preferences.notifications.pushEnabled,
                            soundEnabled = checked,
                            vibrationEnabled = preferences.notifications.vibrationEnabled
                        )
                    }
                }
            )
            SettingToggleRow(
                label = "Vibration",
                description = "Vibrate when a new threat is detected",
                checked = preferences.notifications.vibrationEnabled,
                onCheckedChange = { checked ->
                    scope.launch {
                        repository.updateNotificationPrefs(
                            pushEnabled = preferences.notifications.pushEnabled,
                            soundEnabled = preferences.notifications.soundEnabled,
                            vibrationEnabled = checked
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationSettingsScreenPreview() {
    VisionIntelTheme {
        NotificationSettingsScreen()
    }
}
