package com.waiter.app.data.dto

data class UpdateItemStatusRequest(
    val status: Int // Ми будемо передавати int (0,1,2), як в Enum на сервері
)