package com.techmaina.visionintel.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.VideoCameraFront
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun VideoAnalysisIllustration(modifier: Modifier = Modifier) {
    IllustrationCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.VideoCameraFront,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
            Icon(
                imageVector = Icons.Outlined.AutoGraph,
                contentDescription = null,
                tint = Color(0xFFDDE3EA),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(44.dp)
            )
        }
    }
}

@Composable
fun CameraConnectIllustration(modifier: Modifier = Modifier) {
    IllustrationCard(
        modifier = modifier,
        background = Brush.linearGradient(
            colors = listOf(Color(0xFF3C6E6A), Color(0xFF2D4F52))
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.CameraAlt,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun ThreatAlertIllustration(modifier: Modifier = Modifier) {
    IllustrationCard(
        modifier = modifier,
        background = Brush.linearGradient(
            colors = listOf(Color(0xFF0F1A24), Color(0xFF22303D))
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = Color(0xFFE0B36A),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(44.dp)
            )
        }
    }
}

@Composable
fun ThumbnailPlaceholder(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Outlined.VideoCameraFront,
    background: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF101820), Color(0xFF22303D))
    ),
    iconTint: Color = Color.White
) {
    Box(
        modifier = modifier
            .background(background, shape = RoundedCornerShape(16.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun PermissionIconBadge(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Outlined.Notifications
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun IllustrationCard(
    modifier: Modifier,
    background: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF101820), Color(0xFF1C2B2F))
    ),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(background, shape = RoundedCornerShape(24.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x22FFFFFF),
                radius = size.minDimension * 0.35f,
                style = Stroke(width = size.minDimension * 0.02f)
            )
            drawCircle(
                color = Color(0x15FFFFFF),
                radius = size.minDimension * 0.48f,
                style = Stroke(width = size.minDimension * 0.015f)
            )
        }
        content()
    }
}
