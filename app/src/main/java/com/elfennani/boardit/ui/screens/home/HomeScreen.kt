package com.elfennani.boardit.ui.screens.home

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.plus
import com.elfennani.boardit.ui.screens.board.navigateToBoardScreen
import com.elfennani.boardit.ui.screens.editor.navigateToEditorScreen
import com.elfennani.boardit.ui.screens.home.components.BoardItem
import com.elfennani.boardit.ui.screens.home.components.HomeScaffold
import com.elfennani.boardit.ui.screens.home.components.Sidebar
import com.elfennani.boardit.ui.screens.manage.navigateToManageScreen
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onNavigateToManage: () -> Unit,
    onNavigateToBoard: (Board) -> Unit,
    onNavigateToEditor: () ->Unit,
    onSelectCategory: (Category?) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val selectedCategoryLabel = when (state.currentCategory) {
        is SelectedCategory.All -> "All"
        is SelectedCategory.Id -> state.categories?.find { it.id == state.currentCategory.id }?.label
        is SelectedCategory.Loading -> "Loading..."
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Sidebar(
                activeCategory = state.currentCategory,
                categories = state.categories ?: emptyList(),
                tags = state.tags ?: emptyList(),
                onNavigateToManage = onNavigateToManage,
                onSelectCategory = {
                    onSelectCategory(it)
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        }) {
        HomeScaffold(
            title = selectedCategoryLabel ?: "",
            onDrawerOpen = { coroutineScope.launch { drawerState.open() } },
            onClickAdd = onNavigateToEditor
        ) {
            LazyColumn(
                contentPadding = it + PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.boards ?: emptyList()) { board ->
                    BoardItem(board = board, onNavigateToBoard)
                }
            }
        }
    }
}


const val HomeScreenPattern = "boards/home"
fun NavGraphBuilder.homeScreen(supabaseClient: SupabaseClient, navController: NavController) {

    composable(
        HomeScreenPattern,
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        val homeViewModel: HomeViewModel = hiltViewModel()
        val state by homeViewModel.homeScreenState.collectAsState()

        HomeScreen(
            state = state,
            onNavigateToManage = {
                navController.navigateToManageScreen()
            },
            onNavigateToBoard = navController::navigateToBoardScreen,
            onNavigateToEditor = navController::navigateToEditorScreen,
            onSelectCategory = homeViewModel::selectCategory,
        )
    }
}

fun NavController.navigateToHomeScreen(popUpToTop: Boolean = false) {
    this.navigate(HomeScreenPattern) {
        if (popUpToTop) {
            popUpTo(
                this@navigateToHomeScreen.currentBackStackEntry?.destination?.route
                    ?: return@navigate
            ) {
                inclusive = true
            }
        }
    }
}