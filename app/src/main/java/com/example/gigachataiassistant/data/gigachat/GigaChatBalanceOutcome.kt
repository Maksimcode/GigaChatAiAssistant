package com.example.gigachataiassistant.data.gigachat

data class GigaChatBalanceEntry(
    val usage: String,
    val value: Double,
)

sealed class GigaChatBalanceOutcome {
    data class Success(val entries: List<GigaChatBalanceEntry>) : GigaChatBalanceOutcome()
    data object NotAvailable : GigaChatBalanceOutcome()
    data class Failure(val cause: Throwable) : GigaChatBalanceOutcome()
}
