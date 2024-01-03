package com.elfennani.boardit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.elfennani.boardit.ui.graphs.AuthGraphPattern
import com.elfennani.boardit.ui.graphs.authGraph
import com.elfennani.boardit.ui.screens.onboarding.navigateToOnBoarding
import com.elfennani.boardit.ui.graphs.BoardsGraphPattern
import com.elfennani.boardit.ui.graphs.boardsGraph
import com.elfennani.boardit.ui.screens.home.navigateToHomeScreen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserInfo

@Composable
fun Navigation(
    supabaseClient: SupabaseClient,
    userInfo: UserInfo? = null
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(key1 = null) {
        supabaseClient.auth.sessionStatus.collect {
            when (it) {
                is SessionStatus.Authenticated -> {
                    if (navController.currentBackStackEntry?.destination?.route?.startsWith(
                            AuthGraphPattern
                        ) == true
                    ){
                        navController.navigateToHomeScreen(popUpToTop = true)
                    }
                }

                SessionStatus.NotAuthenticated -> {
                    if (navController.currentBackStackEntry?.destination?.route?.startsWith(
                            BoardsGraphPattern
                        ) == true
                    ){
                        navController.navigateToOnBoarding(popUpToTop = true)
                    }
                }
                else -> {}
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (userInfo == null) AuthGraphPattern else BoardsGraphPattern
    ) {
        authGraph(navController, supabaseClient, context)
        boardsGraph(navController, supabaseClient)
    }
}