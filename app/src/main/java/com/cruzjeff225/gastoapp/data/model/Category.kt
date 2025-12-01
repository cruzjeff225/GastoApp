package com.cruzjeff225.gastoapp.data.model

import androidx.annotation.DrawableRes
import com.cruzjeff225.gastoapp.R

data class Category(
    val id: String,
    val name: String,
    @DrawableRes val icon: Int,
    val color: String,
    val type: TransactionType
)

object Categories {
    val EXPENSE_CATEGORIES = listOf(
        Category("1", "Comida", R.drawable.ic_expense_comida, "#FF6B6B", TransactionType.EXPENSE),
        Category("2", "Transporte", R.drawable.ic_expense_transporte, "#4ECDC4", TransactionType.EXPENSE),
        Category("3", "Entretenimiento", R.drawable.ic_expense_entretenimiento, "#45B7D1", TransactionType.EXPENSE),
        Category("4", "Vivienda", R.drawable.ic_expense_vivienda, "#96CEB4", TransactionType.EXPENSE),
        Category("5", "Salud", R.drawable.ic_expense_salud, "#DDA15E", TransactionType.EXPENSE),
        Category("6", "Educación", R.drawable.ic_expense_educacion, "#BC6C25", TransactionType.EXPENSE),
        Category("7", "Compras", R.drawable.ic_expense_compras, "#E63946", TransactionType.EXPENSE),
        Category("8", "Servicios", R.drawable.ic_expense_servicios, "#457B9D", TransactionType.EXPENSE),
        Category("9", "Otros", R.drawable.ic_expense_otros, "#8D99AE", TransactionType.EXPENSE)
    )

    val INCOME_CATEGORIES = listOf(
        Category("10", "Salario", R.drawable.ic_income_salario, "#06D6A0", TransactionType.INCOME),
        Category("11", "Freelance", R.drawable.ic_income_freelance, "#118AB2", TransactionType.INCOME),
        Category("12", "Inversiones", R.drawable.ic_income_inversiones, "#073B4C", TransactionType.INCOME),
        Category("13", "Regalo", R.drawable.ic_income_regalo, "#EF476F", TransactionType.INCOME),
        Category("14", "Otros", R.drawable.ic_income_otros, "#FFD166", TransactionType.INCOME)
    )

    fun getAll() = EXPENSE_CATEGORIES + INCOME_CATEGORIES

    fun getByType(type: TransactionType) = if (type == TransactionType.EXPENSE) {
        EXPENSE_CATEGORIES
    } else {
        INCOME_CATEGORIES
    }

    fun getCategoryByName(name: String): Category? {
        return getAll().find { it.name == name }
    }
}
