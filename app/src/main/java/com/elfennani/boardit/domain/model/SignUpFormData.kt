package com.elfennani.boardit.domain.model

data class SignUpFormData(
    val fullName: String,
    val email: String,
    val password: String,
    val repeatPassword: String
)
