package com.elfennani.boardit.ui.screens.manage

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.ui.components.IndentationType
import com.elfennani.boardit.ui.components.SidebarItem
import com.elfennani.boardit.ui.components.DashedDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    state: ManageScreenState,
    onBack: () -> Unit
) {
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
                leading = { AddButton {} })

            state.categories?.forEachIndexed { index, category ->
                SidebarItem(
                    label = category.label,
                    indentationType = if (index == state.categories.size - 1) IndentationType.End else IndentationType.Middle,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))

            SidebarItem(
                label = "Tags",
                onClick = null,
                icon = Icons.Rounded.Tag,
                leading = { AddButton {} })
            state.tagDtos?.forEachIndexed { index, tag ->
                SidebarItem(
                    label = tag.label,
                    color = Color.Gray,
                    indentationType = if (index == state.tagDtos.size - 1) IndentationType.End else IndentationType.Middle
                )
            }
        }
    }
}

@Composable
fun AddButton(onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(400.dp))
            .clickable { onAdd() }
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(400.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            Modifier.size(14.dp)
        )
        Text(text = "Add", style = MaterialTheme.typography.labelSmall)
    }
}

const val ManageScreenPattern = "boards/manage";
fun NavGraphBuilder.manageScreen(navController: NavController) {
    composable(ManageScreenPattern) {
        val viewModel: ManageViewModel = hiltViewModel()
        val state by viewModel.manageScreenState

        ManageScreen(
            state,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}

fun NavController.navigateToManageScreen() {
    this.navigate(ManageScreenPattern)
}