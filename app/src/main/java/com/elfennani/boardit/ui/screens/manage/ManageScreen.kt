package com.elfennani.boardit.ui.screens.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.ui.components.IndentationType
import com.elfennani.boardit.ui.components.SidebarItem
import com.elfennani.boardit.ui.components.DashedDivider
import com.elfennani.boardit.ui.screens.manage.components.AddButton
import com.elfennani.boardit.ui.screens.manage.components.AddCategoryBottomSheet
import com.elfennani.boardit.ui.screens.manage.components.AddTagBottomSheet
import com.elfennani.boardit.ui.screens.manage.components.EditCategoryBottomSheet
import com.elfennani.boardit.ui.screens.manage.components.EditTagBottomSheet

sealed class ModalState() {
    data object Closed : ModalState()
    data object AddCategory : ModalState()
    data object AddTag : ModalState()
    data class EditCategory(val category: Category) : ModalState()
    data class EditTag(val tag: Tag) : ModalState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    state: ManageScreenState,
    onAddCategory: (label: String) -> Unit,
    onEditCategory: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onAddTag: (String, Color) -> Unit,
    onEditTag: (Tag) -> Unit,
    onDeleteTag: (Tag) -> Unit,
    onBack: () -> Unit,
) {
    var modalState: ModalState by remember {
        mutableStateOf(ModalState.Closed)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        text = "Manage",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        }
    ) { innerPadding ->

        Column(
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SidebarItem(
                label = "Categories",
                onClick = null,
                icon = Icons.Rounded.FolderCopy,
                leading = { AddButton { modalState = ModalState.AddCategory } })

            state.categories?.forEachIndexed { index, category ->
                SidebarItem(
                    label = category.label,
                    indentationType = if (index == state.categories.size - 1) IndentationType.End else IndentationType.Middle,
                    onClick = {
                        modalState = ModalState.EditCategory(category)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))

            SidebarItem(
                label = "Tags",
                onClick = null,
                icon = Icons.Rounded.Tag,
                leading = { AddButton { modalState = ModalState.AddTag } })
            state.tags?.forEachIndexed { index, tag ->
                SidebarItem(
                    label = tag.label,
                    color = tag.color,
                    indentationType = if (index == state.tags.size - 1) IndentationType.End else IndentationType.Middle,
                    onClick = { modalState = ModalState.EditTag(tag) }
                )
            }

            when (modalState) {
                is ModalState.AddCategory -> AddCategoryBottomSheet(
                    onClose = {
                        modalState = ModalState.Closed
                    },
                    onConfirm = onAddCategory
                )

                is ModalState.EditCategory -> {
                    val category = (modalState as ModalState.EditCategory).category
                    EditCategoryBottomSheet(
                        category,
                        onClose = {
                            modalState = ModalState.Closed
                        },
                        onConfirm = onEditCategory,
                        onDelete = onDeleteCategory
                    )
                }

                is ModalState.AddTag -> AddTagBottomSheet(onClose = {
                    modalState = ModalState.Closed
                }, onConfirm = onAddTag)

                is ModalState.EditTag -> {
                    val tag = (modalState as ModalState.EditTag).tag
                    EditTagBottomSheet(
                        tag,
                        onClose = {
                            modalState = ModalState.Closed
                        },
                        onConfirm = onEditTag,
                        onDelete = onDeleteTag
                    )
                }

                else -> {}
            }
        }
    }
}

const val ManageScreenPattern = "boards/manage";
fun NavGraphBuilder.manageScreen(navController: NavController) {
    composable(ManageScreenPattern) {
        val viewModel: ManageViewModel = hiltViewModel()
        val state by viewModel.manageScreenState.collectAsState()

        ManageScreen(
            state,
            onAddCategory = viewModel::addCategory,
            onEditCategory = viewModel::editCategory,
            onDeleteCategory = viewModel::deleteCategory,
            onAddTag = viewModel::addTag,
            onEditTag = viewModel::editTag,
            onDeleteTag = viewModel::deleteTag,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}

fun NavController.navigateToManageScreen() {
    this.navigate(ManageScreenPattern)
}