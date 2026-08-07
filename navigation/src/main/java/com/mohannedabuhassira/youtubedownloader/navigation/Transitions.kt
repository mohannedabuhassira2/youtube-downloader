package com.mohannedabuhassira.youtubedownloader.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween

private const val DEFAULT_TRANSITION_DURATION = 300

fun AnimatedContentTransitionScope<*>.slideInToLeft(
    durationMillis: Int = DEFAULT_TRANSITION_DURATION
): EnterTransition = slideIntoContainer(
    towards = SlideDirection.Left,
    animationSpec = tween(durationMillis)
)

fun AnimatedContentTransitionScope<*>.slideOutToLeft(
    durationMillis: Int = DEFAULT_TRANSITION_DURATION
): ExitTransition = slideOutOfContainer(
    towards = SlideDirection.Left,
    animationSpec = tween(durationMillis)
)

fun AnimatedContentTransitionScope<*>.slideInToRight(
    durationMillis: Int = DEFAULT_TRANSITION_DURATION
): EnterTransition = slideIntoContainer(
    towards = SlideDirection.Right,
    animationSpec = tween(durationMillis)
)

fun AnimatedContentTransitionScope<*>.slideOutToRight(
    durationMillis: Int = DEFAULT_TRANSITION_DURATION
): ExitTransition = slideOutOfContainer(
    towards = SlideDirection.Right,
    animationSpec = tween(durationMillis)
)
