package com.elfennani.boardit.ui.screens.login

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
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) :ViewModel() {
    private val _isLoggingIn = mutableStateOf(false)
    val isLoggingIn : State<Boolean> = _isLoggingIn

    private val _errorState = mutableStateOf(LoginErrorState())
    val errorState: State<LoginErrorState> = _errorState

    fun logIn(loginFormData: LoginFormData, onSuccess: () -> Unit){
        if(!validate(loginFormData)){
            return
        }

        viewModelScope.launch {
            _isLoggingIn.value = true
            try {
                supabaseClient.auth.signInWith(Email){
                    this.email = loginFormData.email
                    this.password = loginFormData.password
                }
                onSuccess()
            }catch (e: RestException){
                _errorState.value = errorState.value.copy(other = e.error)
            }catch (e: Exception){
                Log.d("LOGINERROR", e.toString())
            }finally {
                _isLoggingIn.value = false
            }
        }
    }

    private fun validate(loginFormData: LoginFormData): Boolean {
        var errorState = LoginErrorState()

        if(loginFormData.email.isEmpty()){
            errorState = errorState.copy(email = "Email is required")
        }

        if(loginFormData.password.isEmpty()){
            errorState = errorState.copy(password = "Password has to have 6 character at least")
        }

        _errorState.value = errorState
        return errorState == LoginErrorState()
    }

}