package com.techmaina.visionintel.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techmaina.visionintel.BuildConfig
import com.techmaina.visionintel.di.LocalAppContainer
import com.techmaina.visionintel.core.model.AnalysisRequest
import com.techmaina.visionintel.core.model.AnalysisSourceType
import com.techmaina.visionintel.core.storage.AppPrefs
import com.techmaina.visionintel.data.history.HistoryViewModel
import com.techmaina.visionintel.ui.screens.ComingSoonScreen
import com.techmaina.visionintel.ui.screens.alerts.AcknowledgeAlertScreen
import com.techmaina.visionintel.ui.screens.alerts.AlertDetailScreen
import com.techmaina.visionintel.ui.screens.alerts.AlertsScreen
import com.techmaina.visionintel.ui.screens.alerts.DailySummaryScreen
import com.techmaina.visionintel.ui.screens.alerts.DetailedReportScreen
import com.techmaina.visionintel.ui.screens.alerts.ExportReportScreen
import com.techmaina.visionintel.ui.screens.alerts.ThreatDetectedScreen
import com.techmaina.visionintel.ui.screens.auth.ForgotPasswordScreen
import com.techmaina.visionintel.ui.screens.auth.GoogleAuthenticatingScreen
import com.techmaina.visionintel.ui.screens.auth.GoogleConnectedScreen
import com.techmaina.visionintel.ui.screens.auth.LoginScreen
import com.techmaina.visionintel.ui.screens.auth.RegisterScreen
import com.techmaina.visionintel.ui.screens.auth.ResetPasswordScreen
import com.techmaina.visionintel.ui.screens.analysis.AnalysisResultScreen
import com.techmaina.visionintel.ui.screens.analysis.AnalysisPlaceholderScreen
import com.techmaina.visionintel.ui.screens.analysis.LiveCameraFeedScreen
import com.techmaina.visionintel.ui.screens.analysis.SelectVideoSourceScreen
import com.techmaina.visionintel.ui.screens.analysis.DroneVideoUploadScreen
import com.techmaina.visionintel.ui.screens.analysis.ProcessingDroneVideoScreen
import com.techmaina.visionintel.ui.screens.analysis.ProcessingVideoAnalysisScreen
import com.techmaina.visionintel.ui.screens.analysis.JobReportScreen
import com.techmaina.visionintel.ui.screens.analysis.UploadVideoScreen
import com.techmaina.visionintel.ui.screens.history.HistoryDashboardScreen
import com.techmaina.visionintel.ui.screens.history.HistoryDetailScreen
import com.techmaina.visionintel.ui.screens.history.SearchFilterScreen
import com.techmaina.visionintel.ui.screens.history.SnapshotGalleryScreen
import com.techmaina.visionintel.ui.screens.home.HomeScreen
import com.techmaina.visionintel.ui.screens.home.HomeTab
import com.techmaina.visionintel.ui.screens.onboarding.PermissionsScreen
import com.techmaina.visionintel.ui.screens.onboarding.StoragePermissionScreen
import com.techmaina.visionintel.ui.screens.onboarding.SplashThreeScreen
import com.techmaina.visionintel.ui.screens.profile.ProfileDashboardScreen
import com.techmaina.visionintel.ui.screens.profile.EditProfileScreen
import com.techmaina.visionintel.ui.screens.settings.AboutVisionIntelScreen
import com.techmaina.visionintel.ui.screens.settings.AiModelInfoScreen
import com.techmaina.visionintel.ui.screens.settings.ChangePasswordScreen
import com.techmaina.visionintel.ui.screens.settings.HelpSupportScreen
import com.techmaina.visionintel.ui.screens.settings.LanguageSettingsScreen
import com.techmaina.visionintel.ui.screens.settings.NetworkSettingsScreen
import com.techmaina.visionintel.ui.screens.settings.NotificationSettingsScreen
import com.techmaina.visionintel.ui.screens.settings.SettingsScreen
import com.techmaina.visionintel.ui.screens.settings.ThemeSettingsScreen
import com.techmaina.visionintel.ui.screens.splash.AppStartRouterScreen
import com.techmaina.visionintel.ui.screens.splash.Splash1Screen
import com.techmaina.visionintel.ui.screens.splash.Splash2Screen
import com.techmaina.visionintel.ui.screens.splash.Splash3Screen
import com.techmaina.visionintel.ui.screens.status.NetworkStatusScreen
import com.techmaina.visionintel.ui.screens.status.MlSmokeTestScreen
import com.techmaina.visionintel.ui.screens.reports.ReportsDashboardScreen
import com.techmaina.visionintel.ui.screens.reports.ReportsHistoryScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

// DO NOT CHANGE startDestination. Add new screens as additional composables only.
@Composable
@OptIn(ExperimentalAnimationApi::class)
fun AppNavGraph(
    startDestination: String = Routes.APP_START
) {
    require(startDestination == Routes.APP_START) {
        "startDestination must remain APP_START to protect onboarding flow."
    }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val container = LocalAppContainer.current
    val analysisRepository = container.analysisRepository
    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory(container.historyRepository))
    fun currentBaseRoute(): String? {
        val route = navController.currentBackStackEntry?.destination?.route ?: return null
        return route.substringBefore("?")
    }
    fun navigateBottomBar(targetRoute: String) {
        val popUpToId = navController.graph.findNode(Routes.HOME)?.id ?: navController.graph.startDestinationId
        navController.navigate(targetRoute) {
            popUpTo(popUpToId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateHomeRoot() {
        val homeId = navController.graph.findNode(Routes.HOME)?.id
        val popped = if (homeId != null) navController.popBackStack(homeId, false) else navController.popBackStack(Routes.HOME, false)
        if (!popped) {
            navigateBottomBar(targetRoute = Routes.HOME)
        }
    }
    fun navigateAlertsRoot() {
        val alertsId = navController.graph.findNode(Routes.ALERTS_LIST)?.id
        val popped = if (alertsId != null) navController.popBackStack(alertsId, false) else navController.popBackStack(Routes.ALERTS_LIST, false)
        if (!popped) {
            navigateBottomBar(targetRoute = Routes.ALERTS_LIST)
        }
    }
    fun navigateReportsRoot() {
        val reportsId = navController.graph.findNode(Routes.REPORTS_DASHBOARD)?.id
        val popped = if (reportsId != null) navController.popBackStack(reportsId, false) else navController.popBackStack(Routes.REPORTS_DASHBOARD, false)
        if (!popped) {
            navigateBottomBar(targetRoute = Routes.REPORTS_DASHBOARD)
        }
    }
    fun navigateHistoryRoot() {
        val historyId = navController.graph.findNode(Routes.HISTORY_DASHBOARD)?.id
        val popped = if (historyId != null) navController.popBackStack(historyId, false) else navController.popBackStack(Routes.HISTORY_DASHBOARD, false)
        if (!popped) {
            navigateBottomBar(targetRoute = Routes.HISTORY_DASHBOARD)
        }
    }
    fun navigateProfileRoot() {
        val profileId = navController.graph.findNode(Routes.PROFILE_DASHBOARD)?.id
        val popped = if (profileId != null) navController.popBackStack(profileId, false) else navController.popBackStack(Routes.PROFILE_DASHBOARD, false)
        if (!popped) {
            navigateBottomBar(targetRoute = Routes.PROFILE_DASHBOARD)
        }
    }
    fun navigateWithOrigin(originRoute: String?, threatsFound: Boolean) {
        val route = if (threatsFound) {
            Routes.alerts(originRoute)
        } else {
            Routes.analysisResult(originRoute)
        }
        navController.navigate(route)
    }
    fun handleOriginBack(originRoute: String?) {
        if (!originRoute.isNullOrBlank()) {
            val popped = navController.popBackStack(originRoute, false)
            if (!popped) {
                navController.navigate(originRoute) {
                    launchSingleTop = true
                }
            }
        } else {
            val popped = navController.popBackStack()
            if (!popped) {
                navigateHomeRoot()
            }
        }
    }
    fun openSettings() {
        val currentRoute = currentBaseRoute()
        if (currentRoute == Routes.SETTINGS) return
        navController.navigate(Routes.SETTINGS) {
            launchSingleTop = true
        }
    }
    fun hasRequiredPermissions(): Boolean {
        val permissions = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_VIDEO)
                add(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasStoragePermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { with(NavTransitions) { enter() } },
        exitTransition = { with(NavTransitions) { exit() } },
        popEnterTransition = { with(NavTransitions) { popEnter() } },
        popExitTransition = { with(NavTransitions) { popExit() } }
    ) {
        composable(Routes.APP_START) {
            AppStartRouterScreen(
                onNavigateToSplash = {
                    navController.navigate(Routes.SPLASH_1) {
                        popUpTo(Routes.APP_START) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.APP_START) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.SPLASH_1) {
            Splash1Screen(
                onGetStarted = { navController.navigate(Routes.SPLASH_2) }
            )
        }
        composable(Routes.SPLASH_2) {
            Splash2Screen(
                onBack = { navController.navigateUp() },
                onNext = { navController.navigate(Routes.SPLASH_3) }
            )
        }
        composable(Routes.SPLASH_3) {
            Splash3Screen(
                onGetStarted = {
                    scope.launch {
                        val permissionsSeen = withContext(Dispatchers.IO) {
                            AppPrefs.setOnboardingCompleted(context, true)
                            AppPrefs.getPermissionsPromptSeen(context)
                        }
                        val destination = if (!hasRequiredPermissions() || !permissionsSeen) Routes.PERMISSIONS else Routes.LOGIN
                        navController.navigate(destination) {
                            popUpTo(Routes.SPLASH_1) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(Routes.STORAGE_PERMISSION) {
            StoragePermissionScreen(
                onGranted = {
                    scope.launch {
                        val permissionsSeen = withContext(Dispatchers.IO) {
                            AppPrefs.getPermissionsPromptSeen(context)
                        }
                        val destination = if (!permissionsSeen || !hasRequiredPermissions()) {
                            Routes.PERMISSIONS
                        } else {
                            Routes.HOME
                        }
                        navController.navigate(destination) {
                            popUpTo(Routes.STORAGE_PERMISSION) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onSettings = { openSettings() }
            )
        }
        composable(Routes.PERMISSIONS) {
            val previousRoute = navController.previousBackStackEntry
                ?.destination
                ?.route
                ?.substringBefore("?")
            val openedFromProfile = previousRoute == Routes.PROFILE_DASHBOARD

            PermissionsScreen(
                onGrant = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            AppPrefs.setPermissionsPromptSeen(context, true)
                        }
                        if (openedFromProfile) {
                            navController.popBackStack()
                        } else {
                            val isLoggedIn = withContext(Dispatchers.IO) {
                                AppPrefs.getIsLoggedIn(context)
                            }
                            val destination = if (isLoggedIn) Routes.HOME else Routes.LOGIN
                            navController.navigate(destination) {
                                popUpTo(Routes.PERMISSIONS) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                onLater = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            AppPrefs.setPermissionsPromptSeen(context, true)
                        }
                        if (openedFromProfile) {
                            navController.popBackStack()
                        } else {
                            val isLoggedIn = withContext(Dispatchers.IO) {
                                AppPrefs.getIsLoggedIn(context)
                            }
                            val destination = if (isLoggedIn) Routes.HOME else Routes.LOGIN
                            navController.navigate(destination) {
                                popUpTo(Routes.PERMISSIONS) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                showBack = openedFromProfile,
                onBack = if (openedFromProfile) ({ navController.popBackStack() }) else null
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            AppPrefs.setIsLoggedIn(context, true)
                        }
                        val permissionsSeen = withContext(Dispatchers.IO) {
                            AppPrefs.getPermissionsPromptSeen(context)
                        }
                        val destination = when {
                            !hasStoragePermission() -> Routes.STORAGE_PERMISSION
                            !permissionsSeen || !hasRequiredPermissions() -> Routes.PERMISSIONS
                            else -> Routes.HOME
                        }
                        navController.navigate(destination) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onCreateAccount = { navController.navigate(Routes.REGISTER) },
                onGoogleSignIn = { navController.navigate(Routes.GOOGLE_AUTHENTICATING) },
                onNetworkStatus = { navController.navigate(Routes.NETWORK_STATUS_SETUP) }
            )
        }
        composable(Routes.GOOGLE_AUTHENTICATING) {
            GoogleAuthenticatingScreen(
                onAuthenticated = {
                    navController.navigate(Routes.GOOGLE_CONNECTED) {
                        popUpTo(Routes.LOGIN) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.GOOGLE_CONNECTED) {
            GoogleConnectedScreen(
                onContinue = {
                    scope.launch(Dispatchers.IO) {
                        AppPrefs.setIsLoggedIn(context, true)
                    }
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBack = { navController.navigateUp() },
                onReset = { navController.navigate(Routes.RESET_PASSWORD) }
            )
        }
        composable(Routes.RESET_PASSWORD) {
            ResetPasswordScreen(
                onBack = { navController.navigateUp() },
                onReset = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                selectedTab = HomeTab.HOME,
                onSettings = {
                    openSettings()
                },
                onSystemStatus = { navController.navigate(Routes.NETWORK_STATUS) },
                onStartNewAnalysis = { navController.navigate(Routes.SELECT_VIDEO_SOURCE) },
                onUploadVideo = {},
                onLiveCamera = {},
                onViewAllAlerts = {
                    navController.navigate(Routes.alerts(Routes.HOME))
                },
                onNavHome = {
                    navigateHomeRoot()
                },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = {
                    navigateReportsRoot()
                },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.SELECT_VIDEO_SOURCE) {
            SelectVideoSourceScreen(
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onUploadVideo = { navController.navigate(Routes.UPLOAD_VIDEO) },
                onLiveCamera = { navController.navigate(Routes.LIVE_CAMERA_FEED) },
                onDroneVideo = { navController.navigate(Routes.DRONE_VIDEO_UPLOAD) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.DRONE_VIDEO_UPLOAD) {
            DroneVideoUploadScreen(
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onStartAnalysis = { uri ->
                    navController.navigate(Routes.processingVideo(source = AnalysisSourceType.DRONE_VIDEO.name, uri = uri))
                },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.PROCESSING_DRONE_VIDEO,
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")?.takeIf { it.isNotBlank() } ?: ""
            // Back-compat: route existed before job-based processing. Reuse the same non-blocking pipeline.
            ProcessingVideoAnalysisScreen(
                analysisRepository = analysisRepository,
                sourceType = AnalysisSourceType.DRONE_VIDEO,
                uri = uri,
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onFinished = { result ->
                    val jid = result.sessionId ?: return@ProcessingVideoAnalysisScreen
                    navController.navigate(Routes.jobReport(jobId = jid, originRoute = Routes.DRONE_VIDEO_UPLOAD)) {
                        popUpTo(Routes.PROCESSING_DRONE_VIDEO) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNetworkStatus = { navController.navigate(Routes.NETWORK_STATUS) { launchSingleTop = true } },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.PROCESSING_VIDEO_ANALYSIS,
            arguments = listOf(
                navArgument("source") { type = NavType.StringType; defaultValue = "" },
                navArgument("uri") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source").orEmpty()
            val uri = backStackEntry.arguments?.getString("uri").orEmpty()
            val sourceType = AnalysisSourceType.from(source)
            ProcessingVideoAnalysisScreen(
                analysisRepository = analysisRepository,
                sourceType = sourceType,
                uri = uri,
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onFinished = { result ->
                    val originRoute = when (sourceType) {
                        AnalysisSourceType.DRONE_VIDEO -> Routes.DRONE_VIDEO_UPLOAD
                        else -> Routes.UPLOAD_VIDEO
                    }
                    val jid = result.sessionId ?: return@ProcessingVideoAnalysisScreen
                    navController.navigate(Routes.jobReport(jobId = jid, originRoute = originRoute)) {
                        popUpTo(Routes.PROCESSING_VIDEO_ANALYSIS) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNetworkStatus = { navController.navigate(Routes.NETWORK_STATUS) { launchSingleTop = true } },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
	        composable(Routes.NETWORK_STATUS) {
	            NetworkStatusScreen(
	                onBack = { navController.navigateUp() },
	                onSettings = {
	                    openSettings()
	                },
	                onNavHome = { navigateHomeRoot() },
	                onNavAlerts = { navigateAlertsRoot() },
	                onNavReports = { navigateReportsRoot() },
	                onNavHistory = { navigateHistoryRoot() },
	                onNavProfile = { navigateProfileRoot() }
	            )
	        }
	        composable(Routes.NETWORK_STATUS_SETUP) {
	            NetworkStatusScreen(
	                showBottomNav = false,
	                onBack = { navController.navigateUp() },
	                onSettings = {
	                    openSettings()
	                }
	            )
	        }
            composable(
                route = Routes.JOB_REPORT,
                arguments = listOf(
                    navArgument("jobId") { type = NavType.StringType; defaultValue = "" },
                    navArgument("origin") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId").orEmpty()
                val origin = backStackEntry.arguments?.getString("origin").orEmpty().ifBlank { null }
                JobReportScreen(
                    analysisRepository = analysisRepository,
                    jobId = jobId,
                    onBack = { handleOriginBack(origin) },
                    onExport = { reportId -> navController.navigate(Routes.exportReport(reportId)) { launchSingleTop = true } },
                    onExportReport = { jid -> navController.navigate(Routes.exportReportByJob(jid)) { launchSingleTop = true } },
                    onNavHome = { navigateHomeRoot() },
                    onNavAlerts = { navigateAlertsRoot() },
                    onNavReports = { navigateReportsRoot() },
                    onNavHistory = { navigateHistoryRoot() },
                    onNavProfile = { navigateProfileRoot() }
                )
            }
	        composable(Routes.ML_SMOKE_TEST) {
	            MlSmokeTestScreen(
	                onBack = { navController.navigateUp() }
	            )
	        }
	        composable(Routes.UPLOAD_VIDEO) {
	            UploadVideoScreen(
	                onBack = { navController.navigateUp() },
	                onSettings = { openSettings() },
	                onStartAnalysis = { uri ->
                        navController.navigate(Routes.processingVideo(source = AnalysisSourceType.UPLOAD_VIDEO.name, uri = uri))
	                },
                onNavHome = {
                    navigateHomeRoot()
                },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = {
                    navigateReportsRoot()
                },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
	        composable(Routes.LIVE_CAMERA_FEED) {
	            LiveCameraFeedScreen(
                    analysisRepository = analysisRepository,
	                onBack = { navController.navigateUp() },
	                onSettings = { openSettings() },
                    onSessionFinished = { result ->
                        navigateWithOrigin(
                            originRoute = Routes.LIVE_CAMERA_FEED,
                            threatsFound = result.hasThreat
                        )
                    },
                    onNetworkError = {
                        navController.navigate(Routes.NETWORK_STATUS) { launchSingleTop = true }
                    },
	                onNavHome = {
	                    navigateHomeRoot()
	                },
	                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = {
                    navigateReportsRoot()
                },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.ANALYSIS_PLACEHOLDER,
            arguments = listOf(
                navArgument("source") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("uri") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source")
            val uri = backStackEntry.arguments?.getString("uri")?.takeIf { it.isNotBlank() }
            val request = AnalysisRequest(
                sourceType = AnalysisSourceType.from(source),
                uri = uri
            )
            AnalysisPlaceholderScreen(
                request = request,
                onBack = { navController.navigateUp() },
                onReturnHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = Routes.ALERTS,
            arguments = listOf(
                navArgument("origin") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val origin = backStackEntry.arguments
                ?.getString("origin")
                ?.takeIf { it.isNotBlank() }
                ?.let(Uri::decode)
            AlertsScreen(
                showBack = origin != null,
                onBack = { handleOriginBack(origin) },
                onSettings = { openSettings() },
                onDailySummary = { navController.navigate(Routes.DAILY_SUMMARY) },
                onAlertSelected = { item ->
                    navController.navigate(Routes.threatDetected(alertId = item.id, reportId = item.reportId))
                },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.ANALYSIS_RESULT,
            arguments = listOf(
                navArgument("origin") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val origin = backStackEntry.arguments
                ?.getString("origin")
                ?.takeIf { it.isNotBlank() }
                ?.let(Uri::decode)
            AnalysisResultScreen(
                onBack = { handleOriginBack(origin) },
                onSettings = { openSettings() },
                onBackToHome = { navigateHomeRoot() },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.DAILY_SUMMARY) {
            DailySummaryScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.THREAT_DETECTED) {
            ThreatDetectedScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onViewDetails = { navController.navigate(Routes.ALERT_DETAIL) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.THREAT_DETECTED_WITH_IDS,
            arguments = listOf(
                navArgument("alertId") { type = NavType.StringType; defaultValue = "" },
                navArgument("reportId") { type = NavType.StringType; defaultValue = "" },
            )
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId")?.takeIf { it.isNotBlank() }
            val reportId = backStackEntry.arguments?.getString("reportId")?.takeIf { it.isNotBlank() }
            ThreatDetectedScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onViewDetails = { navController.navigate(Routes.alertDetail(alertId = alertId, reportId = reportId)) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.ALERT_DETAIL) {
            AlertDetailScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onViewReport = { navController.navigate(Routes.DETAILED_REPORT) },
                onAcknowledge = { navController.navigate(Routes.ACKNOWLEDGE_ALERT) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.ALERT_DETAIL_WITH_IDS,
            arguments = listOf(
                navArgument("alertId") { type = NavType.StringType; defaultValue = "" },
                navArgument("reportId") { type = NavType.StringType; defaultValue = "" },
            )
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId")?.takeIf { it.isNotBlank() }
            val reportId = backStackEntry.arguments?.getString("reportId")?.takeIf { it.isNotBlank() }
            AlertDetailScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onViewReport = { navController.navigate(Routes.detailedReport(reportId)) },
                onAcknowledge = { navController.navigate(Routes.acknowledgeAlert(alertId)) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.DETAILED_REPORT) {
            DetailedReportScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onExport = { navController.navigate(Routes.EXPORT_REPORT) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.DETAILED_REPORT_WITH_ID,
            arguments = listOf(
                navArgument("reportId") { type = NavType.StringType; defaultValue = "" },
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")?.takeIf { it.isNotBlank() }
            DetailedReportScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onExport = { navController.navigate(Routes.exportReport(reportId)) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.EXPORT_REPORT) {
            ExportReportScreen(
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.EXPORT_REPORT_WITH_ID,
            arguments = listOf(
                navArgument("reportId") { type = NavType.StringType; defaultValue = "" },
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")?.takeIf { it.isNotBlank() }
            ExportReportScreen(
                reportId = reportId,
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.EXPORT_REPORT_WITH_JOB_ID,
            arguments = listOf(
                navArgument("jobId") { type = NavType.StringType; defaultValue = "" },
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId")?.takeIf { it.isNotBlank() }
            ExportReportScreen(
                jobId = jobId,
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.REPORTS_DASHBOARD) {
            ReportsDashboardScreen(
                onSettings = { openSettings() },
                onViewAllReports = { navController.navigate(Routes.REPORTS_HISTORY) },
                onReportSelected = { reportId -> navController.navigate(Routes.detailedReport(reportId)) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.REPORTS_HISTORY) {
            ReportsHistoryScreen(
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onReportSelected = { reportId -> navController.navigate(Routes.detailedReport(reportId)) },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.HISTORY_DASHBOARD) {
            val historyItems = historyViewModel.historyItems.collectAsState()
            HistoryDashboardScreen(
                historyItems = historyItems.value,
                onSnapshotGallery = { navController.navigate(Routes.SNAPSHOT_GALLERY) },
                onSearch = { navController.navigate(Routes.SEARCH_FILTER) },
                onSettings = { openSettings() },
                onHistorySelected = { historyId ->
                    navController.navigate(Routes.historyDetail(historyId))
                },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
	        composable(Routes.SNAPSHOT_GALLERY) {
	            val snapshots = historyViewModel.snapshots.collectAsState()
	            SnapshotGalleryScreen(
	                snapshots = snapshots.value,
	                onBack = { navController.popBackStack() },
	                onSettings = { openSettings() },
	                onNavHome = { navigateHomeRoot() },
	                onNavAlerts = { navigateAlertsRoot() },
	                onNavReports = { navigateReportsRoot() },
	                onNavHistory = { navigateHistoryRoot() },
	                onNavProfile = { navigateProfileRoot() }
	            )
	        }
        composable(Routes.SEARCH_FILTER) {
            val searchQuery = historyViewModel.searchQuery.collectAsState()
            val selectedDate = historyViewModel.selectedDate.collectAsState()
            val selectedThreatType = historyViewModel.selectedThreatType.collectAsState()
            SearchFilterScreen(
                searchQuery = searchQuery.value,
                selectedDate = selectedDate.value,
                selectedThreatType = selectedThreatType.value,
                onSearchQueryChange = historyViewModel::updateSearchQuery,
                onDateSelected = historyViewModel::updateSelectedDate,
                onThreatSelected = historyViewModel::updateThreatType,
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.HISTORY_DETAIL,
            arguments = listOf(
                navArgument("historyId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val historyId = backStackEntry.arguments?.getString("historyId").orEmpty()
            HistoryDetailScreen(
                historyItem = historyViewModel.getHistoryItem(historyId),
                onBack = { navController.popBackStack() },
                onSettings = { openSettings() },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.PROFILE_DASHBOARD) {
            ProfileDashboardScreen(
                onSettings = { openSettings() },
                onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                onChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                onNotificationSettings = { navController.navigate(Routes.NOTIFICATION_SETTINGS) },
                onAppSettings = { openSettings() },
                onAiModelInfo = { navController.navigate(Routes.AI_MODEL_INFO) },
                onAbout = { navController.navigate(Routes.ABOUT_VISION_INTEL) },
                onHelpSupport = { navController.navigate(Routes.HELP_SUPPORT) },
                onPermissions = { navController.navigate(Routes.PERMISSIONS) },
                onLogout = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            AppPrefs.setIsLoggedIn(context, false)
                        }
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.APP_START) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable(Routes.ACKNOWLEDGE_ALERT) {
            AcknowledgeAlertScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onMarkSeen = { navController.navigateUp() },
                onMarkInvestigated = { navController.navigateUp() },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(
            route = Routes.ACKNOWLEDGE_ALERT_WITH_ID,
            arguments = listOf(
                navArgument("alertId") { type = NavType.StringType; defaultValue = "" },
            )
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId")?.takeIf { it.isNotBlank() }
            AcknowledgeAlertScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() },
                onMarkSeen = {
                    scope.launch {
                        if (!alertId.isNullOrBlank()) {
                            container.alertsRepository.acknowledgeAlert(alertId)
                        }
                        navController.navigateUp()
                    }
                },
                onMarkInvestigated = {
                    scope.launch {
                        if (!alertId.isNullOrBlank()) {
                            container.alertsRepository.acknowledgeAlert(alertId)
                        }
                        navController.navigateUp()
                    }
                },
                onNavHome = { navigateHomeRoot() },
                onNavAlerts = { navigateAlertsRoot() },
                onNavReports = { navigateReportsRoot() },
                onNavHistory = { navigateHistoryRoot() },
                onNavProfile = { navigateProfileRoot() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.navigateUp() },
                onModelInfo = { navController.navigate(Routes.AI_MODEL_INFO) },
                onNetworkSettings = { navController.navigate(Routes.NETWORK_SETTINGS) }
            )
        }
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable(Routes.NOTIFICATION_SETTINGS) {
            NotificationSettingsScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable(Routes.THEME_SETTINGS) {
            ThemeSettingsScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable(Routes.LANGUAGE_SETTINGS) {
            LanguageSettingsScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable(Routes.AI_MODEL_INFO) {
            AiModelInfoScreen(
                onBack = { navController.navigateUp() },
                onSettings = { openSettings() }
            )
        }
	        composable(Routes.ABOUT_VISION_INTEL) {
	            AboutVisionIntelScreen(
	                onBack = { navController.navigateUp() },
	                onOpenMlSmokeTest = {
	                    if (BuildConfig.DEBUG) {
	                        navController.navigate(Routes.ML_SMOKE_TEST) { launchSingleTop = true }
	                    }
	                }
	            )
	        }
        composable(Routes.HELP_SUPPORT) {
            HelpSupportScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable(Routes.NETWORK_SETTINGS) {
            NetworkSettingsScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable(Routes.DASHBOARD) {
            ComingSoonScreen(
                title = "Dashboard",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.COMING_SOON,
            arguments = listOf(
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Coming Soon"
            val showBack = navController.previousBackStackEntry != null
            ComingSoonScreen(
                title = title,
                showBack = showBack,
                onBack = { navController.popBackStack() }
            )
        }
    }
}


