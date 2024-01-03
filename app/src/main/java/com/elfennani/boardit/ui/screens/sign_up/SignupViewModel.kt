package com.elfennani.boardit.ui.screens.sign_up

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {
    private val _isSigningUp = mutableStateOf(false)
    val isSigningUp: State<Boolean> = _isSigningUp

    private val _errorState = mutableStateOf(SignupErrorState())
    val errorState: State<SignupErrorState> = _errorState

    fun signUp(signUpFormData: SignUpFormData, onSuccess: () -> Unit) {
        if (!validate(signUpFormData)) {
            return;
        }

        viewModelScope.launch {
            _isSigningUp.value = true
            try {
                supabaseClient.auth.signUpWith(Email) {
                    this.email = signUpFormData.email
                    this.password = signUpFormData.password
                    this.data = buildJsonObject {
                        put("fullname", signUpFormData.fullName)
                    }
                }

                onSuccess()
            } catch (e: RestException) {
                _errorState.value = _errorState.value.copy(other = e.error)
            } catch (e: Exception) {
                Log.d("SIGNUPERROR", e.toString())
            } finally {
                _isSigningUp.value = false
            }
        }
    }

    private fun validate(signUpFormData: SignUpFormData): Boolean {
        var errorState = SignupErrorState()

        if (signUpFormData.fullName.isEmpty()) {
            errorState = errorState.copy(fullName = "Full name is required")
        }

        if (signUpFormData.email.isEmpty()) {
            errorState = errorState.copy(email = "Email is required")
        }

        if (signUpFormData.password.length < 6) {
            errorState = errorState.copy(password = "Password has to have 6 character at least")
        }

        if (signUpFormData.password != signUpFormData.repeatPassword) {
            errorState = errorState.copy(repeatPassword = "Password has to match")
        }

        _errorState.value = errorState
        return errorState == SignupErrorState()
    }


}