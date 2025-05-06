package com.app.expensetracking.domain.repo

import android.util.Log
import com.app.expensetracking.model.Expense
import com.app.expensetracking.model.ExpenseCategory
import com.app.expensetracking.util.HelperFunctions.Companion.getEndOfDay
import com.app.expensetracking.util.HelperFunctions.Companion.getStartOfDay
import com.app.expensetracking.util.HelperFunctions.Companion.getStartOfMonth
import com.app.expensetracking.util.HelperFunctions.Companion.getStartOfWeek
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

    override fun getDailyExpenseTotal(): Flow<Double> = flow {
        val start = getStartOfDay()
        val end =  getEndOfDay()

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
                emit(0.0)
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
                emit(total)
            }
        } catch (e: Exception) {
            Log.e("ExpenseDebug", "Error getting daily expenses: ${e.message}", e)
            emit(0.0)
        }
    }


    override fun getWeeklyExpenseTotal(): Flow<Double> = flow {
        val start = getStartOfWeek()
        try {
            val snapshot = firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .whereGreaterThanOrEqualTo("date", start)
                .get()
                .await()

            val total = snapshot.sumOf { it.toObject(Expense::class.java).amount }
            emit(total)
        } catch (e: Exception) {
            Log.e("ExpenseDebug", "Error getting weekly expenses: ${e.message}", e)
            emit(0.0)
        }
    }

    override fun getMonthlyExpenseTotal(): Flow<Double> = flow {
        val start = getStartOfMonth()
        try {
            val snapshot = firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .whereGreaterThanOrEqualTo("date", start)
                .get()
                .await()

            val total = snapshot.sumOf { it.toObject(Expense::class.java).amount }
            emit(total)
        } catch (e: Exception) {
            Log.e("ExpenseDebug", "Error getting monthly expenses: ${e.message}", e)
            emit(0.0)
        }
    }

    override fun getMonthlyCategoryTotals(): Flow<Map<ExpenseCategory, Double>> = flow {
        val start = getStartOfMonth()
        try {
            val expenses = firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .whereGreaterThanOrEqualTo("date", start)
                .get()
                .await()
                .map { it.toObject(Expense::class.java) }

            val categoryTotals = expenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            emit(categoryTotals)
        } catch (e: Exception) {
            Log.e("ExpenseDebug", "Error getting category totals: ${e.message}", e)
            emit(emptyMap())
        }
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

    override fun getMonthlyDailyExpenses(): Flow<Map<String, Double>> = flow {
        val start = getStartOfMonth()
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())

        try {
            val expenses = firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .whereGreaterThanOrEqualTo("date", start)
                .get()
                .await()
                .map { it.toObject(Expense::class.java) }

            val monthlyExpenses = expenses.filter { expense ->
                val expenseDate = Date(expense.date)
                calendar.time = expenseDate
                calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear
            }

            val dailyExpenses = monthlyExpenses.groupBy { expense ->
                val date = Date(expense.date)
                formatter.format(date)
            }.mapValues { (_, expenses) ->
                expenses.sumOf { it.amount }
            }

            Log.d("ChartDebug", "Daily expenses: $dailyExpenses")
            emit(dailyExpenses)
        } catch (e: Exception) {
            Log.e("ExpenseDebug", "Error getting monthly daily expenses: ${e.message}", e)
            emit(emptyMap())
        }
    }



}