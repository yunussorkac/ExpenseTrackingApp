package com.app.expensetracking.presentation.currency

import androidx.lifecycle.ViewModel
import com.app.expensetracking.domain.usecase.currency.CurrencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyUseCase: CurrencyUseCase
) : ViewModel() {

    fun convertAmount(amount: Double, from: String, to: String): Flow<Double?> = flow {
        val response = currencyUseCase.getSingleRateFlow(from.lowercase(), to.lowercase())
        response.collect { rate ->
            emit(rate?.let { amount * it })
        }
    }

}