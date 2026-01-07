package com.techmaina.visionintel.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import com.techmaina.visionintel.ui.theme.spacing

@Composable
fun Modifier.screenPadding(
    horizontal: Boolean = true,
    vertical: Boolean = true
): Modifier {
    val spacing = MaterialTheme.spacing
    val horizontalPadding = if (horizontal) spacing.screenHorizontal else 0.dp
    val verticalPadding = if (vertical) spacing.screenVertical else 0.dp
    return this.padding(horizontal = horizontalPadding, vertical = verticalPadding)
}
