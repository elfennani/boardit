package com.elfennani.boardit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.elfennani.boardit.presentation.auth.AuthGraphPattern
import com.elfennani.boardit.presentation.auth.authGraph
import com.elfennani.boardit.presentation.boards.BoardsGraphPattern
import com.elfennani.boardit.presentation.boards.HomeScreenPattern
import com.elfennani.boardit.presentation.boards.boardsGraph
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.user.UserInfo

@Composable
fun Navigation(
    supabaseClient: SupabaseClient,
    userInfo: UserInfo? = null
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = if (userInfo == null) AuthGraphPattern else BoardsGraphPattern
    ) {
        authGraph(navController, supabaseClient, context)
        boardsGraph(navController, supabaseClient)
    }
}