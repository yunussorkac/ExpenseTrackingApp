package com.app.expensetracking.domain.repository

import android.util.Log
import com.app.expensetracking.data.remote.repository.IExpenseRepository
import com.app.expensetracking.domain.model.Expense
import com.app.expensetracking.domain.model.ExpenseCategory
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

    override fun getMonthlyCategoryTotals(month: Int?, year: Int?): Flow<Map<ExpenseCategory, Double>> = flow {
        val calendar = Calendar.getInstance()

        val targetMonth = month ?: calendar.get(Calendar.MONTH)
        val targetYear = year ?: calendar.get(Calendar.YEAR)

        val start = calendar.apply {
            set(Calendar.YEAR, targetYear)
            set(Calendar.MONTH, targetMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val end = calendar.apply {
            set(Calendar.YEAR, targetYear)
            set(Calendar.MONTH, targetMonth)
            set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        try {
            val expenses = firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .whereGreaterThanOrEqualTo("date", start)
                .whereLessThanOrEqualTo("date", end)
                .get()
                .await()
                .map { it.toObject(Expense::class.java) }

            val categoryTotals = expenses.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            Log.d("ChartDebug", "Category totals for ${targetMonth + 1}/$targetYear: $categoryTotals")
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

    override fun getMonthlyDailyExpenses(month: Int?, year: Int? ): Flow<Map<String, Double>> = flow {
        val calendar = Calendar.getInstance()

        val targetMonth = month ?: calendar.get(Calendar.MONTH)
        val targetYear = year ?: calendar.get(Calendar.YEAR)

        val start = calendar.apply {
            set(Calendar.YEAR, targetYear)
            set(Calendar.MONTH, targetMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val end = calendar.apply {
            set(Calendar.YEAR, targetYear)
            set(Calendar.MONTH, targetMonth)
            set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())

        try {
            val expenses = firestore.collection("Expenses")
                .document(auth.currentUser?.uid ?: "")
                .collection("Expenses")
                .whereGreaterThanOrEqualTo("date", start)
                .whereLessThanOrEqualTo("date", end)
                .get()
                .await()
                .map { it.toObject(Expense::class.java) }

            val dailyExpenses = expenses.groupBy { expense ->
                val date = Date(expense.date)
                formatter.format(date)
            }.mapValues { (_, expenses) ->
                expenses.sumOf { it.amount }
            }

            Log.d("ChartDebug", "Daily expenses for ${targetMonth + 1}/$targetYear: $dailyExpenses")
            emit(dailyExpenses)
        } catch (e: Exception) {
            Log.e("ExpenseDebug", "Error getting monthly daily expenses: ${e.message}", e)
            emit(emptyMap())
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

    override fun searchExpenses(query: String): Flow<List<Expense>> = callbackFlow {
        val queryRef = firestore
            .collection("Expenses")
            .document(auth.currentUser?.uid ?: "")
            .collection("Expenses")
            .orderBy("title")
            .startAt(query)
            .endAt(query + '\uf8ff')

        val listener = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val expenses = snapshot?.toObjects(Expense::class.java) ?: emptyList()
            trySend(expenses).isSuccess
        }

        awaitClose { listener.remove() }
    }

}