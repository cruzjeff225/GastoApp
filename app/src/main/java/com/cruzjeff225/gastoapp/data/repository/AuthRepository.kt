package com.cruzjeff225.gastoapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.cruzjeff225.gastoapp.data.model.Transaction
import com.cruzjeff225.gastoapp.data.model.TransactionType
import com.cruzjeff225.gastoapp.utils.Constants
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Authentication

    // Get current user ID
    val currentUserId: String?
        get() = auth.currentUser?.uid

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // New user registration
    suspend fun registerUser(email: String, password: String, fullName: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            // Update user profile with full name
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()
                user.updateProfile(profileUpdates).await()
            }

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Registro fallido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login user
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Inicio de sesión fallido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Logout user
    fun logout() {
        auth.signOut()
    }


    // Transactions

    suspend fun addTransaction(transaction: Transaction): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .add(transaction)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
        return try {
            firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .document(transaction.id)
                .set(transaction)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserTransactions(userId: String): Result<List<Transaction>> {
        return try {
            val snapshot = firestore.collection(Constants.TRANSACTIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val transactions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }

            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Calculations

    fun calculateBalance(transactions: List<Transaction>): Double {
        val income = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        return income - expenses
    }

    fun getTotalIncome(transactions: List<Transaction>): Double {
        return transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
    }

    fun getTotalExpenses(transactions: List<Transaction>): Double {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    fun getCategoryTotals(transactions: List<Transaction>): Map<String, Double> {
        return transactions
            .groupBy { it.category }
            .mapValues { (_, trans) -> trans.sumOf { it.amount } }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }
    }
}