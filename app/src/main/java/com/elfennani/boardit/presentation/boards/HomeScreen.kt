package com.elfennani.boardit.presentation.boards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.rememberSignOutWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

@Composable
fun HomeScreen(
    onLogout: () -> Unit
){
    Scaffold {
        Column(Modifier.padding(it)) {
            Text(text = "Hello World")
            Button(onClick = onLogout) {
                Text(text = "Logout")
            }
        }
    }
}

const val HomeScreenPattern = "boards/home"
fun NavGraphBuilder.homeScreen(supabaseClient: SupabaseClient) {

    composable(HomeScreenPattern){
        val signOut = supabaseClient.composeAuth.rememberSignOutWithGoogle()
        val homeViewModel : HomeViewModel = hiltViewModel()

        HomeScreen(
            onLogout = {
                signOut.startFlow()
            }
        )
    }
}

fun NavController.navigateToHomeScreen(){
    this.navigate(HomeScreenPattern)
}