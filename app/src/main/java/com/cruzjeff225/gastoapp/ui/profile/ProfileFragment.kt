package com.cruzjeff225.gastoapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cruzjeff225.gastoapp.databinding.FragmentProfileBinding
import com.cruzjeff225.gastoapp.ui.auth.LoginActivity
import com.cruzjeff225.gastoapp.utils.CustomDialog
import com.cruzjeff225.gastoapp.utils.InputDialog
import com.cruzjeff225.gastoapp.utils.gone
import com.cruzjeff225.gastoapp.utils.showToast
import com.cruzjeff225.gastoapp.utils.visible

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.fullName.ifEmpty { "Usuario" }
                binding.tvUserEmail.text = it.email

                // Update initials in avatar
                val initials = getInitials(it.fullName)
                binding.tvUserInitials.text = initials
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

        viewModel.logoutSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                navigateToLogin()
            }
        }

        viewModel.deleteAccountSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                CustomDialog.showSuccess(
                    requireContext(),
                    "Tu cuenta ha sido eliminada exitosamente"
                ) {
                    navigateToLogin()
                }
            }
        }

        // Observar contadores
        viewModel.getTransactionCount().observe(viewLifecycleOwner) { count ->
            binding.tvTransactionCount.text = count.toString()
        }

        viewModel.getGoalsCount().observe(viewLifecycleOwner) { count ->
            binding.tvGoalsCount.text = count.toString()
        }
    }

    private fun setupListeners() {
        // Personal data
        binding.cardMyData.setOnClickListener {
            showEditProfileDialog()
        }

        // Change password
        binding.cardChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        // Notifications
        binding.cardNotifications.setOnClickListener {
            CustomDialog.showInfo(
                requireContext(),
                "Próximamente",
                "La configuración de notificaciones estará disponible en una próxima actualización."
            )
        }

        // About
        binding.cardAbout.setOnClickListener {
            showAboutDialog()
        }

        // Delete account
        binding.cardDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun getInitials(fullName: String): String {
        if (fullName.isEmpty()) return "?"

        val parts = fullName.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            else -> parts[0].take(2).uppercase()
        }
    }

    private fun showEditProfileDialog() {
        val user = viewModel.user.value ?: return

        InputDialog.showEditNameDialog(
            requireContext(),
            user.fullName
        ) { newName ->
            viewModel.updateUserProfile(newName)
            requireContext().showToast("Perfil actualizado")
        }
    }

    private fun showChangePasswordDialog() {
        val dialog = android.app.Dialog(requireContext())
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        val binding = com.cruzjeff225.gastoapp.databinding.DialogChangePasswordBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        val confirmButton = binding.root.findViewById<android.widget.Button>(com.cruzjeff225.gastoapp.R.id.btnConfirm)
        val cancelButton = binding.root.findViewById<android.widget.Button>(com.cruzjeff225.gastoapp.R.id.btnCancel)

        confirmButton?.setOnClickListener {
            val currentPassword = binding.etCurrentPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            when {
                currentPassword.isEmpty() -> {
                    requireContext().showToast("Ingresa tu contraseña actual")
                }
                newPassword.isEmpty() -> {
                    requireContext().showToast("Ingresa una nueva contraseña")
                }
                newPassword.length < 6 -> {
                    requireContext().showToast("La contraseña debe tener al menos 6 caracteres")
                }
                newPassword != confirmPassword -> {
                    requireContext().showToast("Las contraseñas no coinciden")
                }
                else -> {
                    viewModel.changePassword(currentPassword, newPassword)
                    dialog.dismiss()
                }
            }
        }

        cancelButton?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showLogoutDialog() {
        CustomDialog.showWarning(
            requireContext(),
            "Cerrar Sesión",
            "¿Estás seguro de que quieres cerrar sesión?",
            onConfirm = {
                viewModel.logout()
            }
        )
    }


    private fun showDeleteAccountDialog() {
        InputDialog.showPasswordDialog(
            requireContext(),
            "Eliminar Cuenta",
            "⚠️ Esta acción es permanente y eliminará todos tus datos.\n\nIngresa tu contraseña para confirmar:"
        ) { password ->
            // Show final confirmation
            CustomDialog.showDeleteAccountConfirmation(requireContext()) {
                viewModel.deleteAccount(password)
            }
        }
    }

    private fun showAboutDialog() {
        CustomDialog.showInfo(
            requireContext(),
            "Acerca de GastoApp",
            "Versión 1.0.0\n\nGastoApp es tu compañero ideal para gestionar tus finanzas personales.\n\n"
        )
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}