package com.app.expensetracking.domain.repo

import android.util.Log
import com.app.expensetracking.model.Expense
import com.app.expensetracking.model.ExpenseCategory
import com.app.expensetracking.util.HelperFunctions.Companion.getStartOfDay
import com.app.expensetracking.util.HelperFunctions.Companion.getStartOfMonth
import com.app.expensetracking.util.HelperFunctions.Companion.getStartOfWeek
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
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

    override suspend fun getDailyExpenseTotal(): Double {
        val start = getStartOfDay()
        val end = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        Log.d("ExpenseDebug", "Fetching daily expenses from $start to $end")

        try {
            val snapshot = firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .whereGreaterThanOrEqualTo("date", start)
                .whereLessThanOrEqualTo("date", end)
                .get()
                .await()

            Log.d("ExpenseDebug", "Daily document count: ${snapshot.size()}")

            if (snapshot.isEmpty) {
                Log.d("ExpenseDebug", "No expenses found for today.")
                return 0.0
            } else {
                var total = 0.0
                snapshot.forEach { document ->
                    try {
                        val expense = document.toObject(Expense::class.java)
                        Log.d("ExpenseDebug", "Expense: ${expense.title} - ${expense.amount} - Date: ${expense.date}")
                        total += expense.amount
                    } catch (e: Exception) {
                        Log.e("ExpenseDebug", "Error processing expense: ${e.message}")
                    }
                }
                Log.d("ExpenseDebug", "Daily total: $total")
                return total
            }
        } catch (e: Exception) {
            Log.e("ExpenseDebug", "Error getting daily expenses: ${e.message}", e)
            return 0.0
        }
    }


    override suspend fun getWeeklyExpenseTotal(): Double {
        val start = getStartOfWeek()
        return firestore.collection("Expenses")
            .document(auth.currentUser?.uid ?: "")
            .collection("Expenses")
            .whereGreaterThanOrEqualTo("date", start)
            .get().await()
            .sumOf { it.toObject(Expense::class.java).amount }
    }

    override suspend fun getMonthlyExpenseTotal(): Double {
        val start = getStartOfMonth()
        return firestore.collection("Expenses")
            .document(auth.currentUser?.uid ?: "")
            .collection("Expenses")
            .whereGreaterThanOrEqualTo("date", start)
            .get().await()
            .sumOf { it.toObject(Expense::class.java).amount }
    }

    override suspend fun getMonthlyCategoryTotals(): Map<ExpenseCategory, Double> {
        val start = getStartOfMonth()
        val expenses = firestore.collection("Expenses")
            .document(auth.currentUser?.uid ?: "")
            .collection("Expenses")
            .whereGreaterThanOrEqualTo("date", start)
            .get().await()
            .map { it.toObject(Expense::class.java) }

        return expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
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

}