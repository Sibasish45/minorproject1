package com.example.loginsignupapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ReminderActivity : AppCompatActivity(), ReminderAdapter.OnItemClickListener {

    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var btnSave: Button
    private lateinit var recyclerView: RecyclerView

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    private val reminders = mutableListOf<ReminderModel>()
    private lateinit var adapter: ReminderAdapter
    private val calendar = Calendar.getInstance()

    private var editingReminderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        btnSave = findViewById(R.id.btnSave)
        recyclerView = findViewById(R.id.reminderRecyclerView)

        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid ?: return
        databaseRef = FirebaseDatabase.getInstance().getReference("expense_reminders").child(userId)

        adapter = ReminderAdapter(reminders, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        etDate.setOnClickListener { showDatePicker() }

        btnSave.setOnClickListener {
            if (editingReminderId == null) saveReminder()
            else updateReminder()
        }

        loadRemindersRealtime()
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            etDate.setText(format.format(calendar.time))
        }
        DatePickerDialog(
            this, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveReminder() {
        val title = etTitle.text.toString().trim()
        val amount = etAmount.text.toString().trim()
        val date = etDate.text.toString().trim()

        if (title.isEmpty() || amount.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val reminderId = databaseRef.push().key ?: return
        val reminder = ReminderModel(reminderId, firebaseAuth.currentUser!!.uid, title, amount.toDouble(), date)

        databaseRef.child(reminderId).setValue(reminder)
            .addOnSuccessListener {
                Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show()
                scheduleNotification(title)
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateReminder() {
        val title = etTitle.text.toString().trim()
        val amount = etAmount.text.toString().trim()
        val date = etDate.text.toString().trim()

        if (editingReminderId == null) return

        val updatedReminder = ReminderModel(editingReminderId!!, firebaseAuth.currentUser!!.uid, title, amount.toDouble(), date)

        databaseRef.child(editingReminderId!!).setValue(updatedReminder)
            .addOnSuccessListener {
                Toast.makeText(this, "Reminder updated", Toast.LENGTH_SHORT).show()
                editingReminderId = null
                btnSave.text = "Save"
                clearFields()
            }
    }

    private fun loadRemindersRealtime() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reminders.clear()
                for (child in snapshot.children) {
                    val reminder = child.getValue(ReminderModel::class.java)
                    if (reminder != null) reminders.add(0, reminder)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReminderActivity, "Failed to load reminders", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearFields() {
        etTitle.text.clear()
        etAmount.text.clear()
        etDate.text.clear()
    }

    private fun scheduleNotification(title: String) {
        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("title", title)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + 30000 // Example: 30 seconds later
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    // 🔥 Real-time delete and edit via Adapter
    override fun onDelete(reminderId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Reminder")
            .setMessage("Are you sure you want to delete this reminder?")
            .setPositiveButton("Yes") { _, _ ->
                databaseRef.child(reminderId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onEdit(reminder: ReminderModel) {
        etTitle.setText(reminder.title)
        etAmount.setText(reminder.amount.toString())
        etDate.setText(reminder.date)
        editingReminderId = reminder.id
        btnSave.text = "Update"
    }
}
