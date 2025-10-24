package com.waiter.app.data.dto

data class SetStatusRequest(
    val status: String,
    val changedBy: String
)
