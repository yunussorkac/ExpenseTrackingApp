package com.app.expensetracking.domain.usecase.currency

import com.app.expensetracking.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    fun getSingleRateFlow(base: String, target: String): Flow<Double?> {
        return repository.getRateFlow(base, target)
    }
}