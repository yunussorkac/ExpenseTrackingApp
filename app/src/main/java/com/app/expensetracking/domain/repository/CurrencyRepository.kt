package com.app.expensetracking.domain.repository

import android.util.Log
import com.app.expensetracking.data.remote.api.CurrencyApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val api: CurrencyApi
) {
    fun getRateFlow(base: String, target: String): Flow<Double?> = flow {
        val response = api.getRates(base)
        if (response.isSuccessful) {
            val json = response.body()
            val currencyObject = json?.getAsJsonObject(base)
            emit(currencyObject?.get(target)?.asDouble)
            Log.d("CurrencyRepository", "getRateFlow: $currencyObject")
        } else {
            Log.d("CurrencyRepository", "getRateFlow: ${response.errorBody()}")
            emit(null)
        }
    }.catch { emit(null)
        Log.d("CurrencyRepository", "getRateFlow: $it")
    }
}