package com.elfennani.boardit.ui.screens.editor

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.elfennani.boardit.data.models.AttachmentType
import com.elfennani.boardit.data.models.EditorAttachment
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.ui.screens.editor.components.EditorAttachmentActions
import com.elfennani.boardit.ui.screens.editor.components.EditorScaffold
import com.elfennani.boardit.ui.screens.editor.components.EditorSelector
import com.elfennani.boardit.ui.screens.editor.components.EditorTextFields
import com.elfennani.boardit.ui.screens.editor.components.InsertLinkBottomSheet
import com.elfennani.boardit.ui.screens.editor.components.SelectCategoryBottomSheet
import com.elfennani.boardit.ui.screens.editor.components.SelectTagsBottomSheet
import com.elfennani.boardit.ui.screens.home.navigateToHomeScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
    state: EditorScreenState,
    onEvent: (EditorScreenEvent) -> Unit,
    onMedia: (PickVisualMediaRequest) -> Unit,
    onPdf: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit
) {
    EditorScaffold(
        onBack = onBack,
        onSave = { onEvent(EditorScreenEvent.Save) },
        disabled = state.isSaving,
        onDelete = if (!state.isNew) ({ onEvent(EditorScreenEvent.DeleteBoard) }) else null
    ) {
        LaunchedEffect(key1 = state.isDone) {
            if (state.isDone) onBack()
        }
        LaunchedEffect(key1 = state.isDoneDeleting) {
            if (state.isDoneDeleting) onNavigateToHome()
        }

        when (state.modalState) {
            ModalState.SELECT_CATEGORY -> SelectCategoryBottomSheet(
                categories = state.categories,
                selected = state.selectedCategory,
                onSelect = { onEvent(EditorScreenEvent.SelectCategory(it)) },
                onClose = { onEvent(EditorScreenEvent.CloseModal) }
            )

            ModalState.SELECT_TAGS -> SelectTagsBottomSheet(
                tags = state.tags,
                selected = state.selectedTags,
                onSelect = { onEvent(EditorScreenEvent.SelectTag(it)) },
                onClose = { onEvent(EditorScreenEvent.CloseModal) }
            )

            ModalState.INSERT_LINK -> InsertLinkBottomSheet(
                onClose = { onEvent(EditorScreenEvent.CloseModal) },
                onConfirm = {
                    onEvent(EditorScreenEvent.PickLink(it))
                }
            )

            ModalState.CLOSED -> {}
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 16.dp)
        ) {
            EditorTextFields(
                titleValue = state.titleTextFieldValue,
                bodyValue = state.bodyTextFieldValue,
                onTitleChange = { onEvent(EditorScreenEvent.ModifyTitle(it)) },
                onBodyChange = { onEvent(EditorScreenEvent.ModifyBody(it)) }
            )

            val arrangement = Arrangement.spacedBy(16.dp)
            if (state.attachments.isNotEmpty())
                FlowRow(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = arrangement,
                    horizontalArrangement = arrangement
                ) {
                    state.attachments.forEach {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(8.dp)
                                )
                                .size(64.dp)
                                .graphicsLayer { clip = false },
                            contentAlignment = Alignment.Center
                        ) {
                            val attachmentType = when (it) {
                                is EditorAttachment.Remote -> it.attachment.type
                                is EditorAttachment.Local -> it.type
                            }
                            when (attachmentType) {
                                is AttachmentType.Image -> AsyncImage(
                                    model = when (it) {
                                        is EditorAttachment.Remote -> it.attachment.url
                                        is EditorAttachment.Local -> it.uri
                                    },
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )

                                is AttachmentType.Pdf -> Icon(
                                    imageVector = Icons.Rounded.PictureAsPdf,
                                    contentDescription = null,
                                )

                                is AttachmentType.Link -> Icon(
                                    imageVector = Icons.Rounded.Link,
                                    contentDescription = null,
                                )

                                is AttachmentType.Unsupported -> {}
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(6.dp, (-6).dp)
                                    .clip(CircleShape)
                                    .clickable(role = Role.Button) {
                                        onEvent(
                                            EditorScreenEvent.DeleteAttachment(
                                                it
                                            )
                                        )
                                    }
                                    .background(MaterialTheme.colorScheme.error)
                                    .size(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

            EditorAttachmentActions(
                onLink = { onEvent(EditorScreenEvent.OpenModal(ModalState.INSERT_LINK)) },
                onImage = { onMedia(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onPdf = { onPdf() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        EditorSelector(
            onClick = { onEvent(EditorScreenEvent.OpenModal(ModalState.SELECT_CATEGORY)) },
            icon = Icons.Rounded.FolderCopy
        ) {
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

        EditorSelector(
            onClick = { onEvent(EditorScreenEvent.OpenModal(ModalState.SELECT_TAGS)) },
            icon = Icons.Rounded.Tag
        ) {
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
        val context = LocalContext.current
        val limit = ActivityResultContracts.PickMultipleVisualMedia(5)
        val pickImages = rememberLauncherForActivityResult(limit) {
            viewModel.event(
                EditorScreenEvent.PickImages(it, context)
            )
        }

        val pickPdf =
            rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
                viewModel.event(EditorScreenEvent.PickPdfs(it, context))
            }

        EditorScreen(
            state,
            onEvent = viewModel::event,
            onMedia = pickImages::launch,
            onPdf = { pickPdf.launch(arrayOf("application/pdf")) },
            onBack = navController::popBackStack,
            onNavigateToHome = {
                navController.navigateToHomeScreen(true)
            }
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