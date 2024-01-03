package com.elfennani.boardit.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.remote.models.TagDto
import com.elfennani.boardit.ui.components.DashedDivider
import com.elfennani.boardit.ui.components.IndentationType
import com.elfennani.boardit.ui.components.SidebarItem

@Composable
fun Sidebar(
    categories: List<Category>,
    tagDtos: List<TagDto>,
    onNavigateToManage: () -> Unit
) {
    ModalDrawerSheet(
        Modifier.width(266.dp),
        drawerContainerColor = MaterialTheme.colorScheme.background
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { /*TODO*/ }, modifier = Modifier.offset(x = 10.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))
            SidebarItem(label = "Categories", icon = Icons.Rounded.FolderCopy, leading = {
                Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
            })
            categories.forEachIndexed { index, category ->
                SidebarItem(
                    label = category.label,
                    indentationType = if(index == categories.size - 1) IndentationType.End else IndentationType.Middle,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))
            SidebarItem(label = "Tags", icon = Icons.Rounded.Tag, leading = {
                Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
            })
            tagDtos.forEachIndexed { index, tag ->
                SidebarItem(
                    label = tag.label,
                    color = Color.Gray,
                    indentationType = if(index == tagDtos.size - 1) IndentationType.End else IndentationType.Middle
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = onNavigateToManage, modifier = Modifier.widthIn(min = 165.dp)) {
                    Text(
                        text = "Manage",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

}