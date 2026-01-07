package com.techmaina.visionintel.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.core.storage.AppPrefs
import com.techmaina.visionintel.data.network.BackendApi
import com.techmaina.visionintel.data.network.BackendConfig
import com.techmaina.visionintel.data.network.TokenStore
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VICard
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.components.VISecondaryButton
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.theme.spacing
import kotlinx.coroutines.launch
import java.util.UUID

sealed class ConnectionTestResult {
    data object Idle : ConnectionTestResult()
    data object Testing : ConnectionTestResult()
    data class Success(val backendOk: Boolean, val mlOk: Boolean, val modelLoaded: Boolean) : ConnectionTestResult()
    data class Error(val message: String) : ConnectionTestResult()
}

@Composable
fun NetworkSettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var currentUrl by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    var testResult by remember { mutableStateOf<ConnectionTestResult>(ConnectionTestResult.Idle) }
    var isSaving by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    
    // Load current URL on first composition
    LaunchedEffect(Unit) {
        val override = AppPrefs.getBackendBaseUrlOverride(context)
        val resolved = BackendConfig.getBaseUrl(context)
        currentUrl = resolved
        urlInput = override ?: ""
    }
    
    val tokenStore = remember { TokenStore(context) }
    val api = remember { BackendApi(context, tokenStore) }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = "Network Settings",
                showBack = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .screenPadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            // Current URL display
            VICard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Current Backend URL",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentUrl.ifBlank { "(using default)" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // URL input
            OutlinedTextField(
                value = urlInput,
                onValueChange = { 
                    urlInput = it
                    saveMessage = null
                },
                label = { Text("Backend URL Override") },
                placeholder = { Text("http://192.168.1.x/visionintel-backend/public/api/v1") },
                supportingText = { 
                    Text("Leave empty to use default. For physical device, use your PC's LAN IP or set up adb reverse.")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Save and Reset buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                VIPrimaryButton(
                    text = if (isSaving) "Saving..." else "Save URL",
                    onClick = {
                        scope.launch {
                            isSaving = true
                            saveMessage = null
                            try {
                                BackendConfig.setBaseUrlOverride(context, urlInput.trim().ifBlank { null })
                                currentUrl = BackendConfig.getBaseUrl(context)
                                saveMessage = "URL saved successfully"
                                testResult = ConnectionTestResult.Idle
                            } catch (e: Exception) {
                                saveMessage = "Failed to save: ${e.message}"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f)
                )
                
                VISecondaryButton(
                    text = "Reset",
                    onClick = {
                        scope.launch {
                            isSaving = true
                            urlInput = ""
                            BackendConfig.setBaseUrlOverride(context, null)
                            currentUrl = BackendConfig.getBaseUrl(context)
                            saveMessage = "Reset to default"
                            testResult = ConnectionTestResult.Idle
                            isSaving = false
                        }
                    },
                    enabled = !isSaving && urlInput.isNotBlank(),
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Save message
            AnimatedVisibility(visible = saveMessage != null) {
                Text(
                    text = saveMessage.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (saveMessage?.contains("success") == true) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            
            // Connection Test Section
            Text(
                text = "Connection Test",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            VIPrimaryButton(
                text = "Test Connection",
                onClick = {
                    scope.launch {
                        testResult = ConnectionTestResult.Testing
                        try {
                            val requestId = UUID.randomUUID().toString()
                            val healthResult = api.health(requestId)
                            val backendOk = healthResult.optBoolean("ok", false) || 
                                healthResult.has("status") // Some health endpoints return {status: "ok"}
                            
                            var mlOk = false
                            var modelLoaded = false
                            try {
                                val mlResult = api.mlHealth(requestId)
                                mlOk = mlResult.optBoolean("ok", false)
                                modelLoaded = mlResult.optBoolean("model_loaded", false)
                            } catch (_: Exception) {
                                // ML service might not be running
                            }
                            
                            testResult = ConnectionTestResult.Success(
                                backendOk = backendOk,
                                mlOk = mlOk,
                                modelLoaded = modelLoaded
                            )
                        } catch (e: Exception) {
                            testResult = ConnectionTestResult.Error(
                                e.message ?: "Connection failed"
                            )
                        }
                    }
                },
                enabled = testResult !is ConnectionTestResult.Testing,
                leadingIcon = {
                    if (testResult is ConnectionTestResult.Testing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Test Results
            AnimatedVisibility(visible = testResult !is ConnectionTestResult.Idle) {
                VICard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = when (testResult) {
                        is ConnectionTestResult.Success -> MaterialTheme.colorScheme.primaryContainer
                        is ConnectionTestResult.Error -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    when (val result = testResult) {
                        is ConnectionTestResult.Testing -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Testing connection...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        is ConnectionTestResult.Success -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ConnectionStatusRow(
                                    label = "Backend API",
                                    isOk = result.backendOk
                                )
                                ConnectionStatusRow(
                                    label = "ML Service",
                                    isOk = result.mlOk
                                )
                                if (result.mlOk) {
                                    ConnectionStatusRow(
                                        label = "Model Loaded",
                                        isOk = result.modelLoaded
                                    )
                                }
                            }
                        }
                        is ConnectionTestResult.Error -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = result.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            
            // Help text
            VICard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Setup Instructions",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        |For Emulator: Use default (10.0.2.2)
                        |
                        |For Physical Device (USB):
                        |1. Run: adb reverse tcp:8080 tcp:80
                        |2. Set URL: http://127.0.0.1:8080/visionintel-backend/public/api/v1
                        |
                        |For Physical Device (Wi-Fi):
                        |1. Find your PC's IP (ipconfig)
                        |2. Set URL: http://[PC-IP]/visionintel-backend/public/api/v1
                        |3. Ensure firewall allows port 80
                    """.trimMargin(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ConnectionStatusRow(
    label: String,
    isOk: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isOk) Icons.Outlined.CheckCircle else Icons.Outlined.Error,
            contentDescription = null,
            tint = if (isOk) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if (isOk) "OK" else "Not Available",
            style = MaterialTheme.typography.bodySmall,
            color = if (isOk) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkSettingsScreenPreview() {
    VisionIntelTheme {
        NetworkSettingsScreen()
    }
}
