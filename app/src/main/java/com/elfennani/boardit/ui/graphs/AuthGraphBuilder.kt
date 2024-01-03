package com.elfennani.boardit.ui.graphs

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.elfennani.boardit.ui.screens.onboarding.OnBoardingScreenPattern
import com.elfennani.boardit.ui.screens.login.loginScreen
import com.elfennani.boardit.ui.screens.login.navigateToLogin
import com.elfennani.boardit.ui.screens.sign_up.navigateToSignUp
import com.elfennani.boardit.ui.screens.onboarding.onBoardingScreen
import com.elfennani.boardit.ui.screens.sign_up.signUpScreen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult

const val AuthGraphPattern = "auth"


fun NavGraphBuilder.authGraph(
    navController: NavController,
    supabaseClient: SupabaseClient,
    context: Context
) {
    navigation(startDestination = OnBoardingScreenPattern, route = AuthGraphPattern) {
        onBoardingScreen(
            supabaseClient,
            onNativeSignInResult = {
                handleNativeSignin(it, context) {
//                    navController.navigateToHomeScreen()
                }
            },
            onLogin = {
                navController.navigateToLogin()
            },
            onSignUp = {
                navController.navigateToSignUp()
            }
        )

        loginScreen(
            onBack = {
                navController.popBackStack()
            },
            onLogin = {
//                navController.navigateToHomeScreen()
            }
        )

        signUpScreen(
            supabaseClient,
            onBack = {
                navController.popBackStack()
            },
            onNativeSigninResult = {
                handleNativeSignin(it, context) {
//                    navController.navigateToHomeScreen()
                }
            },
            onSignUp = {
//                navController.navigateToHomeScreen()
            }
        )
    }
}

fun handleNativeSignin(
    nativeSignInResult: NativeSignInResult,
    context: Context,
    onSuccess: () -> Unit
) {
    val handler = Handler(Looper.getMainLooper())
    handler.post {
        when (nativeSignInResult) {
            is NativeSignInResult.Success -> {
                onSuccess()
            }

            is NativeSignInResult.NetworkError -> {
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                Log.d("ERRORSIGNIN2", nativeSignInResult.message)
            }

            is NativeSignInResult.Error -> {
                Toast.makeText(context, nativeSignInResult.message, Toast.LENGTH_SHORT).show()
                Log.d("ERRORSIGNIN3", nativeSignInResult.message)
            }

            else -> {}
        }
    }
}
