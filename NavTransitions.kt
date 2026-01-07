package com.techmaina.visionintel.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

object NavTransitions {
    private const val MOVE_MS = 300
    private const val FADE_OUT_MS = 140
    private const val FADE_IN_MS = 200

    private val tabOrder = listOf(
        Routes.HOME,
        Routes.ALERTS_LIST,
        Routes.REPORTS_DASHBOARD,
        Routes.HISTORY_DASHBOARD,
        Routes.PROFILE_DASHBOARD
    ).map { it.substringBefore("?") }

    private val bottomRootBases = tabOrder.toSet()

    private fun baseRoute(entry: NavBackStackEntry): String {
        return entry.destination.route?.substringBefore("?").orEmpty()
    }

    private fun AnimatedContentTransitionScope<NavBackStackEntry>.isBottomTabSwitch(): Boolean {
        val from = baseRoute(initialState)
        val to = baseRoute(targetState)
        return from in bottomRootBases && to in bottomRootBases && from != to
    }

    private fun AnimatedContentTransitionScope<NavBackStackEntry>.isNavigatingToBottomRoot(): Boolean {
        return baseRoute(targetState) in bottomRootBases
    }

    private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabDirection(): Int {
        val fromIdx = tabOrder.indexOf(baseRoute(initialState)).takeIf { it >= 0 } ?: 0
        val toIdx = tabOrder.indexOf(baseRoute(targetState)).takeIf { it >= 0 } ?: 0
        return (toIdx - fromIdx).coerceIn(-1, 1)
    }

    private fun AnimatedContentTransitionScope<NavBackStackEntry>.isOpeningSettings(): Boolean {
        return baseRoute(targetState) == Routes.SETTINGS
    }

    private fun AnimatedContentTransitionScope<NavBackStackEntry>.isClosingSettings(): Boolean {
        return baseRoute(initialState) == Routes.SETTINGS
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.enter(): EnterTransition {
        return when {
            isBottomTabSwitch() || isNavigatingToBottomRoot() -> {
                val dir = tabDirection()
                val offset = if (dir == 0) 0 else dir
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> (fullWidth / 24) * offset },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(FADE_IN_MS))
            }
            isOpeningSettings() ->
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight / 12 },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(FADE_IN_MS))
            else ->
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth / 12 },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(FADE_IN_MS))
        }
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.exit(): ExitTransition {
        return when {
            isBottomTabSwitch() || isNavigatingToBottomRoot() -> {
                val dir = tabDirection()
                val offset = if (dir == 0) 0 else -dir
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> (fullWidth / 28) * offset },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(FADE_OUT_MS))
            }
            isOpeningSettings() ->
                fadeOut(animationSpec = tween(FADE_OUT_MS))
            else ->
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -(fullWidth / 14) },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(FADE_OUT_MS))
        }
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnter(): EnterTransition {
        return when {
            isBottomTabSwitch() || isNavigatingToBottomRoot() -> {
                val dir = tabDirection()
                val offset = if (dir == 0) 0 else -dir
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> (fullWidth / 24) * offset },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(FADE_IN_MS))
            }
            isClosingSettings() ->
                fadeIn(animationSpec = tween(FADE_IN_MS))
            else ->
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -(fullWidth / 12) },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(FADE_IN_MS))
        }
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.popExit(): ExitTransition {
        return when {
            isBottomTabSwitch() || isNavigatingToBottomRoot() -> {
                val dir = tabDirection()
                val offset = if (dir == 0) 0 else dir
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> (fullWidth / 28) * offset },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(FADE_OUT_MS))
            }
            isClosingSettings() ->
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight / 12 },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(FADE_OUT_MS))
            else ->
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> (fullWidth / 14) },
                    animationSpec = tween(MOVE_MS, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(FADE_OUT_MS))
        }
    }
}
