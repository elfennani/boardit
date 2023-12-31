package com.elfennani.boardit.presentation.auth

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.R
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

@Composable
fun OnBoardingScreen(
    onLoginWithGoogle: () -> Unit, onLogin: () -> Unit, onSignUp: () -> Unit
) {
    Scaffold {
        Column(
            Modifier
                .padding(it)
                .padding(32.dp),
        ) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Image(painterResource(id = R.drawable.onboarding), contentDescription = null)
            }
            Spacer(modifier = Modifier.height(24.dp))

            val heading = buildAnnotatedString {
                append("Discover a Creative Essence with ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("MoodBoard Pro")
                }
            }
            Text(
                text = heading,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Express, Share, Repeat. MoodBoard Pro helps you capture and curate your daily vibes effortlessly. Let the journey to self-expression begin!",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.round_alternate_email_24),
                    null,
                    Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Login with Email",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onLoginWithGoogle,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(painterResource(id = R.drawable.bi_google), null, Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Login with Google",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            val createAnAccountText = buildAnnotatedString {
                append("New here? ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("Create a new account")
                }
            }

            TextButton(onClick = onSignUp, modifier = Modifier.fillMaxWidth()) {
                Text(text = createAnAccountText, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

const val OnBoardingScreenPattern = "auth/onboarding"
fun NavGraphBuilder.onBoardingScreen(
    supabaseClient: SupabaseClient,
    onNativeSignInResult: (NativeSignInResult) -> Unit,
    onLogin: () -> Unit,
    onSignUp: () -> Unit
) {
    composable(OnBoardingScreenPattern, exitTransition = {
        fadeOut()
    }, enterTransition = {
        fadeIn()
    }) {
        val authState = supabaseClient.composeAuth.rememberSignInWithGoogle(
            onResult = onNativeSignInResult
        )

        OnBoardingScreen(onLoginWithGoogle = {
            authState.startFlow()
        }, onLogin, onSignUp)
    }
}

