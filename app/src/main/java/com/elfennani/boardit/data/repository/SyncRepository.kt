package com.elfennani.boardit.data.repository

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.ClipboardManager
import androidx.core.content.ContextCompat.getSystemService
import com.elfennani.boardit.R
import com.elfennani.boardit.data.local.models.SerializableData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
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
    private val boardRepository: BoardRepository,
    private val tagRepository: TagRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : SyncRepository {

    private val drive = context.driveInstance
    private val dataFilename = "data.json"

    override fun sync() {
        val data = Json.encodeToString(combineData())
        val clip = ClipData.newPlainText("JSON", data)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(clip)
        Log.d("DATAFILE", data)
//        Log.d("DATAFILE", "Syncing")
//        Log.d("DATAFILE 2", GoogleSignIn.getLastSignedInAccount(context)?.idToken.toString())
//        if (GoogleSignIn.getLastSignedInAccount(context) == null) return
//        getOnlineBoards()
    }

    private fun combineData() = SerializableData(
        boards = boardRepository.getBoards(),
        categories = categoryRepository.getCategories(),
        tags = tagRepository.getTags(),
        attachments = boardRepository.getAttachments()
    )

    private fun getOnlineBoards() {
        val files = drive.files().list().setSpaces("appDataFolder").execute().files
        Log.d("DATAFILE files", files.map { it.name }.toString())
        var dataFile = files.find { it.name == dataFilename }
        if (dataFile == null) {
            Log.d("DATAFILE", "Data file not found")
            createDataFile()
            dataFile = drive.Files().list().setSpaces("appDataFolder")
                .execute().files.find { it.name == dataFilename }!!
        }

        val outputStream: OutputStream = ByteArrayOutputStream()
        drive.Files().get(dataFile.id).executeMediaAndDownloadTo(outputStream)
        val data = String((outputStream as ByteArrayOutputStream).toByteArray())

        Log.d("DATAFILE", data)
    }

    private fun createDataFile() {
        Log.d("DATAFILE", "Creating Data File")
        val bytes = initialData.trimIndent().toByteArray()
        context.openFileOutput(dataFilename, Context.MODE_PRIVATE).use { stream ->
            stream.write(bytes)
        }

        val file = File(context.filesDir, dataFilename)
        val gFile = GFile()
        gFile.setName(dataFilename)
        gFile.setParents(Collections.singletonList("appDataFolder"));
        val fileContent = FileContent("application/json", file)
        drive.files().create(gFile, fileContent).execute()
        Log.d("DATAFILE", "Data File Created Successfully")
    }

    private val Context.driveInstance: Drive
        get() {
            val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
            val credential = GoogleAccountCredential.usingOAuth2(
                this,
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
                .setApplicationName(getString(R.string.app_name))
                .build()
        }
}