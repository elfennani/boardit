package com.elfennani.boardit

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection
import coil.request.ImageRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues {
    return PaddingValues(
        start = this.calculateStartPadding(LayoutDirection.Ltr) + paddingValues.calculateStartPadding(
            LayoutDirection.Ltr
        ),
        top = this.calculateTopPadding() + paddingValues.calculateTopPadding(),
        end = this.calculateEndPadding(LayoutDirection.Ltr) + paddingValues.calculateEndPadding(
            LayoutDirection.Ltr
        ),
        bottom = this.calculateBottomPadding() + paddingValues.calculateBottomPadding()
    )
}


fun LocalDateTime.formatReadable(): String = this.format(
    DateTimeFormatter.ofPattern(
        "MMM d, yyyy 'at' hh:mm a"
    )
)

fun Context.getActivity(): Activity = when (this) {
    is Activity -> this
    else -> this.getActivity()
}