package com.cruzjeff225.gastoapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cruzjeff225.gastoapp.databinding.ItemIconSelectorBinding
import androidx.core.graphics.toColorInt

class IconAdapter(
    private val icons: List<Pair<Int, String>>,
    private val onIconClick: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val binding = ItemIconSelectorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(icons[position].first, position == selectedPosition)
    }

    override fun getItemCount() = icons.size

    fun setSelectedIcon(iconRes: Int) {
        val position = icons.indexOfFirst { it.first == iconRes }
        if (position != -1) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    inner class IconViewHolder(
        private val binding: ItemIconSelectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(iconRes: Int, isSelected: Boolean) {
            with(binding) {
                ivIcon.setImageResource(iconRes)

                // Highlight selected icon
                if (isSelected) {
                    iconContainer.setBackgroundColor("#E8F5E9".toColorInt())
                    iconContainer.elevation = 4f
                } else {
                    iconContainer.setBackgroundColor("#F5F5F7".toColorInt())
                    iconContainer.elevation = 0f
                }

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onIconClick(iconRes)
                }
            }
        }
    }
}