package com.elfennani.boardit.ui.screens.board

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.ui.screens.board.components.BoardScaffold

@Composable
fun BoardScreen(
    board: Board?,
    onBack: () -> Unit
) {
    BoardScaffold(
        title = board?.title ?: "",
        onBack = onBack,
        onClickEdit = { /*TODO*/ }
    ) {
        Text(text = board?.category?.label ?: "Hello World!!!")
    }
}

const val BoardScreenPattern = "boards/{id}"
fun NavGraphBuilder.boardScreen(navController: NavController) {
    composable(
        BoardScreenPattern,
        enterTransition = {
            fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
    ) {
        val boardViewModel: BoardViewModel = hiltViewModel()
        val board by boardViewModel.boards.collectAsState()

        BoardScreen(
            board = board,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}

fun NavController.navigateToBoardScreen(board: Board) {
    this.navigate(BoardScreenPattern.replace("{id}", board.id.toString()))
}

fun NavController.navigateToBoardScreen(boardId: Int) {
    this.navigate(BoardScreenPattern.replace("{id}", boardId.toString()))
}