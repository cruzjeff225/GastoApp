package com.cruzjeff225.gastoapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.cruzjeff225.gastoapp.databinding.DialogCustomBinding

class CustomDialog(private val context: Context) {

    data class Builder(
        private val context: Context,
        private var title: String = "",
        private var message: String = "",
        private var icon: String = "⚠️",
        private var iconBackgroundColor: String = "#FEF3C7",
        private var positiveButtonText: String = "Aceptar",
        private var negativeButtonText: String = "Cancelar",
        private var positiveButtonColor: String = "#7C3AED",
        private var negativeButtonColor: String = "#6B7280",
        private var onPositiveClick: (() -> Unit)? = null,
        private var onNegativeClick: (() -> Unit)? = null,
        private var showNegativeButton: Boolean = true,
        private var cancelable: Boolean = true
    ) {

        fun title(title: String) = apply { this.title = title }
        fun message(message: String) = apply { this.message = message }
        fun icon(icon: String) = apply { this.icon = icon }
        fun iconBackgroundColor(color: String) = apply { this.iconBackgroundColor = color }
        fun positiveButtonText(text: String) = apply { this.positiveButtonText = text }
        fun negativeButtonText(text: String) = apply { this.negativeButtonText = text }
        fun positiveButtonColor(color: String) = apply { this.positiveButtonColor = color }
        fun negativeButtonColor(color: String) = apply { this.negativeButtonColor = color }
        fun onPositiveClick(action: () -> Unit) = apply { this.onPositiveClick = action }
        fun onNegativeClick(action: () -> Unit) = apply { this.onNegativeClick = action }
        fun showNegativeButton(show: Boolean) = apply { this.showNegativeButton = show }
        fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }

        fun build(): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val binding = DialogCustomBinding.inflate(LayoutInflater.from(context))
            dialog.setContentView(binding.root)
            dialog.setCancelable(cancelable)

            // Set dialog width to 90% of screen width
            dialog.window?.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Set icon
            binding.tvIcon.text = icon
            try {
                binding.iconBackground.setBackgroundColor(Color.parseColor(iconBackgroundColor))
            } catch (e: Exception) {
                binding.iconBackground.setBackgroundColor(Color.parseColor("#F3F4F6"))
            }

            // Set title and message
            binding.tvTitle.text = title
            binding.tvMessage.text = message

            // Set positive button
            binding.btnPositive.text = positiveButtonText
            try {
                binding.btnPositive.setBackgroundColor(Color.parseColor(positiveButtonColor))
            } catch (e: Exception) {
                binding.btnPositive.setBackgroundColor(Color.parseColor("#7C3AED"))
            }

            binding.btnPositive.setOnClickListener {
                onPositiveClick?.invoke()
                dialog.dismiss()
            }

            // Set negative button
            if (showNegativeButton) {
                binding.btnNegative.visibility = android.view.View.VISIBLE
                binding.btnNegative.text = negativeButtonText
                try {
                    binding.btnNegative.setBackgroundColor(Color.parseColor(negativeButtonColor))
                } catch (e: Exception) {
                    binding.btnNegative.setBackgroundColor(Color.parseColor("#6B7280"))
                }

                binding.btnNegative.setOnClickListener {
                    onNegativeClick?.invoke()
                    dialog.dismiss()
                }
            } else {
                binding.btnNegative.visibility = android.view.View.GONE
            }

            return dialog
        }

        fun show(): Dialog {
            val dialog = build()
            dialog.show()
            return dialog
        }
    }

    companion object {
        // Preset dialog types for common scenarios

        fun showDeleteConfirmation(
            context: Context,
            itemName: String,
            onConfirm: () -> Unit
        ) {
            Builder(context)
                .title("Eliminar")
                .message("¿Estás seguro de que quieres eliminar \"$itemName\"?")
                .icon("🗑️")
                .iconBackgroundColor("#FFEBEE")
                .positiveButtonText("Eliminar")
                .positiveButtonColor("#EF4444")
                .negativeButtonText("Cancelar")
                .onPositiveClick(onConfirm)
                .show()
        }

        fun showSuccess(
            context: Context,
            message: String,
            onDismiss: (() -> Unit)? = null
        ) {
            Builder(context)
                .title("¡Éxito!")
                .message(message)
                .icon("✅")
                .iconBackgroundColor("#E8F5E9")
                .positiveButtonText("Aceptar")
                .positiveButtonColor("#10B981")
                .showNegativeButton(false)
                .onPositiveClick { onDismiss?.invoke() }
                .show()
        }

        fun showError(
            context: Context,
            message: String,
            onDismiss: (() -> Unit)? = null
        ) {
            Builder(context)
                .title("Error")
                .message(message)
                .icon("❌")
                .iconBackgroundColor("#FFEBEE")
                .positiveButtonText("Entendido")
                .positiveButtonColor("#EF4444")
                .showNegativeButton(false)
                .onPositiveClick { onDismiss?.invoke() }
                .show()
        }

        fun showWarning(
            context: Context,
            title: String,
            message: String,
            onConfirm: () -> Unit,
            onCancel: (() -> Unit)? = null
        ) {
            Builder(context)
                .title(title)
                .message(message)
                .icon("⚠️")
                .iconBackgroundColor("#FEF3C7")
                .positiveButtonText("Continuar")
                .positiveButtonColor("#F59E0B")
                .negativeButtonText("Cancelar")
                .onPositiveClick(onConfirm)
                .onNegativeClick { onCancel?.invoke() }
                .show()
        }

        fun showInfo(
            context: Context,
            title: String,
            message: String,
            onDismiss: (() -> Unit)? = null
        ) {
            Builder(context)
                .title(title)
                .message(message)
                .icon("ℹ️")
                .iconBackgroundColor("#E3F2FD")
                .positiveButtonText("Entendido")
                .positiveButtonColor("#3B82F6")
                .showNegativeButton(false)
                .onPositiveClick { onDismiss?.invoke() }
                .show()
        }

        fun showDeleteAccountConfirmation(
            context: Context,
            onConfirm: () -> Unit
        ) {
            Builder(context)
                .title("¿Estás completamente seguro?")
                .message("Se eliminarán:\n• Todas tus transacciones\n• Todas tus metas de ahorro\n• Todos tus datos personales\n\nEsta acción NO se puede deshacer.")
                .icon("🚨")
                .iconBackgroundColor("#FFEBEE")
                .positiveButtonText("Sí, eliminar")
                .positiveButtonColor("#EF4444")
                .negativeButtonText("No, cancelar")
                .onPositiveClick(onConfirm)
                .show()
        }
    }
}