package com.app.expensetracking.data.remote.repository

import com.app.expensetracking.domain.model.User

interface IAuthRepository {

    suspend fun registerUser(email: String, password: String): Result<User>

    suspend fun loginUser(email: String, password: String): Result<User>

    suspend fun getUser(): Result<User>

}