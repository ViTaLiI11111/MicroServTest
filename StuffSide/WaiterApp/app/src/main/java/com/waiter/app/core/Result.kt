package com.waiter.app.core

sealed class Result<out T> {
    data class Ok<T>(val value: T): Result<T>()
    data class Err(val error: Throwable): Result<Nothing>()
    data object Loading: Result<Nothing>()
}
