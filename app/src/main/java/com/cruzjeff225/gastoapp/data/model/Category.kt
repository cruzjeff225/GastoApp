package com.cruzjeff225.gastoapp.data.model

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: TransactionType
)

object Categories {
    val EXPENSE_CATEGORIES = listOf(
        Category("1", "Comida", "🍔", "#FF6B6B", TransactionType.EXPENSE),
        Category("2", "Transporte", "🚗", "#4ECDC4", TransactionType.EXPENSE),
        Category("3", "Entretenimiento", "🎬", "#45B7D1", TransactionType.EXPENSE),
        Category("4", "Vivienda", "🏠", "#96CEB4", TransactionType.EXPENSE),
        Category("5", "Salud", "💊", "#DDA15E", TransactionType.EXPENSE),
        Category("6", "Educación", "📚", "#BC6C25", TransactionType.EXPENSE),
        Category("7", "Compras", "🛍️", "#E63946", TransactionType.EXPENSE),
        Category("8", "Servicios", "💡", "#457B9D", TransactionType.EXPENSE),
        Category("9", "Otros", "📦", "#8D99AE", TransactionType.EXPENSE)
    )

    val INCOME_CATEGORIES = listOf(
        Category("10", "Salario", "💼", "#06D6A0", TransactionType.INCOME),
        Category("11", "Freelance", "💻", "#118AB2", TransactionType.INCOME),
        Category("12", "Inversiones", "📈", "#073B4C", TransactionType.INCOME),
        Category("13", "Regalo", "🎁", "#EF476F", TransactionType.INCOME),
        Category("14", "Otros", "💰", "#FFD166", TransactionType.INCOME)
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