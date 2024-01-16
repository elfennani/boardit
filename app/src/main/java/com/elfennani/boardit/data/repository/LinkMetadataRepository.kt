package com.elfennani.boardit.data.repository

import android.util.Log
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType
import com.elfennani.boardit.data.models.LinkMetadata
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import javax.inject.Inject

interface LinkMetadataRepository {
    val savedMetadata: Flow<List<LinkMetadata>>
    suspend fun load(attachment: Attachment)
}

class LinkMetadataRepositoryImpl @Inject constructor(
    private val mmkv: MMKV
) : LinkMetadataRepository {

    private val _metadata = MutableStateFlow(getMetadata())

    override val savedMetadata: Flow<List<LinkMetadata>>
        get() = _metadata

    private fun getMetadata() = mmkv
        .allKeys()
        ?.filter { it.startsWith("link:") }
        ?.map { Json.decodeFromString<LinkMetadata>(mmkv.decodeString(it)!!) }
        ?: emptyList()

    private fun synchronize() {
        _metadata.value = getMetadata()
    }

    override suspend fun load(attachment: Attachment) {
        Log.d("LOAD_METADATA", attachment.toString())
        if (attachment.type !is AttachmentType.Link || _metadata.value.any { it.attachmentId == attachment.id })
            return;

        try {
            var metadata = LinkMetadata(attachment.id, null, null, attachment.url)
            val url =
                if (attachment.url.startsWith("http")) attachment.url else "http://${attachment.url}"
            Thread {
                val response = Jsoup
                    .connect(url)
                    .followRedirects(true)
                    .timeout(10000)
                    .execute()
                val document = response.parse()
                metadata = metadata.copy(title = document.title())
                val openGraphTags = document.select("meta[property^=og:]")

                openGraphTags.forEach {
                    when (it.attr("property")) {
                        "og:image" -> metadata = metadata.copy(thumbnailUrl = it.attr("content"))
                        "og:title" -> metadata = metadata.copy(title = it.attr("content"))
                    }
                }

                mmkv.encode("link:${attachment.id}", Json.encodeToString(metadata))
                synchronize()
            }.start()
        } catch (e: Exception) {
            Log.d("LOAD_METADATA", e.toString())
        }
    }
}