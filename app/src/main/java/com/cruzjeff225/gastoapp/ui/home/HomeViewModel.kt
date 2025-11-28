package com.cruzjeff225.gastoapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cruzjeff225.gastoapp.data.model.Transaction
import com.cruzjeff225.gastoapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = AuthRepository.getInstance()

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> = _balance

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpenses = MutableLiveData<Double>()
    val totalExpenses: LiveData<Double> = _totalExpenses

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = repository.currentUserId
            if (userId != null) {
                val result = repository.getUserTransactions(userId)

                result.onSuccess { transactionsList ->
                    _transactions.value = transactionsList
                    calculateTotals(transactionsList)
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } else {
                _error.value = "Usuario no autenticado"
            }

            _isLoading.value = false
        }
    }

    private fun calculateTotals(transactionsList: List<Transaction>) {
        _balance.value = repository.calculateBalance(transactionsList)
        _totalIncome.value = repository.getTotalIncome(transactionsList)
        _totalExpenses.value = repository.getTotalExpenses(transactionsList)
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            val result = repository.deleteTransaction(transactionId)

            result.onSuccess {
                loadTransactions() // Recargar lista
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }
}