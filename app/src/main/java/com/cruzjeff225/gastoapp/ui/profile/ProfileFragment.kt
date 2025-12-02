package com.cruzjeff225.gastoapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.databinding.FragmentProfileBinding
import com.cruzjeff225.gastoapp.ui.auth.LoginActivity
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
                requireContext().showToast("Cuenta eliminada exitosamente")
                navigateToLogin()
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
            requireContext().showToast("Configuración de notificaciones - Próximamente")
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

        val input = EditText(requireContext()).apply {
            setText(user.fullName)
            hint = "Nombre completo"
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Perfil")
            .setMessage("Ingresa tu nuevo nombre")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    viewModel.updateUserProfile(newName)
                    requireContext().showToast("Perfil actualizado")
                } else {
                    requireContext().showToast("El nombre no puede estar vacío")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = view.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar Contraseña")
            .setView(view)
            .setPositiveButton("Cambiar") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

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
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Cerrar Sesión") { _, _ ->
                viewModel.logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteAccountDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Ingresa tu contraseña"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(50, 30, 50, 30)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Cuenta")
            .setMessage("⚠️ Esta acción es permanente y eliminará todos tus datos.\n\nIngresa tu contraseña para confirmar:")
            .setView(input)
            .setPositiveButton("Eliminar") { _, _ ->
                val password = input.text.toString()
                if (password.isNotEmpty()) {
                    confirmDeleteAccount(password)
                } else {
                    requireContext().showToast("Debes ingresar tu contraseña")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmDeleteAccount(password: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Estás completamente seguro?")
            .setMessage("Se eliminarán:\n• Todas tus transacciones\n• Todas tus metas de ahorro\n• Todos tus datos personales\n\nEsta acción NO se puede deshacer.")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                viewModel.deleteAccount(password)
            }
            .setNegativeButton("No, cancelar", null)
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Acerca de GastoApp")
            .setMessage("Versión 1.0.0\n\nGastoApp es tu compañero ideal para gestionar tus finanzas personales.\n\nDesarrollado con ❤️")
            .setPositiveButton("Cerrar", null)
            .show()
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