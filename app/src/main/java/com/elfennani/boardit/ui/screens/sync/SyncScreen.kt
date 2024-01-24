package com.elfennani.boardit.ui.screens.sync

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.elfennani.boardit.ui.components.DashedDivider
import com.elfennani.boardit.ui.screens.manage.components.SmallButton
import com.elfennani.boardit.ui.screens.sync.components.LoginView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SyncScreen(
    account: GoogleSignInAccount?,
    onLogin: () -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = {
                    if (account != null)
                        Text(
                            "Sync with Google Drive",
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
        Column(
            Modifier
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (account == null)
                LoginView(onLogin)
            else {
                Column(Modifier.fillMaxHeight()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = account.photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Column(
                            Modifier.padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = account.displayName ?: "",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Last Sync: Jul 20, 2023 at 10:10 AM",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        SmallButton(icon = Icons.Rounded.Sync, label = "Sync") {}
                        Spacer(modifier = Modifier.width(8.dp))
                        SmallButton(icon = Icons.Rounded.Logout, label = "Sign out") {
                            onSignOut()
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    DashedDivider(color = Color(0xFFD9D9D9))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nothing to Sync",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.33f)
                        )
                    }
                }
            }
        }
    }
}

val Context.googleClient: GoogleSignInClient
    get() {
        val signInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope(DriveScopes.DRIVE_FILE),
                Scope(DriveScopes.DRIVE)
            )
            .build()

        return GoogleSignIn.getClient(this, signInOptions)
    }

const val SyncScreenPattern = "boards/sync"
fun NavGraphBuilder.syncScreen(navController: NavController) {
    composable(SyncScreenPattern) {
        val context = LocalContext.current
        var account by remember {
            mutableStateOf(GoogleSignIn.getLastSignedInAccount(context))
        }
        val contract = ActivityResultContracts.StartActivityForResult()
        val startForResult =
            rememberLauncherForActivityResult(contract) { result: androidx.activity.result.ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (result.data != null) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                        account = task.result
                    }
                }
            }

        SyncScreen(
            account = account,
            onLogin = { startForResult.launch(context.googleClient.signInIntent) },
            onSignOut = {
                context.googleClient.signOut().addOnCompleteListener {
                    account = GoogleSignIn.getLastSignedInAccount(context)
                }
            },
            onBack = navController::popBackStack
        )
    }
}

fun NavController.navigateToSyncScreen() {
    navigate(SyncScreenPattern)
}