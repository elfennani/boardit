package com.elfennani.boardit.ui.screens.home

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.plus
import com.elfennani.boardit.ui.components.DashedDivider
import com.elfennani.boardit.ui.components.SelectTagsBottomSheet
import com.elfennani.boardit.ui.screens.board.navigateToBoardScreen
import com.elfennani.boardit.ui.screens.editor.navigateToEditorScreen
import com.elfennani.boardit.ui.screens.home.components.BoardItem
import com.elfennani.boardit.ui.screens.home.components.HomeScaffold
import com.elfennani.boardit.ui.screens.home.components.Sidebar
import com.elfennani.boardit.ui.screens.manage.navigateToManageScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    onNavigateToManage: () -> Unit,
    onNavigateToBoard: (Board) -> Unit,
    onNavigateToEditor: () -> Unit,
    onSelectCategory: (Category?) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCloseModal: () -> Unit,
    onSelectTag: (Tag) -> Unit,
    onSearchValue: (TextFieldValue) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val requestFocusRequest = remember {
        FocusRequester()
    }
    val selectedCategoryLabel = when (state.currentCategory) {
        is SelectedCategory.All -> "All"
        is SelectedCategory.Id -> state.categories?.find { it.id == state.currentCategory.id }?.label
        is SelectedCategory.Loading -> "Loading..."
    }

    if (state.isFilteringTags)
        SelectTagsBottomSheet(
            tags = state.tags,
            selected = state.filteredTags,
            onSelect = onSelectTag,
            onClose = onCloseModal
        )

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
            onClickAdd = onNavigateToEditor,
            onSearchClick = onSearchClick,
            onFilterClick = onFilterClick,
            isSearching = state.isSearching,
            input = {
                LaunchedEffect(key1 = state.isSearching) {
                    requestFocusRequest.requestFocus()
                }

                BasicTextField(
                    value = state.searchValue,
                    onValueChange = onSearchValue,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    modifier = Modifier.focusRequester(requestFocusRequest)
                ) {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(0.dp, 12.dp)
                        ) {
                            if (state.searchValue.text.isEmpty())
                                Text(
                                    text = "Search here...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.25f
                                        )
                                    ),
                                )
                            it()
                        }
                    }
                }
            }
        ) {
            LazyColumn(
                contentPadding = it + PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ) + PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()

            ) {
                items(state.boards, key = { board -> board.id }) { board ->
                    BoardItem(
                        modifier = Modifier.animateItemPlacement(),
                        board = board,
                        onNavigateToBoard
                    )
                }
            }
        }
    }
}


const val HomeScreenPattern = "boards/home"
fun NavGraphBuilder.homeScreen(navController: NavController) {

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
            onSearchClick = homeViewModel::toggleSearchInput,
            onFilterClick = homeViewModel::openFilterTagsModal,
            onCloseModal = homeViewModel::closeFilterTagsModal,
            onSelectTag = homeViewModel::filterTag,
            onSearchValue = homeViewModel::setSearchValue
        )
    }
}

fun NavController.navigateToHomeScreen(popUpToTop: Boolean = false) {
    this.navigate(HomeScreenPattern) {
        if (popUpToTop) {
            popUpTo(graph.id)
        }
    }
}