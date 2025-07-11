package com.app.expensetracking.domain.usecase.auth

import com.app.expensetracking.data.remote.repository.IAuthRepository
import com.app.expensetracking.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(): Flow<Result<User>> = flow {
        emit(repository.getUser())
    }
}