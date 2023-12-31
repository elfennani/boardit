package com.elfennani.boardit.presentation.boards

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import io.github.jan.supabase.SupabaseClient

const val BoardsGraphPattern = "boards"
fun NavGraphBuilder.boardsGraph(navController: NavController, supabaseClient: SupabaseClient){
    navigation(
        startDestination = HomeScreenPattern,
        route = BoardsGraphPattern
    ){
        homeScreen(
            supabaseClient
        )
    }
}