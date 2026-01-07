package com.techmaina.visionintel.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.techmaina.visionintel.data.network.BackendApi
import com.techmaina.visionintel.data.network.TokenStore
import com.techmaina.visionintel.ui.components.AppBottomNav
import com.techmaina.visionintel.ui.components.VIAppTopBar
import com.techmaina.visionintel.ui.components.VICard
import com.techmaina.visionintel.ui.components.screenPadding
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.theme.spacing
import kotlinx.coroutines.launch
import org.json.JSONObject

data class SnapshotItem(
    val id: String,
    val jobId: String,
    val timestampMs: Long,
    val imageUrl: String,
    val threatType: String?,
    val createdAt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapshotsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onSnapshotClick: (SnapshotItem) -> Unit = {},
    onNavHome: () -> Unit = {},
    onNavAlerts: () -> Unit = {},
    onNavReports: () -> Unit = {},
    onNavHistory: () -> Unit = {},
    onNavProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var isLoading by remember { mutableStateOf(true) }
    var snapshots by remember { mutableStateOf<List<SnapshotItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedThreatType by remember { mutableStateOf<String?>(null) }
    
    // Get unique threat types for filtering
    val threatTypes = remember(snapshots) {
        snapshots.mapNotNull { it.threatType }.distinct().sorted()
    }
    
    // Filtered snapshots - remove duplicates by imageUrl, keep only threat images
    val filteredSnapshots = remember(snapshots, selectedThreatType) {
        val baseList = if (selectedThreatType == null) snapshots
            else snapshots.filter { it.threatType == selectedThreatType }
        
        // Remove duplicate images by URL and prioritize threat images
        baseList
            .filter { !it.threatType.isNullOrBlank() }  // Only show threat images
            .distinctBy { it.imageUrl }  // Remove duplicates
            .sortedByDescending { it.createdAt }  // Most recent first
    }
    
    // Group by date
    val groupedSnapshots = remember(filteredSnapshots) {
        filteredSnapshots.groupBy { it.createdAt.take(10) } // Group by date (YYYY-MM-DD)
    }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val tokenStore = TokenStore(context)
                val api = BackendApi(context, tokenStore)
                val response = api.getSnapshots(jobId = null)
                
                val snapshotsList = mutableListOf<SnapshotItem>()
                val dataArray = response.optJSONArray("snapshots") ?: return@launch
                
                for (i in 0 until dataArray.length()) {
                    val obj = dataArray.getJSONObject(i)
                    snapshotsList.add(
                        SnapshotItem(
                            id = obj.optString("id", ""),
                            jobId = obj.optString("job_id", ""),
                            timestampMs = obj.optLong("timestamp_ms", 0),
                            imageUrl = obj.optString("url", obj.optString("image_path", "")),
                            threatType = obj.optString("threat_type", null),
                            createdAt = obj.optString("created_at", "")
                        )
                    )
                }
                
                snapshots = snapshotsList
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load snapshots"
                isLoading = false
            }
        }
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VIAppTopBar(
                title = "Snapshots",
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .screenPadding()
        ) {
            // Filter chips
            if (threatTypes.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedThreatType == null,
                        onClick = { selectedThreatType = null },
                        label = { Text("All") }
                    )
                    threatTypes.forEach { type ->
                        FilterChip(
                            selected = selectedThreatType == type,
                            onClick = { 
                                selectedThreatType = if (selectedThreatType == type) null else type 
                            },
                            label = { 
                                Text(type.replaceFirstChar { it.uppercase() }) 
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
            }
            
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                filteredSnapshots.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No snapshots found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredSnapshots) { snapshot ->
                            SnapshotGridItem(
                                snapshot = snapshot,
                                onClick = { onSnapshotClick(snapshot) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SnapshotGridItem(
    snapshot: SnapshotItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = snapshot.imageUrl,
            contentDescription = "Snapshot",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Threat type badge
        if (!snapshot.threatType.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = snapshot.threatType.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun formatTimestamp(ms: Long): String {
    val seconds = ms / 1000
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) "${minutes}m ${secs}s" else "${secs}s"
}
