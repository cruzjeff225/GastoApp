package com.cruzjeff225.gastoapp.data.model

import java.util.Date

data class Transaction(
    var id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val date: Long = Date().time,
    val createdAt: Long = Date().time
)

enum class TransactionType {
    INCOME,   // Ingreso
    EXPENSE   // Gasto
}