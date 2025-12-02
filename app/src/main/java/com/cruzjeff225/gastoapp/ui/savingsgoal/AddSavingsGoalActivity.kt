package com.cruzjeff225.gastoapp.ui.savingsgoal

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.adapters.ColorAdapter
import com.cruzjeff225.gastoapp.adapters.IconAdapter
import com.cruzjeff225.gastoapp.databinding.ActivityAddSavingsGoalBinding
import com.cruzjeff225.gastoapp.data.model.SavingsGoal
import com.cruzjeff225.gastoapp.data.model.SavingsGoalColors
import com.cruzjeff225.gastoapp.data.model.SavingsGoalIcons
import com.cruzjeff225.gastoapp.data.repository.AuthRepository
import com.cruzjeff225.gastoapp.utils.Constants
import com.cruzjeff225.gastoapp.utils.showToast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddSavingsGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSavingsGoalBinding
    private val repository = AuthRepository.getInstance()

    private var selectedIcon = R.drawable.ic_goal_general
    private var selectedDeadline: Long? = null
    private var isEditMode = false
    private var editingGoalId: String? = null

    private lateinit var iconAdapter: IconAdapter
    private lateinit var colorAdapter: ColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSavingsGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if editing existing goal
        checkEditMode()

        setupIconRecyclerView()
        setupColorRecyclerView()
        setupListeners()
    }

    private fun checkEditMode() {
        val goalId = intent.getStringExtra(Constants.EXTRA_SAVINGS_GOAL_ID)
        if (goalId != null) {
            isEditMode = true
            editingGoalId = goalId
            binding.btnSave.text = "Actualizar Meta"
            loadGoalData(goalId)
        }
    }

    private fun loadGoalData(goalId: String) {
        lifecycleScope.launch {
            val userId = repository.currentUserId ?: return@launch
            val result = repository.getUserSavingsGoals(userId)

            result.onSuccess { goals ->
                val goal = goals.find { it.id == goalId }
                if (goal != null) {
                    populateFormWithGoal(goal)
                }
            }.onFailure {
                showToast("Error al cargar la meta")
            }
        }
    }

    private fun populateFormWithGoal(goal: SavingsGoal) {
        binding.etGoalName.setText(goal.name)
        binding.etTargetAmount.setText(goal.targetAmount.toString())
        binding.etCurrentAmount.setText(goal.currentAmount.toString())
        binding.etDescription.setText(goal.description)

        selectedIcon = goal.icon
        selectedDeadline = goal.deadline

        iconAdapter.setSelectedIcon(selectedIcon)
        colorAdapter.setSelectedColor(goal.color)

        if (selectedDeadline != null) {
            updateDeadlineDisplay()
            binding.btnClearDeadline.visibility = android.view.View.VISIBLE
        }
    }

    private fun setupIconRecyclerView() {
        iconAdapter = IconAdapter(SavingsGoalIcons.icons) { iconRes ->
            selectedIcon = iconRes
            Log.d("AddSavingsGoal", "Selected icon: $iconRes")
        }

        binding.rvIcons.apply {
            layoutManager = GridLayoutManager(this@AddSavingsGoalActivity, 4)
            adapter = iconAdapter
        }
    }

    private fun setupColorRecyclerView() {
        colorAdapter = ColorAdapter(SavingsGoalColors.colors)

        binding.rvColors.apply {
            layoutManager = GridLayoutManager(this@AddSavingsGoalActivity, 5)
            adapter = colorAdapter
        }
    }

    private fun setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Date picker
        binding.btnSelectDeadline.setOnClickListener {
            showDatePicker()
        }

        // Clear deadline
        binding.btnClearDeadline.setOnClickListener {
            selectedDeadline = null
            binding.tvSelectedDeadline.text = "Sin fecha límite"
            binding.tvSelectedDeadline.setTextColor(getColor(android.R.color.darker_gray))
            binding.btnClearDeadline.visibility = android.view.View.GONE
        }

        // Save button
        binding.btnSave.setOnClickListener {
            if (isEditMode) {
                updateGoal()
            } else {
                saveGoal()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        if (selectedDeadline != null) {
            calendar.timeInMillis = selectedDeadline!!
        }

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDeadline = calendar.timeInMillis
                updateDeadlineDisplay()
                binding.btnClearDeadline.visibility = android.view.View.VISIBLE
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Set minimum date to today
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun updateDeadlineDisplay() {
        if (selectedDeadline != null) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvSelectedDeadline.text = dateFormat.format(Date(selectedDeadline!!))
            binding.tvSelectedDeadline.setTextColor(getColor(R.color.text_primary))
        }
    }

    private fun saveGoal() {
        // Validation
        val name = binding.etGoalName.text.toString().trim()
        if (name.isEmpty()) {
            showToast("Ingresa un nombre para la meta")
            binding.etGoalName.requestFocus()
            return
        }

        val targetAmountText = binding.etTargetAmount.text.toString().trim()
        if (targetAmountText.isEmpty()) {
            showToast("Ingresa un monto objetivo")
            binding.etTargetAmount.requestFocus()
            return
        }

        val targetAmount = targetAmountText.toDoubleOrNull()
        if (targetAmount == null || targetAmount <= 0) {
            showToast("Ingresa un monto válido")
            binding.etTargetAmount.requestFocus()
            return
        }

        val currentAmountText = binding.etCurrentAmount.text.toString().trim()
        val currentAmount = if (currentAmountText.isEmpty()) 0.0 else {
            currentAmountText.toDoubleOrNull() ?: 0.0
        }

        if (currentAmount < 0) {
            showToast("El monto inicial no puede ser negativo")
            binding.etCurrentAmount.requestFocus()
            return
        }

        if (currentAmount > targetAmount) {
            showToast("El monto inicial no puede ser mayor al objetivo")
            binding.etCurrentAmount.requestFocus()
            return
        }

        val userId = repository.currentUserId
        if (userId == null) {
            showToast("Error: Usuario no autenticado")
            return
        }

        val description = binding.etDescription.text.toString().trim()

        // Obtener el color seleccionado del adapter
        val selectedColor = colorAdapter.selectedColor
        Log.d("AddSavingsGoal", "Saving with color: $selectedColor")

        val goal = SavingsGoal(
            userId = userId,
            name = name,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            icon = selectedIcon,
            color = selectedColor,
            deadline = selectedDeadline,
            description = description,
            createdAt = Date().time,
            updatedAt = Date().time,
            isCompleted = currentAmount >= targetAmount
        )

        // Save to Firebase
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Guardando..."

        lifecycleScope.launch {
            val result = repository.addSavingsGoal(goal)

            result.onSuccess { documentId ->
                Log.d("AddSavingsGoal", "Goal saved with ID: $documentId")
                showToast("Meta creada exitosamente")
                setResult(RESULT_OK)
                finish()
            }.onFailure { exception ->
                Log.e("AddSavingsGoal", "Error saving goal", exception)
                showToast("Error: ${exception.message}")
                binding.btnSave.isEnabled = true
                binding.btnSave.text = "Crear Meta"
            }
        }
    }

    private fun updateGoal() {
        // Validation (same as saveGoal)
        val name = binding.etGoalName.text.toString().trim()
        if (name.isEmpty()) {
            showToast("Ingresa un nombre para la meta")
            return
        }

        val targetAmountText = binding.etTargetAmount.text.toString().trim()
        if (targetAmountText.isEmpty()) {
            showToast("Ingresa un monto objetivo")
            return
        }

        val targetAmount = targetAmountText.toDoubleOrNull()
        if (targetAmount == null || targetAmount <= 0) {
            showToast("Ingresa un monto válido")
            return
        }

        val currentAmountText = binding.etCurrentAmount.text.toString().trim()
        val currentAmount = if (currentAmountText.isEmpty()) 0.0 else {
            currentAmountText.toDoubleOrNull() ?: 0.0
        }

        if (currentAmount < 0 || currentAmount > targetAmount) {
            showToast("Monto inicial inválido")
            return
        }

        val userId = repository.currentUserId
        if (userId == null) {
            showToast("Error: Usuario no autenticado")
            return
        }

        val description = binding.etDescription.text.toString().trim()

        // Obtener el color seleccionado del adapter
        val selectedColor = colorAdapter.selectedColor
        Log.d("AddSavingsGoal", "Updating with color: $selectedColor")

        val goal = SavingsGoal(
            id = editingGoalId ?: "",
            userId = userId,
            name = name,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            icon = selectedIcon,
            color = selectedColor,
            deadline = selectedDeadline,
            description = description,
            updatedAt = Date().time,
            isCompleted = currentAmount >= targetAmount
        )

        // Update in Firebase
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Actualizando..."

        lifecycleScope.launch {
            val result = repository.updateSavingsGoal(goal)

            result.onSuccess {
                Log.d("AddSavingsGoal", "Goal updated")
                showToast("Meta actualizada")
                setResult(RESULT_OK)
                finish()
            }.onFailure { exception ->
                Log.e("AddSavingsGoal", "Error updating goal", exception)
                showToast("Error: ${exception.message}")
                binding.btnSave.isEnabled = true
                binding.btnSave.text = "Actualizar Meta"
            }
        }
    }
}