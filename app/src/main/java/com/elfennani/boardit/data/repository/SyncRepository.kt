package com.elfennani.boardit.data.repository

import android.content.ClipData
import android.content.Context
import android.util.Log
import com.elfennani.boardit.R
import com.elfennani.boardit.data.local.models.SerializableData
import com.elfennani.boardit.data.local.models.SerializableDeleted
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.tencent.mmkv.MMKV
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.util.Collections
import javax.inject.Inject


interface SyncRepository {
    fun sync()
    val dataFilename: String
        get() = "data.json"
    val onlineData: SerializableData
    val driveInstance: Drive
    val data: SerializableData
    fun updateDataFile(data: SerializableData)
}

const val initialData = """
{
    "boards": [],
    "categories": [],
    "tags": [],
    "attachments": []
}
"""

typealias GFile = com.google.api.services.drive.model.File

class SyncRepositoryImpl @Inject constructor(
    private val mmkv: MMKV,
    private val boardRepository: BoardRepository,
    private val tagRepository: TagRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : SyncRepository {

    private val drive = driveInstance

    override fun sync() {
        val data = Json.encodeToString(data)
        val clip = ClipData.newPlainText("JSON", data)
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(clip)
    }

    override val data
        get() = SerializableData(
            boards = boardRepository.getBoards(),
            categories = categoryRepository.getCategories(),
            tags = tagRepository.getTags(),
            attachments = boardRepository.getAttachments(),
            deleted = SerializableDeleted(
                boards = boardRepository.deletedBoards,

            )
        )

    override val onlineData: SerializableData
        get() {
            val files = drive.files().list().setSpaces("appDataFolder").execute().files
            val dataFile = files.find { it.name == dataFilename } ?: return SerializableData()

            val outputStream: OutputStream = ByteArrayOutputStream()
            drive.Files().get(dataFile.id).executeMediaAndDownloadTo(outputStream)

            return Json.decodeFromString(String((outputStream as ByteArrayOutputStream).toByteArray()))
        }

    override fun updateDataFile(data: SerializableData){
        val id = fileId
        val file = drive.Files().get(id).execute()
        val contentStream = ByteArrayContent.fromString("application/json", Json.encodeToString(data))

        drive.Files().update(id, file, contentStream)
    }

    private val fileId: String
        get() {
            if(mmkv.containsKey("data-file-id"))
                return mmkv.decodeString("data-file-id")!!

            val bytes = initialData.trimIndent().toByteArray()
            context.openFileOutput(dataFilename, Context.MODE_PRIVATE).use { stream ->
                stream.write(bytes)
            }

            val file = File(context.filesDir, dataFilename)
            val gFile = GFile()
            gFile.setName(dataFilename)
            gFile.setParents(Collections.singletonList("appDataFolder"));
            val fileContent = FileContent("application/json", file)
            return drive.files().create(gFile, fileContent).setFields("id").execute().id
        }

    override val driveInstance: Drive
        get() {
            val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
            val credential = GoogleAccountCredential.usingOAuth2(
                context,
                listOf(
                    DriveScopes.DRIVE,
                    DriveScopes.DRIVE_FILE,
                    DriveScopes.DRIVE_APPDATA
                )
            )
            credential.selectedAccount = googleAccount?.account

            return Drive
                .Builder(
                    AndroidHttp.newCompatibleTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
                )
                .setApplicationName(context.getString(R.string.app_name))
                .build()
        }
}