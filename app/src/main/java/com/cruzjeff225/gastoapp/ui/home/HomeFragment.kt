package com.cruzjeff225.gastoapp.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.adapters.TransactionAdapter
import com.cruzjeff225.gastoapp.databinding.FragmentHomeBinding
import com.cruzjeff225.gastoapp.data.model.Transaction
import com.cruzjeff225.gastoapp.ui.transaction.AddTransactionActivity
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

    // Activity result launcher
    private val addTransactionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

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

        // Select week by default
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
            setHasFixedSize(false)
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

            if (transactions.isEmpty()) {
                binding.emptyState.visible()
                binding.rvRecentTransactions.gone()
                adapter.submitList(emptyList())
            } else {
                binding.emptyState.gone()
                binding.rvRecentTransactions.visible()

                val filteredTransactions = filterTransactionsByPeriod(transactions)

                // Crear una nueva lista para forzar la actualización
                adapter.submitList(filteredTransactions.toList()) {
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) binding.progressBar.visible() else binding.progressBar.gone()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                requireContext().showToast(it)
            }
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

        // FAB click
        binding.fabAddTransaction.setOnClickListener {
            val intent = Intent(requireContext(), AddTransactionActivity::class.java)
            addTransactionLauncher.launch(intent)
        }
    }

    private fun selectPeriod(period: String, selectedButton: TextView) {
        selectedPeriod = period

        // Clear all buttons
        resetPeriodButtons()

        // Highlight selected
        selectedButton.setBackgroundResource(R.drawable.bg_period_buttom_selected)
        selectedButton.setTextColor("#2C2C2C".toColorInt())
        selectedButton.setTypeface(null, Typeface.BOLD)

        // Refresh list
        viewModel.transactions.value?.let { transactions ->
            val filtered = filterTransactionsByPeriod(transactions)
            adapter.submitList(filtered.toList())
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

    private fun updateMonthLabel() {
        val spanishLocale = Locale("es", "ES")
        val dateFormat = SimpleDateFormat("MMMM", spanishLocale)
        val month = dateFormat.format(Date())
        binding.tvMonthLabel.text = month.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(spanishLocale) else it.toString()
        }
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

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment resumes
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}