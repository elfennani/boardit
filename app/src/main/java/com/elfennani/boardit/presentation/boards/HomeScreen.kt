package com.elfennani.boardit.presentation.boards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.presentation.boards.components.Sidebar
import com.elfennani.boardit.presentation.components.DashedDivider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.rememberSignOutWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Sidebar(
                categories = state.categories ?: emptyList()
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
            }
        }
    }
}

const val HomeScreenPattern = "boards/home"
fun NavGraphBuilder.homeScreen(supabaseClient: SupabaseClient) {

    composable(HomeScreenPattern) {
        val signOut = supabaseClient.composeAuth.rememberSignOutWithGoogle()
        val homeViewModel: HomeViewModel = hiltViewModel()

        HomeScreen(
            state = homeViewModel.homeScreenState.value
        )
    }
}

fun NavController.navigateToHomeScreen() {
    this.navigate(HomeScreenPattern)
}