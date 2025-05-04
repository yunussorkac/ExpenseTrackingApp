package com.app.expensetracking.domain.usecase.auth

import com.app.expensetracking.domain.repo.IAuthRepository
import com.app.expensetracking.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.loginUser(email, password)
    }
}