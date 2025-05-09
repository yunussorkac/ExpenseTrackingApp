package com.app.expensetracking.data.remote.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {
    @GET("v1/currencies/{base}.json")
    suspend fun getRates(@Path("base") base: String): Response<JsonObject>
}