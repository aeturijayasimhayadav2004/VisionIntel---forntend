package com.techmaina.visionintel.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.data.settings.UserPreferences
import com.techmaina.visionintel.data.settings.UserPreferencesRepository
import com.techmaina.visionintel.ui.components.VIInputField
import com.techmaina.visionintel.ui.components.VIPrimaryButton
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import kotlinx.coroutines.launch
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { UserPreferencesRepository.getInstance(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = UserPreferences())
    val profile = preferences.profile
    val scope = rememberCoroutineScope()

    var fullName by rememberSaveable { mutableStateOf(profile.fullName) }
    var email by rememberSaveable { mutableStateOf(profile.email) }
    var phone by rememberSaveable { mutableStateOf(profile.phone) }
    var role by rememberSaveable { mutableStateOf(profile.role) }

    LaunchedEffect(profile) {
        fullName = profile.fullName
        email = profile.email
        phone = profile.phone
        role = profile.role
    }

    val saveProfile: () -> Unit = {
        scope.launch {
            repository.updateProfile(
                fullName = fullName,
                email = email,
                phone = phone,
                role = role
            )
            onBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = saveProfile) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .screenPadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initialsFromName(fullName),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = fullName.ifBlank { "Your Name" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Change Photo",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            ProfileField(
                label = "Full Name",
                value = fullName,
                onValueChange = { fullName = it }
            )
            ProfileField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it }
            )
            ProfileField(
                label = "Phone Number",
                value = phone,
                onValueChange = { phone = it }
            )
            ProfileField(
                label = "Role",
                value = role,
                onValueChange = { role = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            VIPrimaryButton(
                text = "Save Changes",
                onClick = saveProfile,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        VIInputField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun initialsFromName(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    val initials = when {
        parts.isEmpty() -> "VI"
        parts.size == 1 -> parts.first().take(2)
        else -> parts[0].take(1) + parts[1].take(1)
    }
    return initials.uppercase()
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    VisionIntelTheme {
        EditProfileScreen()
    }
}
