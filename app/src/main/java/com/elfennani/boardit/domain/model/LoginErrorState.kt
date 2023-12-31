package com.elfennani.boardit.domain.model

data class LoginErrorState(
    val email: String? = null,
    val password: String? = null,
    val other: String? = null
)
