package com.elfennani.boardit.presentation.boards.components

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
import com.elfennani.boardit.domain.model.Category
import com.elfennani.boardit.presentation.boards.IndentationType
import com.elfennani.boardit.presentation.boards.Indicator
import com.elfennani.boardit.presentation.boards.SidebarItem
import com.elfennani.boardit.presentation.components.DashedDivider

@Composable
fun Sidebar(
    categories: List<Category>
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
            SidebarItem(
                label = "All",
                indentationType = if(categories.isEmpty()) IndentationType.End else IndentationType.Middle,
                leading = {
                    val total:Int = categories.sumOf { it.noteCount }
                    Indicator(amount = total)
                })
            categories.forEachIndexed { index, category ->
                SidebarItem(
                    label = category.label,
                    indentationType = if(index == categories.size - 1) IndentationType.End else IndentationType.Middle,
                    leading = {
                        Indicator(amount = category.noteCount)
                    })
            }
            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))
            SidebarItem(label = "Tags", icon = Icons.Rounded.Tag, leading = {
                Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
            })
            SidebarItem(
                label = "Textures",
                color = Color(0xFFef4444),
                indentationType = IndentationType.Middle,
                leading = {
                    Indicator(amount = 12)
                })
            SidebarItem(
                label = "Inspirations",
                color = Color(0xFF14b8a6),
                indentationType = IndentationType.Middle,
                leading = {
                    Indicator(amount = 4)
                })
            SidebarItem(
                label = "Themes",
                color = Color(0xFF8B5CF6),
                indentationType = IndentationType.Middle,
                leading = {
                    Indicator(amount = 19)
                })
            SidebarItem(label = "Colors",
                color = Color(0xFF22C55E),
                indentationType = IndentationType.End,
                leading = {
                    Indicator(amount = 19)
                })
            Spacer(modifier = Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { /*TODO*/ }, modifier = Modifier.widthIn(min = 165.dp)) {
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