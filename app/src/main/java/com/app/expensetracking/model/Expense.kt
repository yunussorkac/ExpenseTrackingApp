package com.app.expensetracking.model

data class Expense(
    val expenseId : String = "",
    val title : String = "",
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val description : String = "",
    val amount : Double = 0.0,
    val date : Long = 0,

) {
}

enum class ExpenseCategory(val displayName: String) {
    FOOD_DRINKS("Food & Drinks"),
    TRANSPORT("Transport"),
    SHOPPING("Shopping"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Health"),
    HOME_BILLS("Home & Bills"),
    EDUCATION_SELFCARE("Education & Self-care"),
    OTHER("Other");

    override fun toString(): String = displayName
}