package com.example.loginsignupapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupapp.R
import com.example.loginsignupapp.model.BudgetCategory

class BudgetCategoryAdapter(
    private val context: Context,
    private val budgetList: List<BudgetCategory>
) : RecyclerView.Adapter<BudgetCategoryAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvBudgetAmount: TextView = itemView.findViewById(R.id.tvBudgetAmount)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.budget_item, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgetList[position]

        holder.tvCategoryName.text = budget.name

        val progress = if (budget.limit > 0) ((budget.spent / budget.limit) * 100).toInt() else 0
        holder.progressBar.progress = progress

        // Use string resource with placeholders
        holder.tvBudgetAmount.text = context.getString(R.string.budget_amount, budget.spent, budget.limit)

        // Use toColorInt for colors
        val color = when {
            progress >= 90 -> Color.RED
            progress >= 70 -> "#FFA500".toColorInt() // Orange
            else -> "#4CAF50".toColorInt() // Green
        }
        holder.progressBar.progressDrawable.setTint(color)
    }

    override fun getItemCount(): Int = budgetList.size
}
