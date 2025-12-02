package com.cruzjeff225.gastoapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cruzjeff225.gastoapp.data.model.User
import com.cruzjeff225.gastoapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = AuthRepository.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess

    private val _deleteAccountSuccess = MutableLiveData<Boolean>()
    val deleteAccountSuccess: LiveData<Boolean> = _deleteAccountSuccess

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true

            val currentUser = repository.getCurrentUser()
            if (currentUser != null) {
                // Get basic user data
                val basicUser = User(
                    id = currentUser.uid,
                    fullName = currentUser.displayName ?: "",
                    email = currentUser.email ?: "",
                    profileImageUrl = currentUser.photoUrl?.toString() ?: ""
                )
                _user.value = basicUser

                // Get user data from Firestore
                val result = repository.getUserData(currentUser.uid)
                result.onSuccess { firestoreUser ->
                    _user.value = firestoreUser
                }.onFailure { exception ->
                    createUserDocument(basicUser)
                }
            } else {
                _error.value = "Usuario no autenticado"
            }

            _isLoading.value = false
        }
    }

    private fun createUserDocument(user: User) {
        viewModelScope.launch {
            repository.updateUserData(user)
        }
    }

    fun updateUserProfile(displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.updateUserProfile(displayName)
            result.onSuccess {
                // Actualizar también en Firestore
                _user.value?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        fullName = displayName,
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.updateUserData(updatedUser)
                    _user.value = updatedUser
                }
            }.onFailure { exception ->
                _error.value = "Error al actualizar perfil: ${exception.message}"
            }

            _isLoading.value = false
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.changePassword(currentPassword, newPassword)
            result.onSuccess {
                _error.value = "Contraseña actualizada exitosamente"
            }.onFailure { exception ->
                _error.value = "Error al cambiar contraseña: ${exception.message}"
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        repository.logout()
        _logoutSuccess.value = true
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.deleteAccount(password)
            result.onSuccess {
                _deleteAccountSuccess.value = true
            }.onFailure { exception ->
                _error.value = "Error al eliminar cuenta: ${exception.message}"
            }

            _isLoading.value = false
        }
    }

    fun getTransactionCount(): LiveData<Int> {
        val count = MutableLiveData<Int>()
        viewModelScope.launch {
            val userId = repository.currentUserId
            if (userId != null) {
                val result = repository.getUserTransactions(userId)
                result.onSuccess { transactions ->
                    count.value = transactions.size
                }
            }
        }
        return count
    }

    fun getGoalsCount(): LiveData<Int> {
        val count = MutableLiveData<Int>()
        viewModelScope.launch {
            val userId = repository.currentUserId
            if (userId != null) {
                val result = repository.getUserSavingsGoals(userId)
                result.onSuccess { goals ->
                    count.value = goals.size
                }
            }
        }
        return count
    }
}