package com.elfennani.boardit.ui.screens.sign_up

data class SignupErrorState(
    val fullName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val repeatPassword: String? = null,
    val other: String? = null
)
