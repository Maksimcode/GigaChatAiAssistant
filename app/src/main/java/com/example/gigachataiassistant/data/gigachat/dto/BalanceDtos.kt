package com.example.gigachataiassistant.data.gigachat.dto

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponseDto(
    val balance: List<BalanceValueDto> = emptyList(),
)

@Serializable
data class BalanceValueDto(
    val usage: String,
    val value: Double,
)
