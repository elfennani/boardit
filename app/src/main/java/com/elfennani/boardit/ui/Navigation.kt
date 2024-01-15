package com.elfennani.boardit.ui

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.elfennani.boardit.getActivity
import com.elfennani.boardit.ui.graphs.BoardsGraphPattern
import com.elfennani.boardit.ui.graphs.boardsGraph
import com.elfennani.boardit.ui.screens.editor.navigateToEditorScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context.getActivity()

    NavHost(
        navController = navController,
        startDestination = BoardsGraphPattern,
        enterTransition = {
            fadeIn()
        },
        exitTransition = {
            fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
    ) {
        boardsGraph(navController)
    }
}