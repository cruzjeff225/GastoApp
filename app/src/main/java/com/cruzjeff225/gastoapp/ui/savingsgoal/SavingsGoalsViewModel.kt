package com.cruzjeff225.gastoapp.ui.savingsgoal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cruzjeff225.gastoapp.data.model.SavingsGoal
import com.cruzjeff225.gastoapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SavingsGoalsViewModel : ViewModel() {

    private val repository = AuthRepository.getInstance()

    private val _goals = MutableLiveData<List<SavingsGoal>>()
    val goals: LiveData<List<SavingsGoal>> = _goals

    private val _totalSaved = MutableLiveData<Double>()
    val totalSaved: LiveData<Double> = _totalSaved

    private val _completedGoalsCount = MutableLiveData<Int>()
    val completedGoalsCount: LiveData<Int> = _completedGoalsCount

    private val _totalGoalsCount = MutableLiveData<Int>()
    val totalGoalsCount: LiveData<Int> = _totalGoalsCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadGoals()
    }

    fun loadGoals() {
        viewModelScope.launch {
            _isLoading.value = true

            val userId = repository.currentUserId
            if (userId != null) {
                val result = repository.getUserSavingsGoals(userId)

                result.onSuccess { goalsList ->
                    _goals.value = goalsList
                    calculateTotals(goalsList)
                }.onFailure { exception ->
                    Log.e("SavingsGoalsVM", "Error loading goals", exception)
                    _error.value = "Error al cargar metas: ${exception.message}"
                    _goals.value = emptyList()
                    _totalSaved.value = 0.0
                    _completedGoalsCount.value = 0
                    _totalGoalsCount.value = 0
                }
            } else {
                _error.value = "Usuario no autenticado"
                _goals.value = emptyList()
                _totalSaved.value = 0.0
                _completedGoalsCount.value = 0
                _totalGoalsCount.value = 0
            }

            _isLoading.value = false
        }
    }

    private fun calculateTotals(goalsList: List<SavingsGoal>) {
        val total = goalsList.sumOf { it.currentAmount }
        val completedCount = goalsList.count { it.isGoalReached() }

        _totalSaved.value = total
        _completedGoalsCount.value = completedCount
        _totalGoalsCount.value = goalsList.size
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            val result = repository.deleteSavingsGoal(goalId)

            result.onSuccess {
                // Reload list
                loadGoals()
            }.onFailure { exception ->
                Log.e("SavingsGoalsVM", "Error deleting goal", exception)
                _error.value = "Error al eliminar: ${exception.message}"
            }
        }
    }

    fun addMoneyToGoal(goalId: String, amount: Double) {
        viewModelScope.launch {
            val result = repository.addAmountToGoal(goalId, amount)

            result.onSuccess {
                // Reload list to show updated amounts
                loadGoals()
            }.onFailure { exception ->
                Log.e("SavingsGoalsVM", "Error adding money", exception)
                _error.value = "Error al agregar dinero: ${exception.message}"
            }
        }
    }
}