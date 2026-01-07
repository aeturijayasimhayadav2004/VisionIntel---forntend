package com.techmaina.visionintel.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIInputField
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class ResetPasswordUiState(
    val title: String = "Reset Password",
    val newLabel: String = "New Password",
    val newPlaceholder: String = "Enter new password",
    val confirmLabel: String = "Confirm New Password",
    val confirmPlaceholder: String = "Confirm new password",
    val buttonLabel: String = "Reset Password"
)

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val uiState = remember { ResetPasswordUiState() }
    val newPassword = rememberSaveable { mutableStateOf("") }
    val confirmPassword = rememberSaveable { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.section)
        ) {
            Text(
                text = uiState.newLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            PasswordField(
                value = newPassword.value,
                placeholder = uiState.newPlaceholder,
                onValueChange = {
                    newPassword.value = it
                    errorMessage.value = null
                }
            )

            Text(
                text = uiState.confirmLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            PasswordField(
                value = confirmPassword.value,
                placeholder = uiState.confirmPlaceholder,
                onValueChange = {
                    confirmPassword.value = it
                    errorMessage.value = null
                }
            )

            if (errorMessage.value != null) {
                Text(
                    text = errorMessage.value.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            VIPrimaryButton(
                text = uiState.buttonLabel,
                onClick = {
                    if (newPassword.value.isBlank() || confirmPassword.value.isBlank()) {
                        errorMessage.value = "Please complete all fields."
                    } else if (newPassword.value != confirmPassword.value) {
                        errorMessage.value = "Passwords do not match."
                    } else {
                        errorMessage.value = "Password reset (placeholder)."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    val visible = rememberSaveable { mutableStateOf(false) }
    VIInputField(
        value = value,
        onValueChange = onValueChange,
        label = placeholder,
        placeholder = placeholder,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (visible.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = if (visible.value) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
        trailingIconDescription = if (visible.value) "Hide password" else "Show password",
        onTrailingIconClick = { visible.value = !visible.value }
    )
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    VisionIntelTheme {
        ChangePasswordScreen()
    }
}
