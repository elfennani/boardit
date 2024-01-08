package com.elfennani.boardit.ui.screens.editor

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.ui.screens.editor.components.EditorAttachementActions
import com.elfennani.boardit.ui.screens.editor.components.EditorScaffold
import com.elfennani.boardit.ui.screens.editor.components.EditorSelector
import com.elfennani.boardit.ui.screens.editor.components.EditorTextFields
import com.elfennani.boardit.ui.screens.editor.components.SelectCategoryBottomSheet
import com.elfennani.boardit.ui.screens.editor.components.SelectTagsBottomSheet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
    state: EditorScreenState,
    onSave: () -> Unit,
    onTitleChange: (TextFieldValue) -> Unit,
    onBodyChange: (TextFieldValue) -> Unit,
    onOpenCategoryModal: () -> Unit,
    onOpenTagModal: () -> Unit,
    onSelectCategory: (Category) -> Unit,
    onSelectTag: (Tag) -> Unit,
    onCloseModal: () -> Unit,
    onBack: () -> Unit
) {
    EditorScaffold(
        onBack = onBack,
        onSave = onSave,
        disabled = state.isSaving,
        onDelete = {}
    ) {
        LaunchedEffect(key1 = state.isDone){
            if(state.isDone) onBack()
        }

        when (state.modalState) {
            ModalState.SELECT_CATEGORY -> SelectCategoryBottomSheet(
                categories = state.categories,
                selected = state.selectedCategory,
                onSelect = onSelectCategory,
                onClose = onCloseModal
            )

            ModalState.SELECT_TAGS -> SelectTagsBottomSheet(
                tags = state.tags,
                selected = state.selectedTags,
                onSelect = onSelectTag,
                onClose = onCloseModal
            )

            else -> {}
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 16.dp)
        ) {
            EditorTextFields(
                titleValue = state.titleTextFieldValue,
                bodyValue = state.bodyTextFieldValue,
                onTitleChange = onTitleChange,
                onBodyChange = onBodyChange
            )

            EditorAttachementActions(
                onImage = { /*TODO*/ },
                onLink = { /*TODO*/ },
                onPdf = { /*TODO*/ }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        EditorSelector(onClick = onOpenCategoryModal, icon = Icons.Rounded.FolderCopy) {
            AnimatedContent(state.selectedCategory, label = "category") { category ->
                Text(
                    text = category?.label ?: "Select Category",
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = if (category == null) 0.5f else 1f
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = if (category == null) FontStyle.Italic else FontStyle.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        EditorSelector(onClick = onOpenTagModal, icon = Icons.Rounded.Tag) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (state.selectedTags.isEmpty())
                    Text(
                        text = "Select Tags",
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )

                state.selectedTags.forEach { TagCard(it) }
            }
        }
    }
}

@Composable
private fun TagCard(tag: Tag) {
    Box(
        Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(tag.color.copy(alpha = 0.2f))
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Text(
            text = tag.label,
            style = MaterialTheme.typography.bodySmall,
            color = tag.color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

const val EditorScreenPattern = "boards/edit"
fun NavGraphBuilder.editorScreen(navController: NavController) {
    composable(
        "$EditorScreenPattern?id={id}",
        arguments = listOf(navArgument("id") { nullable = true }),
        enterTransition = {
            fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
        },
    ) {
        val viewModel: EditorViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()

        EditorScreen(
            state,
            onSave = { viewModel.event(EditorScreenEvent.Save) },
            onTitleChange = { viewModel.event(EditorScreenEvent.ModifyTitle(it)) },
            onBodyChange = { viewModel.event(EditorScreenEvent.ModifyBody(it)) },
            onOpenCategoryModal = { viewModel.event((EditorScreenEvent.OpenModal(ModalState.SELECT_CATEGORY))) },
            onOpenTagModal = { viewModel.event((EditorScreenEvent.OpenModal(ModalState.SELECT_TAGS))) },
            onSelectCategory = { viewModel.event(EditorScreenEvent.SelectCategory(it)) },
            onSelectTag = { viewModel.event(EditorScreenEvent.SelectTag(it)) },
            onCloseModal = { viewModel.event(EditorScreenEvent.CloseModal) },
            onBack = navController::popBackStack
        )
    }
}

fun NavController.navigateToEditorScreen(id: Int? = null) {
    Log.d("EDITORSCREEN", id.toString())
    if (id != null) {
        this.navigate("$EditorScreenPattern?id=$id")
        return;
    }

    this.navigate(EditorScreenPattern)
}