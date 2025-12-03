package com.cruzjeff225.gastoapp.ui.transaction

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cruzjeff225.gastoapp.adapters.TransactionAdapter
import com.cruzjeff225.gastoapp.databinding.ActivityAllTransactionsBinding
import com.cruzjeff225.gastoapp.data.model.Transaction
import com.cruzjeff225.gastoapp.ui.home.HomeViewModel
import com.cruzjeff225.gastoapp.utils.CustomDialog
import com.cruzjeff225.gastoapp.utils.gone
import com.cruzjeff225.gastoapp.utils.showToast
import com.cruzjeff225.gastoapp.utils.visible
import java.util.*

class AllTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllTransactionsBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter
    private var selectedPeriod = "Semana"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedPeriod = intent.getStringExtra("period") ?: "Semana"

        setupToolbar()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.tvPeriod.text = selectedPeriod
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onItemClick = { transaction ->
                showToast("Ver detalle: ${transaction.category}")
            },
            onDeleteClick = { transaction ->
                deleteTransaction(transaction)
            }
        )

        binding.rvAllTransactions.apply {
            layoutManager = LinearLayoutManager(this@AllTransactionsActivity)
            adapter = this@AllTransactionsActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.transactions.observe(this) { transactions ->
            val filteredTransactions = filterTransactionsByPeriod(transactions)

            if (filteredTransactions.isEmpty()) {
                binding.emptyState.visible()
                binding.rvAllTransactions.gone()
            } else {
                binding.emptyState.gone()
                binding.rvAllTransactions.visible()
            }

            adapter.submitList(filteredTransactions)
            binding.tvTransactionCount.text = "${filteredTransactions.size} transacciones"
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) binding.progressBar.visible() else binding.progressBar.gone()
        }

        viewModel.error.observe(this) { error ->
            error?.let { showToast(it) }
        }
    }

    private fun filterTransactionsByPeriod(transactions: List<Transaction>): List<Transaction> {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        return when (selectedPeriod) {
            "Semana" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val startTime = calendar.timeInMillis
                transactions.filter { it.date >= startTime && it.date <= now }
            }
            "Mes" -> {
                calendar.add(Calendar.MONTH, -1)
                val startTime = calendar.timeInMillis
                transactions.filter { it.date >= startTime && it.date <= now }
            }
            "Año" -> {
                calendar.add(Calendar.YEAR, -1)
                val startTime = calendar.timeInMillis
                transactions.filter { it.date >= startTime && it.date <= now }
            }
            else -> transactions
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        CustomDialog.showDeleteConfirmation(
            this,
            transaction.category
        ) {
            viewModel.deleteTransaction(transaction.id)
            showToast("Transacción eliminada")
        }
    }
}