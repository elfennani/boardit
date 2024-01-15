package com.elfennani.boardit.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.elfennani.boardit.ui.graphs.BoardsGraphPattern
import com.elfennani.boardit.ui.graphs.boardsGraph

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = BoardsGraphPattern
    ) {
        boardsGraph(navController)
    }
}