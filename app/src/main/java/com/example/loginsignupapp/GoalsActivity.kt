package com.example.loginsignupapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Goal(val title: String, val target: Double, val deadline: String, val saved: Double = 0.0)

class GoalsActivity : AppCompatActivity() {

    private lateinit var etGoalTitle: EditText
    private lateinit var etGoalAmount: EditText
    private lateinit var etGoalDeadline: EditText
    private lateinit var btnAddGoal: Button
    private lateinit var goalsRecyclerView: RecyclerView

    private val goalsList = mutableListOf<Goal>()
    private lateinit var adapter: GoalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        etGoalTitle = findViewById(R.id.etGoalTitle)
        etGoalAmount = findViewById(R.id.etGoalAmount)
        etGoalDeadline = findViewById(R.id.etGoalDeadline)
        btnAddGoal = findViewById(R.id.btnAddGoal)
        goalsRecyclerView = findViewById(R.id.goalsRecyclerView)

        adapter = GoalsAdapter(goalsList)
        goalsRecyclerView.layoutManager = LinearLayoutManager(this)
        goalsRecyclerView.adapter = adapter

        btnAddGoal.setOnClickListener {
            addGoal()
        }
    }

    private fun addGoal() {
        val title = etGoalTitle.text.toString().trim()
        val amount = etGoalAmount.text.toString().trim().toDoubleOrNull()
        val deadline = etGoalDeadline.text.toString().trim()

        if (title.isEmpty() || amount == null || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val goal = Goal(title, amount, deadline, saved = 0.0)
        goalsList.add(goal)
        adapter.notifyItemInserted(goalsList.size - 1)

        etGoalTitle.text.clear()
        etGoalAmount.text.clear()
        etGoalDeadline.text.clear()
    }
}
