package com.techmaina.visionintel.ui.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.techmaina.visionintel.ui.screens.onboarding.SplashThreeScreen
import com.techmaina.visionintel.ui.theme.VisionIntelTheme

@Composable
fun Splash3Screen(
    modifier: Modifier = Modifier,
    onGetStarted: () -> Unit = {}
) {
    SplashThreeScreen(
        modifier = modifier,
        onGetStarted = onGetStarted
    )
}

@Preview(showBackground = true)
@Composable
fun Splash3ScreenPreview() {
    VisionIntelTheme {
        Splash3Screen()
    }
}
