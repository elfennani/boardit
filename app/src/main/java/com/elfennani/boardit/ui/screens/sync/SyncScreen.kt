package com.elfennani.boardit.ui.screens.sync

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.launch
import java.io.File
import java.util.Collections
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SyncScreen(
    account: GoogleSignInAccount?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val limit = ActivityResultContracts.PickVisualMedia()
    val pickImages = rememberLauncherForActivityResult(limit) {
        scope.launch {
            upload(context, it!!)
        }
    }
    var files by remember {
        mutableStateOf(emptyList<com.google.api.services.drive.model.File>())
    }
    LaunchedEffect(key1 = null) {
        Thread {
            files = getFiles(context)
        }.start()
    }

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: androidx.activity.result.ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)

                    Log.d("LOGIN", task.result.displayName.toString())
                }
            }
        }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sync with Google Drive (${files.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            if (account == null)
                Button(onClick = {
                    startForResult.launch(getGoogleSignInClient(context).signInIntent)
                }) {
                    Text(text = "Login Now")
                }
            else {
                Button(onClick = { pickImages.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text(text = "Sync Now")
                }
            }
        }
    }
}

fun upload(context: Context, uri: Uri) {
    val bytes = context.contentResolver.openInputStream(uri)
        .use { stream -> stream!!.readBytes() }
    val type = context.contentResolver.getType(uri)
    val filename = UUID.randomUUID().toString()
    context.openFileOutput(filename, Context.MODE_PRIVATE).use { stream ->
        stream.write(bytes)
    }

    val file = File(context.filesDir, filename)
    val gFile = com.google.api.services.drive.model.File()
    gFile.setName(filename)
    gFile.setParents(Collections.singletonList("appDataFolder"));
    val fileContent = FileContent(type, file)
    val drive = getDriveInstance(context)
    Thread {
        drive.files().create(gFile, fileContent).setFields("id").execute().let {
            Log.d("UPLOAD", it.id)
        }
    }.start()
}

fun getFiles(context: Context): List<com.google.api.services.drive.model.File> {
    val drive = getDriveInstance(context)
    val files = drive.files()
        .list()
        .setSpaces("appDataFolder")
        .execute()

    return files.files
}

fun getDriveInstance(context: Context): Drive {

    val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
    // get credentials
    val credential = GoogleAccountCredential.usingOAuth2(
        context, listOf(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
    )
    if (googleAccount != null) {
        credential.selectedAccount = googleAccount.account!!
    }

    // get Drive Instance

    return Drive
        .Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
        .setApplicationName(context.getString(R.string.app_name))
        .build()
}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE))
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}

const val SyncScreenPattern = "boards/sync"
fun NavGraphBuilder.syncScreen(navController: NavController) {
    composable(SyncScreenPattern) {
        val context = LocalContext.current

        SyncScreen(
            account = GoogleSignIn.getLastSignedInAccount(context),
            onBack = navController::popBackStack
        )
    }
}

fun NavController.navigateToSyncScreen() {
    navigate(SyncScreenPattern)
}