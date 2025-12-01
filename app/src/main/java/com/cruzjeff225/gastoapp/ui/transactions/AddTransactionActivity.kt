package com.cruzjeff225.gastoapp.ui.transaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.adapters.CategoryAdapter
import com.cruzjeff225.gastoapp.databinding.ActivityAddTransactionBinding
import com.cruzjeff225.gastoapp.data.model.Categories
import com.cruzjeff225.gastoapp.data.model.Category
import com.cruzjeff225.gastoapp.data.model.Transaction
import com.cruzjeff225.gastoapp.data.model.TransactionType
import com.cruzjeff225.gastoapp.data.repository.AuthRepository
import com.cruzjeff225.gastoapp.utils.showToast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val repository = AuthRepository.getInstance()

    private var selectedType = TransactionType.EXPENSE
    private var selectedCategory: Category? = null
    private var selectedDate = Date()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryRecyclerView()
        setupListeners()
        updateDateDisplay()
    }

    private fun setupCategoryRecyclerView() {
        val categories = Categories.getByType(selectedType)
        categoryAdapter = CategoryAdapter(categories) { category ->
            selectedCategory = category
        }

        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(this@AddTransactionActivity, 4)
            adapter = categoryAdapter
        }
    }

    private fun setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Transaction type toggle
        binding.btnExpense.setOnClickListener {
            selectTransactionType(TransactionType.EXPENSE)
        }

        binding.btnIncome.setOnClickListener {
            selectTransactionType(TransactionType.INCOME)
        }

        // Date picker
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Save button
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun selectTransactionType(type: TransactionType) {
        selectedType = type
        selectedCategory = null

        when (type) {
            TransactionType.EXPENSE -> {
                binding.btnExpense.setBackgroundResource(R.drawable.bg_period_buttom_selected)
                binding.btnExpense.setTextColor(getColor(R.color.text_primary))
                binding.btnExpense.setTypeface(null, android.graphics.Typeface.BOLD)

                binding.btnIncome.setBackgroundResource(android.R.color.transparent)
                binding.btnIncome.setTextColor(getColor(R.color.text_secondary))
                binding.btnIncome.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
            TransactionType.INCOME -> {
                binding.btnIncome.setBackgroundResource(R.drawable.bg_period_buttom_selected)
                binding.btnIncome.setTextColor(getColor(R.color.text_primary))
                binding.btnIncome.setTypeface(null, android.graphics.Typeface.BOLD)

                binding.btnExpense.setBackgroundResource(android.R.color.transparent)
                binding.btnExpense.setTextColor(getColor(R.color.text_secondary))
                binding.btnExpense.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }

        // Update categories
        setupCategoryRecyclerView()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.time
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val selected = dateFormat.format(selectedDate)

        binding.tvSelectedDate.text = if (today == selected) "Hoy" else selected
    }

    private fun saveTransaction() {
        // Validations
        val amountText = binding.etAmount.text.toString().trim()
        if (amountText.isEmpty()) {
            showToast("Ingresa un monto")
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            showToast("Ingresa un monto válido")
            return
        }

        if (selectedCategory == null) {
            showToast("Selecciona una categoría")
            return
        }

        val userId = repository.currentUserId
        if (userId == null) {
            showToast("Error: Usuario no autenticado")
            return
        }

        val description = binding.etDescription.text.toString().trim()

        val transaction = Transaction(
            userId = userId,
            type = selectedType,
            amount = amount,
            category = selectedCategory!!.name,
            description = description,
            date = selectedDate.time,
            createdAt = Date().time
        )

        // Save to Firebase
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Guardando..."

        lifecycleScope.launch {
            val result = repository.addTransaction(transaction)

            result.onSuccess { documentId ->
                showToast("Transacción guardada")
                setResult(RESULT_OK)
                finish()
            }.onFailure { exception ->
                showToast("Error: ${exception.message}")
                binding.btnSave.isEnabled = true
                binding.btnSave.text = "Guardar Transacción"
            }
        }
    }
}