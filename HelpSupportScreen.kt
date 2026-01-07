package com.techmaina.visionintel.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.techmaina.visionintel.ui.components.VIListRow
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

data class HelpSupportUiState(
    val title: String = "Help & Support",
    val items: List<String> = listOf(
        "FAQ",
        "Contact Support",
        "Privacy Policy",
        "Terms of Service"
    )
)

@Composable
fun HelpSupportScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val uiState = remember { mutableStateOf(HelpSupportUiState()) }

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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            uiState.value.items.forEach { item ->
                SupportRow(label = item)
            }
        }
    }
}

@Composable
private fun SupportRow(
    label: String,
    modifier: Modifier = Modifier
) {
    VIListRow(
        title = label,
        onClick = {},
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun HelpSupportScreenPreview() {
    VisionIntelTheme {
        HelpSupportScreen()
    }
}
