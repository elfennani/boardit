package com.elfennani.boardit.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.elfennani.boardit.data.local.dao.CachedAttachmentDao
import com.elfennani.boardit.data.local.entities.CachedAttachmentEntity
import com.elfennani.boardit.data.models.Attachment
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.storage.BucketApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.inject.Inject


interface CachedAttachmentRepository {
    var currentlyDownloading: Flow<List<Attachment>>
    val cachedAttachments: Flow<List<CachedAttachmentEntity>>

    suspend fun download(attachment: Attachment)
}

class CachedAttachmentRepositoryImpl @Inject constructor(
    private val cachedAttachmentDao: CachedAttachmentDao,
    private val bucketApi: BucketApi,
    @ApplicationContext private val appContext: Context
) : CachedAttachmentRepository {
    override val cachedAttachments: Flow<List<CachedAttachmentEntity>> =
        cachedAttachmentDao.getAll()

    private var _currentDownloading = MutableStateFlow(emptyList<Attachment>())
    override var currentlyDownloading: Flow<List<Attachment>> = _currentDownloading

    override suspend fun download(attachment: Attachment) {
        try {
            _currentDownloading.value = _currentDownloading.value + attachment
            val bytes = bucketApi.downloadPublic(attachment.url.split("/main/")[1])

            appContext.openFileOutput(attachment.fileName, Context.MODE_PRIVATE).use {
                it.write(bytes)
            }

            val file = File(appContext.filesDir, attachment.fileName)

            val uri = FileProvider.getUriForFile(
                appContext,
                appContext.packageName + ".provider",
                file
            );
            cachedAttachmentDao.upsertCachedAttachment(CachedAttachmentEntity(attachment.url, uri))
        } finally {
            _currentDownloading.value = _currentDownloading.value - attachment
        }
    }
}