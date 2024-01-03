package com.elfennani.boardit.ui.screens.login

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.Button
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
import com.elfennani.boardit.ui.components.InputField

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    isLoading: Boolean,
    errorState: LoginErrorState,
    onLogin: (LoginFormData) -> Unit,
) {
    var emailValue by remember {
        mutableStateOf(TextFieldValue())
    }
    var passwordValue by remember {
        mutableStateOf(TextFieldValue())
    }

    Scaffold(
        modifier = Modifier.imePadding()
    ) { it ->
        Column(
            Modifier
                .padding(it)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column {
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
                        text = "Login with Email",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(56.dp))

                if (!errorState.other.isNullOrEmpty()) {
                    ErrorInfo(error = errorState.other)
                    Spacer(modifier = Modifier.height(24.dp))
                }

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
                    onValueChanged = { passwordValue = it },
                    placeHolder = "********",
                    icon = Icons.Rounded.Password,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    error = errorState.password
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }

            Button(
                onClick = { onLogin(LoginFormData(emailValue.text, passwordValue.text)) },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp),
                enabled = !isLoading
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
        }
    }
}


const val LoginScreenPattern = "auth/login"
fun NavGraphBuilder.loginScreen(
    onBack: () -> Unit,
    onLogin: () -> Unit
) {
    composable(
        LoginScreenPattern,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) + fadeIn()
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) + fadeOut()
        }
    ) {
        val loginViewModel: LoginViewModel = hiltViewModel()

        LoginScreen(
            onBack = onBack,
            isLoading = loginViewModel.isLoggingIn.value,
            errorState = loginViewModel.errorState.value
        ) {
            loginViewModel.logIn(it, onSuccess = onLogin)
        }
    }
}

fun NavController.navigateToLogin() {
    this.navigate(LoginScreenPattern)
}