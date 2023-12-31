package com.elfennani.boardit.domain.model

data class SignupErrorState(
    val fullName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val repeatPassword: String? = null,
    val other: String? = null
)
