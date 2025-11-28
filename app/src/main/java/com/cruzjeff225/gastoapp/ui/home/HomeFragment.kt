package com.cruzjeff225.gastoapp.ui.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.adapters.TransactionAdapter
import com.cruzjeff225.gastoapp.databinding.FragmentHomeBinding
import com.cruzjeff225.gastoapp.data.model.Transaction
import com.cruzjeff225.gastoapp.utils.gone
import com.cruzjeff225.gastoapp.utils.showToast
import com.cruzjeff225.gastoapp.utils.visible
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.toColorInt

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    private var selectedPeriod = "Semana"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()
        updateMonthLabel()

        // Select Week by default
        selectPeriod("Semana", binding.btnWeek)
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onItemClick = { transaction ->
                requireContext().showToast("Ver detalle: ${transaction.category}")
            },
            onDeleteClick = { transaction ->
                deleteTransaction(transaction)
            }
        )

        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.tvBalance.text = String.format("%,.0f", balance)
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvIncome.text = String.format("$%,.0f", income)
        }

        viewModel.totalExpenses.observe(viewLifecycleOwner) { expenses ->
            binding.tvExpenses.text = String.format("$%,.0f", expenses)
        }

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            val filteredTransactions = filterTransactionsByPeriod(transactions)
            adapter.submitList(filteredTransactions)

            if (transactions.isEmpty()) {
                binding.emptyState.visible()
                binding.rvRecentTransactions.gone()
            } else {
                binding.emptyState.gone()
                binding.rvRecentTransactions.visible()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) binding.progressBar.visible() else binding.progressBar.gone()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { requireContext().showToast(it) }
        }
    }

    private fun setupListeners() {
        binding.btnWeek.setOnClickListener {
            selectPeriod("Semana", binding.btnWeek)
        }

        binding.btnMonth.setOnClickListener {
            selectPeriod("Mes", binding.btnMonth)
        }

        binding.btnYear.setOnClickListener {
            selectPeriod("Año", binding.btnYear)
        }

        binding.ivProfile.setOnClickListener {
            requireContext().showToast("Perfil")
        }
    }

    private fun selectPeriod(period: String, selectedButton: TextView) {
        selectedPeriod = period

        // Reset all buttons
        resetPeriodButtons()

        // Highlight selected
        selectedButton.setBackgroundResource(R.drawable.bg_period_buttom_selected)
        selectedButton.setTextColor("#2C2C2C".toColorInt())
        selectedButton.setTypeface(null, Typeface.BOLD)

        // Refresh list
        viewModel.transactions.value?.let { transactions ->
            val filtered = filterTransactionsByPeriod(transactions)
            adapter.submitList(filtered)
        }
    }

    private fun resetPeriodButtons() {
        listOf(binding.btnWeek, binding.btnMonth, binding.btnYear).forEach { button ->
            button.setBackgroundColor(Color.TRANSPARENT)
            button.setTextColor("#757575".toColorInt())
            button.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun filterTransactionsByPeriod(transactions: List<Transaction>): List<Transaction> {
        val calendar = Calendar.getInstance()

        return when (selectedPeriod) {
            "Semana" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                transactions.filter { it.date >= calendar.timeInMillis }
            }
            "Mes" -> {
                calendar.add(Calendar.MONTH, -1)
                transactions.filter { it.date >= calendar.timeInMillis }
            }
            "Año" -> {
                calendar.add(Calendar.YEAR, -1)
                transactions.filter { it.date >= calendar.timeInMillis }
            }
            else -> transactions
        }
    }

    private fun updateMonthLabel() {
        val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        binding.tvMonthLabel.text = dateFormat.format(Date())
    }

    fun refreshData() {
        viewModel.loadTransactions()
    }

    private fun deleteTransaction(transaction: Transaction) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Eliminar")
            .setMessage("¿Eliminar esta transacción?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteTransaction(transaction.id)
                requireContext().showToast("Eliminado")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}