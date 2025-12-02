package com.cruzjeff225.gastoapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cruzjeff225.gastoapp.databinding.ItemColorSelectorBinding

class ColorAdapter(
    private val colors: List<Pair<String, String>>
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private var selectedPosition = 0

    val selectedColor: String
        get() = if (colors.isNotEmpty()) colors[selectedPosition].first else ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorSelectorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position].first, position == selectedPosition)
    }

    override fun getItemCount() = colors.size

    fun setSelectedColor(colorHex: String) {
        val position = colors.indexOfFirst { it.first == colorHex }
        if (position != -1) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    inner class ColorViewHolder(
        private val binding: ItemColorSelectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(colorHex: String, isSelected: Boolean) {
            with(binding) {
                // Set color
                viewColor.setBackgroundColor(Color.parseColor(colorHex))

                // Show/hide selected indicator
                tvSelected.visibility = if (isSelected) View.VISIBLE else View.GONE

                // Add elevation for selected
                if (isSelected) {
                    colorContainer.elevation = 4f
                } else {
                    colorContainer.elevation = 0f
                }

                root.setOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        val previousPosition = selectedPosition
                        selectedPosition = bindingAdapterPosition
                        notifyItemChanged(previousPosition)
                        notifyItemChanged(selectedPosition)
                    }
                }
            }
        }
    }
}
