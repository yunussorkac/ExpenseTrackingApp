package com.app.expensetracking.domain.repo

import com.app.expensetracking.model.User

interface IAuthRepository {

    suspend fun registerUser(email: String, password: String): Result<User>

    suspend fun loginUser(email: String, password: String): Result<User>

    suspend fun getUser(): Result<User>

}