package com.app.expensetracking.domain.model

data class CurrencyResponse(
    val date: String,
    val base: Map<String, Double>
)