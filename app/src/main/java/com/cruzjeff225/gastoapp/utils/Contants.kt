package com.cruzjeff225.gastoapp.utils

object Constants {
    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val TRANSACTIONS_COLLECTION = "transactions"
    const val BUDGETS_COLLECTION = "budgets"
    const val SAVINGS_GOALS_COLLECTION = "savings_goals"

    // SharedPreferences
    const val PREFS_NAME = "GestorGastosPrefs"
    const val KEY_USER_ID = "userId"
    const val KEY_USER_EMAIL = "userEmail"
    const val KEY_USER_NAME = "userName"

    // Intent extras
    const val EXTRA_TRANSACTION = "extra_transaction"
    const val EXTRA_EDIT_MODE = "extra_edit_mode"
    const val EXTRA_SAVINGS_GOAL = "extra_savings_goal"
    const val EXTRA_SAVINGS_GOAL_ID = "extra_savings_goal_id"

    // Request codes
    const val REQUEST_ADD_TRANSACTION = 100
    const val REQUEST_EDIT_TRANSACTION = 101
    const val REQUEST_ADD_SAVINGS_GOAL = 102
    const val REQUEST_EDIT_SAVINGS_GOAL = 103
}