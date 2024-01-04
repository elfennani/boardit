package com.elfennani.boardit.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.ui.screens.home.components.Sidebar
import com.elfennani.boardit.ui.screens.manage.navigateToManageScreen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.rememberSignOutWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onLogOut: () -> Unit,
    onNavigateToManage: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Sidebar(
                categories = state.categories ?: emptyList(),
                tags = state.tags ?: emptyList(),
                onNavigateToManage = onNavigateToManage
            )
        }) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Best Inspirations",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { /*TODO*/ },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        ) {
            Column(Modifier.padding(it)) {
                Text(text = state.isLoadingCategories.toString())
                LazyColumn {
                    items(state.categories ?: emptyList()) {
                        Text(text = it.label)
                    }
                }
                Button(onClick = { onLogOut() }) {
                    Text(text = "Logout")
                }
            }
        }
    }
}

const val HomeScreenPattern = "boards/home"
fun NavGraphBuilder.homeScreen(supabaseClient: SupabaseClient, navController: NavController) {

    composable(HomeScreenPattern) {
        val signOut = supabaseClient.composeAuth.rememberSignOutWithGoogle()
        val homeViewModel: HomeViewModel = hiltViewModel()
        val state by homeViewModel.homeScreenState.collectAsState()

        HomeScreen(
            state = state,
            onLogOut = {signOut.startFlow()},
            onNavigateToManage = {
                navController.navigateToManageScreen()
            }
        )
    }
}

fun NavController.navigateToHomeScreen(popUpToTop: Boolean = false) {
    this.navigate(HomeScreenPattern){
        if(popUpToTop){
            popUpTo(this@navigateToHomeScreen.currentBackStackEntry?.destination?.route ?: return@navigate){
                inclusive = true
            }
        }
    }
}