package com.cruzjeff225.gastoapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cruzjeff225.gastoapp.databinding.ItemTransactionBinding
import com.cruzjeff225.gastoapp.data.model.Categories
import com.cruzjeff225.gastoapp.data.model.Transaction
import com.cruzjeff225.gastoapp.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.toColorInt

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            with(binding) {
                // Category
                val category = Categories.getCategoryByName(transaction.category)
                ivCategoryIcon.setImageResource(category?.icon ?: android.R.drawable.sym_def_app_icon)
                tvCategory.text = transaction.category

                // Format date
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                tvDate.text = dateFormat.format(Date(transaction.date))

                // Amount
                val isIncome = transaction.type == TransactionType.INCOME
                val amountValue = transaction.amount.toInt()

                tvAmount.text = if (isIncome) "+$${amountValue}" else "-$${amountValue}"

                // Colors
                val amountColor = if (isIncome) "#4CAF50".toColorInt() else "#F44336".toColorInt()
                val bgColor = if (isIncome) "#E8F5E9".toColorInt() else "#FFEBEE".toColorInt()

                tvAmount.setTextColor(amountColor)
                DrawableCompat.setTint(iconBackground.background, bgColor)


                // Arrow indicator
                if (isIncome) {
                    ivArrow.visibility = android.view.View.VISIBLE
                    ivArrow.setImageResource(android.R.drawable.arrow_up_float)
                    ivArrow.setColorFilter("#4CAF50".toColorInt())
                } else {
                    ivArrow.visibility = android.view.View.VISIBLE
                    ivArrow.setImageResource(android.R.drawable.arrow_down_float)
                    ivArrow.setColorFilter("#F44336".toColorInt())
                }

                // Click listeners
                root.setOnClickListener { onItemClick(transaction) }
                root.setOnLongClickListener {
                    onDeleteClick(transaction)
                    true
                }
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}