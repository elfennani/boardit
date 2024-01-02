package com.elfennani.boardit.presentation.boards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elfennani.boardit.ui.theme.BoarditTheme

enum class IndentationType {
    None, Middle, End
}

@Composable
fun SidebarItem(
    label: String,
    color: Color? = null,
    leading: (@Composable () -> Unit)? = null,
    icon: ImageVector? = null,
    indentationType: IndentationType = IndentationType.None,
    active: Boolean = false,
    onClick: () -> Unit = {},
) {
    val contentColor = MaterialTheme.colorScheme.onSurface

    Row(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .background(Color.White)
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (indentationType) {
            IndentationType.Middle -> {
                Spacer(modifier = Modifier.width(14.dp))
                IndentationMiddle()
            }

            IndentationType.End -> {
                Spacer(modifier = Modifier.width(14.dp))
                IndentationEnd()
            }

            else -> {}
        }

        Row(
            Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            }

            if (color != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(8.dp)
                        .background(color)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                color = if(active) MaterialTheme.colorScheme.secondary else contentColor,
                fontWeight = if(active) FontWeight.Medium else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            leading?.invoke()
        }
    }
}

@Composable
fun IndentationMiddle() {
    Canvas(
        modifier = Modifier
            .width(15.dp)
            .fillMaxHeight(),
        onDraw = {
            drawLine(
                color = Color(0xFFD4D4D8),
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = 1f * density
            )
            drawLine(
                color = Color(0xFFD4D4D8),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 1f * density,
                cap = StrokeCap.Round
            )
        })
}

@Composable
fun IndentationEnd() {
    Canvas(modifier = Modifier
        .width(15.dp)
        .fillMaxHeight(), onDraw = {

        drawPath(
            path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, size.height/2 - 10 * density)
                arcTo(
                    rect = Rect(
                        left = 0f,
                        right = 10f*density,
                        top = (size.height/2) - 10 * density,
                        bottom = size.height/2
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -90f,
                    forceMoveTo = false
                )
                lineTo(size.width, size.height/2)
            },
            color = Color(0xFFD4D4D8),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1f * density,
            )
        )
    })
}

@Composable
fun Indicator(amount: Int) {
    Box(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF1F5F9))
            .widthIn(18.dp)
            .height(18.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = amount.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B)
        )
    }
}




@Preview
@Composable
fun CategoryPreview() {
    BoarditTheme {
        Column(Modifier.width(250.dp)) {

            SidebarItem(
                label = "Categories",
                icon = Icons.Rounded.FolderCopy,
                leading = {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun CategoryItemMiddlePreview() {
    BoarditTheme {
        Column(Modifier.width(250.dp)) {

            SidebarItem(
                label = "Inspirations",
                leading = { Indicator(amount = 24) },
                indentationType = IndentationType.Middle
            )
        }
    }
}

@Preview
@Composable
fun CategoryItemEndPreview() {
    BoarditTheme {
        Column(Modifier.width(250.dp)) {
            SidebarItem(
                label = "Web Design",
                leading = { Indicator(amount = 12) },
                indentationType = IndentationType.End
            )
        }
    }
}

@Preview
@Composable
fun CategoryItemMiddleEndPreview() {
    BoarditTheme {
        Column(Modifier.width(250.dp)) {
            SidebarItem(
                label = "Categories",
                icon = Icons.Rounded.FolderCopy,
                leading = {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            SidebarItem(
                label = "Inspirations",
                leading = { Indicator(amount = 24) },
                indentationType = IndentationType.Middle
            )
            SidebarItem(
                label = "Web Design",
                leading = { Indicator(amount = 12) },
                indentationType = IndentationType.End
            )
        }
    }
}

@Preview
@Composable
fun TagPreview() {
    BoarditTheme {
        Column(Modifier.width(250.dp)) {

            SidebarItem(
                label = "Inspirations",
                leading = { Indicator(amount = 24) },
                indentationType = IndentationType.Middle,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}