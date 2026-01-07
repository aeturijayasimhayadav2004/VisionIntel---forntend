package com.techmaina.visionintel.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    VIAppTopBar(
        title = title,
        modifier = modifier,
        showBack = showBack,
        onBack = onBack
    )
}
