package com.cruzjeff225.gastoapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cruzjeff225.gastoapp.databinding.ItemCategoryBinding
import com.cruzjeff225.gastoapp.data.model.Category
import androidx.core.graphics.toColorInt

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position == selectedPosition)
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, isSelected: Boolean) {
            with(binding) {
                tvCategoryIcon.text = category.icon
                tvCategoryName.text = category.name

                // Color selected category
                if (isSelected) {
                    categoryBackground.setBackgroundColor(Color.parseColor(category.color))
                    tvCategoryIcon.alpha = 1f
                    tvCategoryName.setTextColor(Color.parseColor(category.color))
                } else {
                    categoryBackground.setBackgroundColor("#F5F5F7".toColorInt())
                    tvCategoryIcon.alpha = 0.7f
                    tvCategoryName.setTextColor("#9CA3AF".toColorInt())
                }

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onCategoryClick(category)
                }
            }
        }
    }
}