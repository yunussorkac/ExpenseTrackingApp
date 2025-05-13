package com.app.expensetracking.domain.repository

import android.util.Log
import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : IExpenseRepository {
    override suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .document(expense.expenseId)
                .set(expense)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getExpenses(): Flow<List<Expense>> = callbackFlow {
        val listener = firestore.collection("Expenses")
            .document(auth.currentUser?.uid ?: "")
            .collection("Expenses")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val expenses = snapshot?.toObjects(Expense::class.java) ?: emptyList()
                trySend(expenses)
            }

        awaitClose { listener.remove() }
    }







    override fun getLast5Expenses(): Flow<List<Expense>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: run {
            Log.e("ExpenseRepository", "User is not authenticated")
            return@callbackFlow
        }

        Log.d("ExpenseRepository", "Fetching last 5 expenses for user: $uid")

        val listenerRegistration = firestore.collection("Expenses")
            .document(uid)
            .collection("Expenses")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ExpenseRepository", "Error fetching expenses: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    if (snapshot.isEmpty) {
                        Log.d("ExpenseRepository", "No expenses found")
                        trySend(emptyList()).isSuccess
                    } else {
                        val expenses = snapshot.documents.mapNotNull {
                            it.toObject(Expense::class.java)
                        }
                        Log.d("ExpenseRepository", "Fetched ${expenses.size} expenses")
                        trySend(expenses).isSuccess
                    }
                } else {
                    Log.d("ExpenseRepository", "Snapshot is null")
                    trySend(emptyList()).isSuccess
                }
            }

        awaitClose {
            listenerRegistration.remove()
            Log.d("ExpenseRepository", "Listener removed")
        }
    }

    override suspend fun deleteExpense(expense: Expense): Result<Unit> {

        return try {
            firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .document(expense.expenseId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }



    override suspend fun getExpenseById(expenseId: String): Result<Expense> {
        return try {
            val document = firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .document(expenseId)
                .get()
                .await()

            val expense = document.toObject(Expense::class.java)
            if (expense != null) {
                Result.success(expense)
            } else {
                Result.failure(Exception("Expense not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun editExpense(expense: Expense): Result<Unit> {
        return try {
            firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .document(expense.expenseId)
                .set(expense)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}