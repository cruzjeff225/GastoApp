package com.cruzjeff225.gastoapp.data.model

import com.cruzjeff225.gastoapp.R
import java.util.Date

data class SavingsGoal(
    var id: String = "",
    val userId: String = "",
    val name: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val icon: Int = R.drawable.ic_goal_general,
    val color: String = "#7C3AED",
    val deadline: Long? = null,
    val description: String = "",
    val createdAt: Long = Date().time,
    val updatedAt: Long = Date().time,
    val isCompleted: Boolean = false
) {
    // Calculate the progress in percentage
    fun getProgressPercentage(): Int {
        if (targetAmount <= 0) return 0
        val percentage = (currentAmount / targetAmount * 100).toInt()
        return percentage.coerceIn(0, 100)
    }

    // Calculate the remaining amount
    fun getRemainingAmount(): Double {
        return (targetAmount - currentAmount).coerceAtLeast(0.0)
    }

    // Check if the goal has been reached
    fun isGoalReached(): Boolean {
        return currentAmount >= targetAmount
    }

    // Calculate the remaining days
    fun getDaysRemaining(): Int? {
        deadline ?: return null
        val now = Date().time
        if (deadline <= now) return 0
        val diff = deadline - now
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}

// Pre-defined icons for the goals
object SavingsGoalIcons {
    val icons = listOf(
        R.drawable.ic_goal_general to "Meta General",
        R.drawable.ic_goal_house to "Casa",
        R.drawable.ic_goal_car to "Auto",
        R.drawable.ic_goal_travel to "Viaje",
        R.drawable.ic_goal_wedding to "Boda",
        R.drawable.ic_goal_education to "Educación",
        R.drawable.ic_goal_tech to "Tecnología",
        R.drawable.ic_goal_vacation to "Vacaciones",
        R.drawable.ic_goal_entertainment to "Entretenimiento",
        R.drawable.ic_goal_investment to "Inversión",
        R.drawable.ic_goal_gift to "Regalo",
        R.drawable.ic_goal_health to "Salud",
        R.drawable.ic_goal_hobby to "Hobby",
        R.drawable.ic_goal_smartphone to "Smartphone",
        R.drawable.ic_goal_watch to "Reloj",
        R.drawable.ic_goal_gym to "Gimnasio"
    )

    fun getIconByName(name: String): Int? {
        return icons.find { it.second == name }?.first
    }
}

// Pre-defined colors for the goals
object SavingsGoalColors {
    val colors = listOf(
        "#7C3AED" to "Púrpura",
        "#3B82F6" to "Azul",
        "#10B981" to "Verde",
        "#F59E0B" to "Naranja",
        "#EF4444" to "Rojo",
        "#EC4899" to "Rosa",
        "#8B5CF6" to "Violeta",
        "#14B8A6" to "Turquesa",
        "#F97316" to "Naranja Oscuro",
        "#06B6D4" to "Cyan"
    )

    fun getColorByName(name: String): String? {
        return colors.find { it.second == name }?.first
    }
}
