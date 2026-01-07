package com.techmaina.visionintel.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VIInputField
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.theme.spacing

private val threatOptions = listOf(
    "Suspicious Activity",
    "Intrusion",
    "Weapon Detected",
    "Fight / Assault",
    "Unknown / Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterScreen(
    modifier: Modifier = Modifier,
    searchQuery: String,
    selectedDate: String?,
    selectedThreatType: String?,
    onSearchQueryChange: (String) -> Unit,
    onDateSelected: (String?) -> Unit,
    onThreatSelected: (String?) -> Unit,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showThreatMenu by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    val dateText = selectedDate.orEmpty()

    if (showDatePicker) {
        val initialDate = selectedDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val initialMillis = initialDate
            ?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
        val pickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis
                        val picked = millis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        onDateSelected(picked?.format(formatter))
                        showDatePicker = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = "Search & Filter",
                showBack = true,
                onBack = onBack,
                showSettings = true,
                onSettings = onSettings
            )
        },
        bottomBar = {
            AppBottomNav(
                selectedTab = HomeTab.HISTORY,
                onHome = onNavHome,
                onAlerts = onNavAlerts,
                onReports = onNavReports,
                onHistory = onNavHistory,
                onProfile = onNavProfile
            )
        }
    ) { innerPadding ->
        SearchFilterContent(
            innerPadding = innerPadding,
            searchQuery = searchQuery,
            dateText = dateText,
            threatType = selectedThreatType.orEmpty(),
            modifier = modifier,
            onSearchQueryChange = onSearchQueryChange,
            onDateClick = { showDatePicker = true },
            onDateClear = { onDateSelected(null) },
            onThreatClick = { showThreatMenu = true },
            onThreatClear = { onThreatSelected(null) },
            threatMenuExpanded = showThreatMenu,
            onThreatDismiss = { showThreatMenu = false },
            onThreatSelected = {
                onThreatSelected(it)
                showThreatMenu = false
            }
        )
    }
}

@Composable
private fun SearchFilterContent(
    innerPadding: PaddingValues,
    searchQuery: String,
    dateText: String,
    threatType: String,
    modifier: Modifier = Modifier,
    onSearchQueryChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onDateClear: () -> Unit,
    onThreatClick: () -> Unit,
    onThreatClear: () -> Unit,
    threatMenuExpanded: Boolean,
    onThreatDismiss: () -> Unit,
    onThreatSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .screenPadding(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.section)
    ) {
        VIInputField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = "Search history",
            placeholder = "Search history",
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Date",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        FilterField(
            value = dateText,
            placeholder = "Select date",
            onClick = onDateClick,
            onClear = if (dateText.isNotBlank()) onDateClear else null
        )

        Text(
            text = "Threat Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        androidx.compose.foundation.layout.Box {
            FilterField(
                value = threatType,
                placeholder = "Select threat type",
                onClick = onThreatClick,
                onClear = if (threatType.isNotBlank()) onThreatClear else null
            )

            androidx.compose.material3.DropdownMenu(
                expanded = threatMenuExpanded,
                onDismissRequest = onThreatDismiss
            ) {
                threatOptions.forEach { option ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = { onThreatSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterField(
    value: String,
    placeholder: String,
    onClick: () -> Unit,
    onClear: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(horizontal = MaterialTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (value.isBlank()) placeholder else value,
            style = MaterialTheme.typography.bodyLarge,
            color = if (value.isBlank()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        if (onClear != null) {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Clear"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchFilterScreenPreview() {
    VisionIntelTheme {
        SearchFilterContent(
            innerPadding = PaddingValues(),
            searchQuery = "",
            dateText = "",
            threatType = "",
            onSearchQueryChange = {},
            onDateClick = {},
            onDateClear = {},
            onThreatClick = {},
            onThreatClear = {},
            threatMenuExpanded = false,
            onThreatDismiss = {},
            onThreatSelected = {}
        )
    }
}
