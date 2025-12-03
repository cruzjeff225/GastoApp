package com.cruzjeff225.gastoapp.ui.savingsgoal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cruzjeff225.gastoapp.adapters.SavingsGoalAdapter
import com.cruzjeff225.gastoapp.databinding.FragmentSavingsGoalsBinding
import com.cruzjeff225.gastoapp.data.model.SavingsGoal
import com.cruzjeff225.gastoapp.utils.Constants
import com.cruzjeff225.gastoapp.utils.CustomDialog
import com.cruzjeff225.gastoapp.utils.InputDialog
import com.cruzjeff225.gastoapp.utils.gone
import com.cruzjeff225.gastoapp.utils.showToast
import com.cruzjeff225.gastoapp.utils.visible

class SavingsGoalsFragment : Fragment() {

    private var _binding: FragmentSavingsGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SavingsGoalsViewModel by viewModels()
    private lateinit var adapter: SavingsGoalAdapter

    // Activity result launcher for add/edit goal
    private val addEditGoalLauncher = registerForActivityResult(
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
        _binding = FragmentSavingsGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = SavingsGoalAdapter(
            onItemClick = { goal ->
                showGoalDetails(goal)
            },
            onEditClick = { goal ->
                editGoal(goal)
            },
            onDeleteClick = { goal ->
                confirmDeleteGoal(goal)
            },
            onAddMoneyClick = { goal ->
                showAddMoneyDialog(goal)
            }
        )

        binding.rvSavingsGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SavingsGoalsFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.goals.observe(viewLifecycleOwner) { goals ->
            if (goals.isEmpty()) {
                binding.emptyState.visible()
                binding.rvSavingsGoals.gone()
            } else {
                binding.emptyState.gone()
                binding.rvSavingsGoals.visible()
            }

            adapter.submitList(goals.toList())
        }

        viewModel.totalSaved.observe(viewLifecycleOwner) { total ->
            binding.tvTotalSaved.text = String.format("%,.0f", total)
        }

        viewModel.completedGoalsCount.observe(viewLifecycleOwner) { completed ->
            val total = viewModel.totalGoalsCount.value ?: 0
            binding.tvGoalsCompleted.text = "$completed/$total metas"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) binding.progressBar.visible() else binding.progressBar.gone()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { requireContext().showToast(it) }
        }
    }

    private fun setupListeners() {
        binding.fabAddGoal.setOnClickListener {
            val intent = Intent(requireContext(), AddSavingsGoalActivity::class.java)
            addEditGoalLauncher.launch(intent)
        }

        binding.ivProfile.setOnClickListener {
            requireContext().showToast("Perfil")
        }
    }

    private fun showGoalDetails(goal: SavingsGoal) {
        val message = buildString {
            append("Monto objetivo: $${String.format("%,.0f", goal.targetAmount)}\n")
            append("Monto actual: $${String.format("%,.0f", goal.currentAmount)}\n")
            append("Falta: $${String.format("%,.0f", goal.getRemainingAmount())}\n")
            append("Progreso: ${goal.getProgressPercentage()}%\n")

            val daysRemaining = goal.getDaysRemaining()
            if (daysRemaining != null) {
                append("\nFecha límite: $daysRemaining días restantes")
            }

            if (goal.description.isNotEmpty()) {
                append("\n\nDescripción: ${goal.description}")
            }
        }

        CustomDialog.showInfo(
            requireContext(),
            goal.name,
            message
        ) {
            // Optionally show add money dialog after viewing details
            showAddMoneyDialog(goal)
        }
    }

    private fun editGoal(goal: SavingsGoal) {
        val intent = Intent(requireContext(), AddSavingsGoalActivity::class.java)
        intent.putExtra(Constants.EXTRA_SAVINGS_GOAL_ID, goal.id)
        addEditGoalLauncher.launch(intent)
    }

    private fun confirmDeleteGoal(goal: SavingsGoal) {
        CustomDialog.showDeleteConfirmation(
            requireContext(),
            goal.name
        ) {
            viewModel.deleteGoal(goal.id)
            requireContext().showToast("Meta eliminada")
        }
    }

    private fun showAddMoneyDialog(goal: SavingsGoal) {
        InputDialog.showAddMoneyDialog(
            requireContext(),
            goal.name,
            goal.currentAmount,
            goal.getRemainingAmount()
        ) { amount ->
            viewModel.addMoneyToGoal(goal.id, amount)
            requireContext().showToast("$${String.format("%,.0f", amount)} agregado exitosamente")
        }
    }

    private fun refreshData() {
        viewModel.loadGoals()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}