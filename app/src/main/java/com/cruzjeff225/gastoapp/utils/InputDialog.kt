package com.cruzjeff225.gastoapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.Window
import com.cruzjeff225.gastoapp.databinding.DialogInputBinding

class InputDialog {

    companion object {
        fun showAddMoneyDialog(
            context: Context,
            goalName: String,
            currentAmount: Double,
            remainingAmount: Double,
            onConfirm: (Double) -> Unit
        ) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val binding = DialogInputBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)

            // Setup
            binding.tvIcon.text = "💰"
            binding.iconBackground.setBackgroundColor(Color.parseColor("#E8F5E9"))
            binding.tvTitle.text = "Agregar dinero a $goalName"
            binding.tvMessage.text = "Monto actual: $${String.format("%,.0f", currentAmount)}\n" +
                    "Falta: $${String.format("%,.0f", remainingAmount)}"
            binding.etInput.hint = "Monto a agregar"
            binding.etInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            binding.btnNegative.setOnClickListener {
                dialog.dismiss()
            }

            binding.btnPositive.setOnClickListener {
                val amountText = binding.etInput.text.toString()
                val amount = amountText.toDoubleOrNull()

                if (amount != null && amount > 0) {
                    onConfirm(amount)
                    dialog.dismiss()
                } else {
                    binding.etInput.error = "Ingresa un monto válido"
                }
            }

            dialog.show()
        }

        fun showEditNameDialog(
            context: Context,
            currentName: String,
            onConfirm: (String) -> Unit
        ) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val binding = DialogInputBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)

            // Setup
            binding.tvIcon.text = "✏️"
            binding.iconBackground.setBackgroundColor(Color.parseColor("#F0E7FF"))
            binding.tvTitle.text = "Editar Perfil"
            binding.tvMessage.text = "Ingresa tu nuevo nombre"
            binding.etInput.hint = "Nombre completo"
            binding.etInput.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            binding.etInput.setText(currentName)

            binding.btnPositive.text = "Guardar"

            binding.btnNegative.setOnClickListener {
                dialog.dismiss()
            }

            binding.btnPositive.setOnClickListener {
                val newName = binding.etInput.text.toString().trim()

                if (newName.isNotEmpty()) {
                    onConfirm(newName)
                    dialog.dismiss()
                } else {
                    binding.etInput.error = "El nombre no puede estar vacío"
                }
            }

            dialog.show()
        }

        fun showPasswordDialog(
            context: Context,
            title: String,
            message: String,
            onConfirm: (String) -> Unit
        ) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val binding = DialogInputBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)

            // Setup
            binding.tvIcon.text = "🔒"
            binding.iconBackground.setBackgroundColor(Color.parseColor("#FFEBEE"))
            binding.tvTitle.text = title
            binding.tvMessage.text = message
            binding.etInput.hint = "Ingresa tu contraseña"
            binding.etInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            binding.btnPositive.text = "Confirmar"
            binding.btnPositive.setBackgroundColor(Color.parseColor("#EF4444"))

            binding.btnNegative.setOnClickListener {
                dialog.dismiss()
            }

            binding.btnPositive.setOnClickListener {
                val password = binding.etInput.text.toString()

                if (password.isNotEmpty()) {
                    onConfirm(password)
                    dialog.dismiss()
                } else {
                    binding.etInput.error = "Debes ingresar tu contraseña"
                }
            }

            dialog.show()
        }

        fun showGenericInputDialog(
            context: Context,
            icon: String,
            iconBackgroundColor: String,
            title: String,
            message: String,
            hint: String,
            inputType: Int = InputType.TYPE_CLASS_TEXT,
            initialValue: String = "",
            positiveButtonText: String = "Aceptar",
            onConfirm: (String) -> Unit
        ) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val binding = DialogInputBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)

            // Setup
            binding.tvIcon.text = icon
            try {
                binding.iconBackground.setBackgroundColor(Color.parseColor(iconBackgroundColor))
            } catch (e: Exception) {
                binding.iconBackground.setBackgroundColor(Color.parseColor("#F3F4F6"))
            }
            binding.tvTitle.text = title
            binding.tvMessage.text = message
            binding.etInput.hint = hint
            binding.etInput.inputType = inputType
            if (initialValue.isNotEmpty()) {
                binding.etInput.setText(initialValue)
            }
            binding.btnPositive.text = positiveButtonText

            binding.btnNegative.setOnClickListener {
                dialog.dismiss()
            }

            binding.btnPositive.setOnClickListener {
                val input = binding.etInput.text.toString().trim()

                if (input.isNotEmpty()) {
                    onConfirm(input)
                    dialog.dismiss()
                } else {
                    binding.etInput.error = "Este campo no puede estar vacío"
                }
            }

            dialog.show()
        }
    }
}