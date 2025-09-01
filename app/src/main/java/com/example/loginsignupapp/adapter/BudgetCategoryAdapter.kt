package com.example.loginsignupapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupapp.R
import com.example.loginsignupapp.model.BudgetCategory

class BudgetCategoryAdapter(
    private val context: Context,
    private val categories: List<BudgetCategory>
) : RecyclerView.Adapter<BudgetCategoryAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvSpentAmount: TextView = itemView.findViewById(R.id.tvSpentAmount)
        val progressBar: ProgressBar = itemView.findViewById(R.id.categoryProgressBar)
        val tvBudgetAlert: TextView = itemView.findViewById(R.id.tvBudgetAlert)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val category = categories[position]
        holder.tvCategoryName.text = category.name
        holder.tvSpentAmount.text = "₹${category.spent} / ₹${category.limit}"
        holder.progressBar.progress = category.percentageSpent
        holder.progressBar.progressDrawable.setTint(ContextCompat.getColor(context, category.statusColor))
        holder.tvBudgetAlert.text = category.alertMessage
    }

    override fun getItemCount(): Int = categories.size
}
