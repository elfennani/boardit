package com.elfennani.boardit.data.local

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun toUri(value: String): Uri = Uri.parse(value)

    @TypeConverter
    fun fromUri(uri: Uri): String = uri.toString()
}