package com.cruzjeff225.gastoapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.databinding.ItemTransactionBinding
import com.cruzjeff225.gastoapp.data.model.Categories
import com.cruzjeff225.gastoapp.data.model.Transaction
import com.cruzjeff225.gastoapp.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

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
                tvCategoryIcon.text = category?.icon ?: "📦"
                tvCategory.text = transaction.category

                // Date: dd.MM.yyyy
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                tvDate.text = dateFormat.format(Date(transaction.date))

                // Amount
                val isIncome = transaction.type == TransactionType.INCOME
                val amountValue = transaction.amount.toInt()

                tvAmount.text = if (isIncome) "+$amountValue" else "-$amountValue"

                // Colors
                val amountColor = if (isIncome) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
                val bgColor = if (isIncome) Color.parseColor("#E8F5E9") else Color.parseColor("#FFEBEE")

                tvAmount.setTextColor(amountColor)
                iconBackground.setBackgroundColor(bgColor)

                // Arrow indicator
                ivArrow.visibility = if (isIncome) android.view.View.VISIBLE else android.view.View.GONE
                if (isIncome) {
                    ivArrow.setImageResource(android.R.drawable.arrow_up_float)
                    ivArrow.setColorFilter(Color.parseColor("#4CAF50"))
                } else {
                    ivArrow.setImageResource(android.R.drawable.arrow_down_float)
                    ivArrow.setColorFilter(Color.parseColor("#F44336"))
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