package com.example.loginsignupapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupapp.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.tvWelcome.text = "Welcome, ${currentUser.displayName ?: "User"}"
            binding.tvEmail.text = currentUser.email
            loadBudgetData(currentUser.uid)
            loadReminderData(currentUser.uid)
        } else {
            binding.tvWelcome.text = "Welcome, Guest"
            binding.tvEmail.text = ""
        }
    }

    /** Load Budget Info from BudgetActivity data **/
    private fun loadBudgetData(userId: String) {
        val budgetRef = dbRef.child("users").child(userId).child("budget")

        budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val limit = snapshot.child("limit").getValue(Double::class.java) ?: 0.0
                val spent = snapshot.child("spent").getValue(Double::class.java) ?: 0.0

                if (limit > 0) {
                    val percent = ((spent / limit) * 100).toInt()
                    binding.tvBudgetUsed.text = "₹${spent.toInt()} of ₹${limit.toInt()} used"
                    binding.progressBudget.progress = percent
                } else {
                    binding.tvBudgetUsed.text = "No budget data available"
                    binding.progressBudget.progress = 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.tvBudgetUsed.text = "Error loading budget"
            }
        })
    }

    /** Load Reminder Info **/
    private fun loadReminderData(userId: String) {
        val reminderRef = dbRef.child("users").child(userId).child("reminders")

        reminderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var completed = 0
                var pending = 0

                for (data in snapshot.children) {
                    val status = data.child("status").getValue(String::class.java)
                    if (status == "completed") completed++ else pending++
                }

                val total = completed + pending
                if (total > 0) {
                    val percent = (completed.toFloat() / total * 100).toInt()
                    binding.tvRemindersStatus.text = "$completed Completed • $pending Pending"
                    binding.progressReminders.progress = percent
                } else {
                    binding.tvRemindersStatus.text = "No reminders found"
                    binding.progressReminders.progress = 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.tvRemindersStatus.text = "Error loading reminders"
            }
        })
    }
}
