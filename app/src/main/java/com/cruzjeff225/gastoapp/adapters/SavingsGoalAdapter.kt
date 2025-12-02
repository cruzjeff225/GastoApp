package com.cruzjeff225.gastoapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.databinding.ItemSavingsGoalBinding
import com.cruzjeff225.gastoapp.data.model.SavingsGoal
import androidx.core.graphics.toColorInt

class SavingsGoalAdapter(
    private val onItemClick: (SavingsGoal) -> Unit,
    private val onEditClick: (SavingsGoal) -> Unit,
    private val onDeleteClick: (SavingsGoal) -> Unit,
    private val onAddMoneyClick: (SavingsGoal) -> Unit
) : ListAdapter<SavingsGoal, SavingsGoalAdapter.SavingsGoalViewHolder>(SavingsGoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavingsGoalViewHolder {
        val binding = ItemSavingsGoalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavingsGoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavingsGoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavingsGoalViewHolder(
        private val binding: ItemSavingsGoalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: SavingsGoal) {
            with(binding) {
                // Basic info
                ivGoalIcon.setImageResource(goal.icon)
                tvGoalName.text = goal.name

                // Deadline
                val daysRemaining = goal.getDaysRemaining()
                if (daysRemaining != null) {
                    tvDeadline.visibility = View.VISIBLE
                    tvDeadline.text = when {
                        daysRemaining == 0 -> "📅 Vence hoy"
                        daysRemaining == 1 -> "📅 1 día restante"
                        daysRemaining < 0 -> "📅 Vencida"
                        else -> "📅 $daysRemaining días restantes"
                    }

                    // Color warning for near deadline
                    if (daysRemaining in 0..7) {
                        tvDeadline.setTextColor("#F59E0B".toColorInt())
                    } else {
                        tvDeadline.setTextColor("#6B7280".toColorInt())
                    }
                } else {
                    tvDeadline.visibility = View.GONE
                }

                // Amounts
                tvCurrentAmount.text = String.format("%,.0f", goal.currentAmount)
                tvTargetAmount.text = String.format("%,.0f", goal.targetAmount)

                // Progress
                val progress = goal.getProgressPercentage()
                progressBar.progress = progress

                // Progress tint based on completion
                val progressColor = when {
                    goal.isGoalReached() -> "#10B981".toColorInt()
                    progress >= 75 -> "#7C3AED".toColorInt()
                    progress >= 50 -> "#3B82F6".toColorInt()
                    progress >= 25 -> "#F59E0B".toColorInt()
                    else -> "#EF4444".toColorInt()
                }
                progressBar.progressTintList = android.content.res.ColorStateList.valueOf(progressColor)

                // Apply goal color to icon background
                try {
                    val backgroundColor = Color.parseColor(goal.color + "33") // 33 = 20% opacity
                    ivGoalIcon.setBackgroundColor(backgroundColor)
                } catch (e: Exception) {
                    ivGoalIcon.setBackgroundColor("#F5F5F7".toColorInt())
                }

                // Percentage or Completed badge
                if (goal.isGoalReached()) {
                    tvPercentage.visibility = View.GONE
                    tvCompleted.visibility = View.VISIBLE
                } else {
                    tvPercentage.visibility = View.VISIBLE
                    tvCompleted.visibility = View.GONE
                    tvPercentage.text = "$progress% completado"

                    // Color badge based on goal color
                    try {
                        android.util.Log.d("SavingsGoalAdapter", "Goal color: ${goal.color}")
                        tvPercentage.setTextColor(goal.color.toColorInt())
                        tvPercentage.setBackgroundColor((goal.color + "20").toColorInt()) // 20% opacity
                    } catch (e: Exception) {
                        // Fallback colors
                    }
                }

                // Card color based on goal color
                try {
                    cardGoal.setCardBackgroundColor(Color.WHITE)
                } catch (e: Exception) {
                    // Fallback
                }

                // Click listeners
                root.setOnClickListener {
                    onItemClick(goal)
                }

                cardGoal.setOnClickListener {
                    onAddMoneyClick(goal)
                }

                // Options menu
                btnOptions.setOnClickListener { view ->
                    showOptionsMenu(view, goal)
                }
            }
        }

        private fun showOptionsMenu(view: View, goal: SavingsGoal) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.menu_savings_goal_options, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_add_money -> {
                        onAddMoneyClick(goal)
                        true
                    }
                    R.id.action_edit -> {
                        onEditClick(goal)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(goal)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    class SavingsGoalDiffCallback : DiffUtil.ItemCallback<SavingsGoal>() {
        override fun areItemsTheSame(oldItem: SavingsGoal, newItem: SavingsGoal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavingsGoal, newItem: SavingsGoal): Boolean {
            return oldItem == newItem
        }
    }
}