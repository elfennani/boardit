package com.elfennani.boardit.ui.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.elfennani.boardit.ui.screens.board.boardScreen
import com.elfennani.boardit.ui.screens.home.HomeScreenPattern
import com.elfennani.boardit.ui.screens.home.homeScreen
import com.elfennani.boardit.ui.screens.manage.manageScreen
import io.github.jan.supabase.SupabaseClient

const val BoardsGraphPattern = "boards"
fun NavGraphBuilder.boardsGraph(navController: NavController, supabaseClient: SupabaseClient){
    navigation(
        startDestination = HomeScreenPattern,
        route = BoardsGraphPattern
    ){
        homeScreen(
            supabaseClient,
            navController
        )

        manageScreen(navController)
        boardScreen(navController)
    }
}