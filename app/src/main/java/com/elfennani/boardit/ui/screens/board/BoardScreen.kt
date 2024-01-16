package com.elfennani.boardit.ui.screens.board

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.models.getColor
import com.elfennani.boardit.formatReadable
import com.elfennani.boardit.ui.screens.board.components.BoardAttachments
import com.elfennani.boardit.ui.screens.board.components.BoardScaffold
import com.elfennani.boardit.ui.screens.editor.navigateToEditorScreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BoardScreen(
    state: BoardScreenState,
    onNavigateToEdit: (String) -> Unit,
    onDialogState: (BoardScreenState.DialogState) -> Unit,
    onBack: () -> Unit
) {
    val board = state.board

    BoardScaffold(
        title = board?.title ?: "",
        onBack = onBack,
        onClickEdit = { if (board != null) onNavigateToEdit(board.id) }
    ) {
        if (board != null) {
            if (board.attachments.isNotEmpty()) {
                BoardAttachments(
                    attachments = board.attachments,
                    onDialogState= onDialogState,
                    dialogState = state.dialogState
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FolderCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = board.category?.label  ?: "Nothing?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = board.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = board.date.formatReadable(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            if (board.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    board.tags.forEach {
                        TagCard(it)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (!board.note.isNullOrBlank()) {
                SelectionContainer {
                    Column {
                        Text(
                            text = "NOTE",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = board.note,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 21.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagCard(tag: Tag) {
    Box(
        Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(
                tag
                    .getColor()
                    .copy(alpha = 0.2f)
            )
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Text(
            text = tag.label,
            style = MaterialTheme.typography.bodySmall,
            color = tag.getColor(),
            fontWeight = FontWeight.SemiBold
        )
    }
}

const val BoardScreenPattern = "boards/{id}"
fun NavGraphBuilder.boardScreen(navController: NavController) {
    composable(
        BoardScreenPattern,
        enterTransition = {
            fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popExitTransition = {
            fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
        exitTransition = {
            fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        val boardViewModel: BoardViewModel = hiltViewModel()
        val state by boardViewModel.state.collectAsState()

        BoardScreen(
            state = state,
            onNavigateToEdit = {
                navController.navigateToEditorScreen(it)
            },
            onDialogState = boardViewModel::setDialogState,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}

fun NavController.navigateToBoardScreen(board: Board) {
    Log.d("BOARDID", board.id)
    this.navigate(BoardScreenPattern.replace("{id}", board.id))
}