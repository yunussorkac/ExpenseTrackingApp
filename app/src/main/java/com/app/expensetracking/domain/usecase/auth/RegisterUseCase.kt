package com.app.expensetracking.domain.usecase.auth

import com.app.expensetracking.data.remote.repository.IAuthRepository
import com.app.expensetracking.domain.model.User
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.registerUser(email, password)
    }
}