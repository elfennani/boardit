package com.elfennani.boardit.ui.screens.sign_up

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfennani.boardit.R
import com.elfennani.boardit.ui.components.ErrorInfo
import com.elfennani.boardit.ui.components.DashedDivider
import com.elfennani.boardit.ui.components.InputField
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    isSigningUp: Boolean,
    onSignUpWithGoogle: () -> Unit,
    onSignUpWithEmail: (SignUpFormData) -> Unit,
    errorState: SignupErrorState
) {
    val scrollState = rememberScrollState()
    var fullNameValue by remember {
        mutableStateOf(TextFieldValue())
    }
    var emailValue by remember {
        mutableStateOf(TextFieldValue())
    }
    var passwordValue by remember {
        mutableStateOf(TextFieldValue())
    }
    var repeatPasswordValue by remember {
        mutableStateOf(TextFieldValue())
    }


    Scaffold() {
        Column(
            Modifier
                .padding(it)
                .consumeWindowInsets(it)
                .imePadding()
                .imeNestedScroll()
                .verticalScroll(scrollState),
        ) {
            Column(modifier = Modifier.padding(32.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                            Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Sign up",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { onSignUpWithGoogle() },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = !isSigningUp
                ) {
                    Icon(painterResource(id = R.drawable.bi_google), null, Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign up with Google",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(24.dp))

                if (!errorState.other.isNullOrEmpty()) {
                    ErrorInfo(error = errorState.other)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                InputField(
                    label = "Full name",
                    value = fullNameValue,
                    onValueChanged = { value -> fullNameValue = value },
                    placeHolder = "John Doe",
                    icon = Icons.Rounded.Person,
                    error = errorState.fullName
                )
                Spacer(modifier = Modifier.height(24.dp))
                InputField(
                    label = "Email",
                    value = emailValue,
                    onValueChanged = { value -> emailValue = value },
                    placeHolder = "john.doe@example.com",
                    icon = Icons.Rounded.AlternateEmail,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    error = errorState.email
                )
                Spacer(modifier = Modifier.height(24.dp))
                InputField(
                    label = "Password",
                    value = passwordValue,
                    onValueChanged = { value -> passwordValue = value },
                    placeHolder = "********",
                    icon = Icons.Rounded.Password,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    error = errorState.password
                )
                Spacer(modifier = Modifier.height(24.dp))
                InputField(
                    label = "Repeat Password",
                    value = repeatPasswordValue,
                    onValueChanged = { value -> repeatPasswordValue = value },
                    placeHolder = "********",
                    icon = Icons.Rounded.Password,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    error = errorState.repeatPassword
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        onSignUpWithEmail(
                            SignUpFormData(
                                fullName = fullNameValue.text,
                                email = emailValue.text,
                                password = passwordValue.text,
                                repeatPassword = repeatPasswordValue.text
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    enabled = !isSigningUp
                ) {
                    Icon(
                        painterResource(id = R.drawable.round_alternate_email_24),
                        null,
                        Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sign up",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

const val SignUpScreenPattern = "auth/signup"
fun NavGraphBuilder.signUpScreen(
    supabaseClient: SupabaseClient,
    onBack: () -> Unit,
    onNativeSigninResult: (NativeSignInResult) -> Unit,
    onSignUp: () -> Unit
) {
    composable(SignUpScreenPattern, enterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) + fadeIn()
    }, exitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) + fadeOut()
    }) {
        val signupViewModel: SignupViewModel = hiltViewModel()
        val authState =
            supabaseClient.composeAuth.rememberSignInWithGoogle(onResult = onNativeSigninResult)

        SignUpScreen(
            onBack,
            isSigningUp = signupViewModel.isSigningUp.value,
            onSignUpWithGoogle = { authState.startFlow() },
            onSignUpWithEmail = { signupViewModel.signUp(it, onSignUp) },
            errorState = signupViewModel.errorState.value
        )
    }
}

fun NavController.navigateToSignUp() {
    this.navigate(SignUpScreenPattern)
}