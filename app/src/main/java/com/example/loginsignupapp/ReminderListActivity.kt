package com.example.loginsignupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginsignupapp.databinding.ActivityReminderListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReminderListActivity : AppCompatActivity(), ReminderAdapter.OnItemClickListener {

    private lateinit var binding: ActivityReminderListBinding
    private lateinit var reminderList: ArrayList<ReminderModel>
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reminderList = ArrayList()
        adapter = ReminderAdapter(reminderList, this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().getReference("expense_reminders")

        // Fetch user-specific reminders
        database.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reminderList.clear()
                    for (reminderSnap in snapshot.children) {
                        val reminder = reminderSnap.getValue(ReminderModel::class.java)
                        if (reminder != null) {
                            reminderList.add(reminder)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ReminderListActivity, "Failed to fetch reminders", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDelete(reminderId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Reminder")
            .setMessage("Are you sure you want to delete this reminder?")
            .setPositiveButton("Yes") { _, _ ->
                database.child(reminderId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onEdit(reminder: ReminderModel) {
        val intent = Intent(this, ReminderActivity::class.java)
        intent.putExtra("reminderId", reminder.id)
        intent.putExtra("title", reminder.title)
        intent.putExtra("amount", reminder.amount.toString())
        intent.putExtra("date", reminder.date)
        startActivity(intent)
    }
}
