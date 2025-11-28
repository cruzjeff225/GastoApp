package com.cruzjeff225.gastoapp.utils

object Constants {
    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val TRANSACTIONS_COLLECTION = "transactions"
    const val BUDGETS_COLLECTION = "budgets"

    // SharedPreferences
    const val PREFS_NAME = "GestorGastosPrefs"
    const val KEY_USER_ID = "userId"
    const val KEY_USER_EMAIL = "userEmail"
    const val KEY_USER_NAME = "userName"

    // Intent extras
    const val EXTRA_TRANSACTION = "extra_transaction"
    const val EXTRA_EDIT_MODE = "extra_edit_mode"

    // Request codes
    const val REQUEST_ADD_TRANSACTION = 100
    const val REQUEST_EDIT_TRANSACTION = 101
}