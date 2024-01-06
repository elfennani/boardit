package com.elfennani.boardit

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection
import coil.request.ImageRequest

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

fun ImageRequest.Builder.storage(url: String, token: String) =
    this.data(url)
        .addHeader("Authorization", "Bearer $token")
        .addHeader("apikey", this.build().context.getString(R.string.SUPABASE_ANON_KEY))