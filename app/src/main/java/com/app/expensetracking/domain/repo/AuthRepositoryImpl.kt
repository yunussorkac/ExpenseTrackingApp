package com.app.expensetracking.domain.repo

import com.app.expensetracking.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IAuthRepository {

    override suspend fun registerUser(email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val user = User(firebaseUser.uid, email)

                firestore.collection("Users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()

                Result.success(user)
            } else {
                Result.failure(Exception("Error."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val user = User(firebaseUser.uid, email)
                Result.success(user)
            } else {
                Result.failure(Exception("Error."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(): Result<User> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("No user"))

            val snapshot = firestore.collection("Users")
                .document(userId)
                .get()
                .await()

            if (snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                    ?: return Result.failure(Exception("Error"))
                Result.success(user)
            } else {
                Result.failure(Exception("No user data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}