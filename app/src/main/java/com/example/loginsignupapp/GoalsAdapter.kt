package com.example.loginsignupapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GoalsAdapter(private val goals: List<Goal>) :
    RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goalTitleText: TextView = itemView.findViewById(R.id.goalTitleText)
        val goalAmountText: TextView = itemView.findViewById(R.id.goalAmountText)
        val goalProgressBar: ProgressBar = itemView.findViewById(R.id.goalProgressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.goalTitleText.text = "${goal.title} (Deadline: ${goal.deadline})"
        holder.goalAmountText.text = "Saved ₹${goal.saved} of ₹${goal.target}"

        val progress = if (goal.target > 0) ((goal.saved / goal.target) * 100).toInt() else 0
        holder.goalProgressBar.progress = progress
    }

    override fun getItemCount(): Int = goals.size
}
