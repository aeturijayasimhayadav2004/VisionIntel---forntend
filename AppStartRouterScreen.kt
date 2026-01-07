package com.techmaina.visionintel.ui.screens.splash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techmaina.visionintel.R
import com.techmaina.visionintel.core.storage.AppPrefs
import com.techmaina.visionintel.ui.theme.VisionIntelTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppStartRouterScreen(
    modifier: Modifier = Modifier,
    onNavigateToSplash: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val onboardingCompleted = withContext(Dispatchers.IO) {
            AppPrefs.getOnboardingCompleted(context)
        }
        val isLoggedIn = withContext(Dispatchers.IO) {
            AppPrefs.getIsLoggedIn(context)
        }
        val destination = "SPLASH_1"
        Log.d("AppStartRouter", "onboardingCompleted=$onboardingCompleted")
        Log.d("AppStartRouter", "isLoggedIn=$isLoggedIn")
        Log.d("AppStartRouter", "destination=$destination")
        onNavigateToSplash()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.visionintel_logo),
            contentDescription = "VisionIntel logo",
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = "VisionIntel",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppStartRouterScreenPreview() {
    VisionIntelTheme {
        AppStartRouterScreen()
    }
}
