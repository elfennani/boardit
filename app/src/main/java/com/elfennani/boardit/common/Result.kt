package com.elfennani.boardit.common

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val errorMessage: String) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}