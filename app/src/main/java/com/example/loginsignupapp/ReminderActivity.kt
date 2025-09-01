package com.example.loginsignupapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnView: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        btnSave = findViewById(R.id.btnSave)
        btnView = findViewById(R.id.btnViewReminders)

        firebaseAuth = FirebaseAuth.getInstance()

        // Open calendar when date field is clicked
        etDate.setOnClickListener {
            showDatePicker()
        }

        // Save reminder to Firebase and trigger notification
        btnSave.setOnClickListener {
            saveReminder()
        }

        // View all reminders
        btnView.setOnClickListener {
            startActivity(Intent(this, ReminderListActivity::class.java))
        }
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            etDate.setText(format.format(calendar.time))
        }

        DatePickerDialog(
            this,
            dateSetListener,
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

        val reminderId = FirebaseDatabase.getInstance().reference.push().key ?: ""
        val userId = firebaseAuth.currentUser?.uid ?: return
        val reminder = ReminderModel(reminderId, userId, title, amount.toDouble(), date)

        FirebaseDatabase.getInstance().getReference("expense_reminders")
            .child(reminderId)
            .setValue(reminder)
            .addOnSuccessListener {
                Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show()
                scheduleNotification(title)
                etTitle.text.clear()
                etAmount.text.clear()
                etDate.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_SHORT).show()
            }
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
        val triggerTime = System.currentTimeMillis() + 30000 // 30 seconds
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}
