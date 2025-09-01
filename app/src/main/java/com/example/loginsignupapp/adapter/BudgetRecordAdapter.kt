package com.example.loginsignupapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupapp.R
import com.example.loginsignupapp.model.BudgetRecord

class BudgetRecordAdapter(
    private val records: List<BudgetRecord>
) : RecyclerView.Adapter<BudgetRecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.tvAmount.text = "₹${record.amount}"
        holder.tvNote.text = record.note
        holder.tvType.text = if (record.isIncome) "Income" else "Expense"
    }

    override fun getItemCount(): Int = records.size
}
