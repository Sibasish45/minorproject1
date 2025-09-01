package com.example.loginsignupapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupapp.databinding.ItemReminderBinding

class ReminderAdapter(
    private val reminders: List<ReminderModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    interface OnItemClickListener {
        fun onDelete(reminderId: String)
        fun onEdit(reminder: ReminderModel)
    }

    inner class ReminderViewHolder(val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: ReminderModel) {
            binding.tvTitle.text = reminder.title
            binding.tvAmount.text = "₹ ${reminder.amount}"
            binding.tvDate.text = "📅 ${reminder.date}"

            binding.btnDelete.setOnClickListener {
                listener.onDelete(reminder.id)
            }

            binding.btnEdit.setOnClickListener {
                listener.onEdit(reminder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemCount() = reminders.size
}
