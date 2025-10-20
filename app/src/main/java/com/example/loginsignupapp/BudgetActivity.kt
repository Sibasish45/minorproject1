package com.example.loginsignupapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupapp.adapter.BudgetRecordAdapter
import com.example.loginsignupapp.model.BudgetLimit
import com.example.loginsignupapp.model.BudgetRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BudgetActivity : AppCompatActivity() {

    private lateinit var btnSetBudget: Button
    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnAddExpense: Button
    private lateinit var recordRecyclerView: RecyclerView
    private lateinit var tvBudgetInfo: TextView

    private val recordList = mutableListOf<BudgetRecord>()
    private lateinit var recordAdapter: BudgetRecordAdapter
    private lateinit var budgetLimit: BudgetLimit

    private val auth = FirebaseAuth.getInstance()
    private val userId: String get() = auth.currentUser?.uid ?: "guest"
    private val database = FirebaseDatabase.getInstance()
    private val budgetRef = database.getReference("users/$userId/budget")
    private val recordsRef = database.getReference("users/$userId/records")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        btnSetBudget = findViewById(R.id.btnBudget)
        etAmount = findViewById(R.id.etAmount)
        etNote = findViewById(R.id.note)
        spinnerCategory = findViewById(R.id.spincategory)
        btnAddExpense = findViewById(R.id.btnaddexp)
        recordRecyclerView = findViewById(R.id.recordrecycler)
        tvBudgetInfo = findViewById(R.id.tvBudgetInfo)

        // Spinner setup
        val categories = listOf("Food", "Shopping", "Transport", "Other")
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // RecyclerView setup
        recordAdapter = BudgetRecordAdapter(recordList)
        recordRecyclerView.layoutManager = LinearLayoutManager(this)
        recordRecyclerView.adapter = recordAdapter

        loadBudget()
        loadRecords()

        btnSetBudget.setOnClickListener { showSetBudgetDialog() }
        btnAddExpense.setOnClickListener { addExpense() }
    }

    private fun showSetBudgetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set Monthly Budget")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Enter total budget (₹)"
        builder.setView(input)

        builder.setPositiveButton("Set") { dialog, _ ->
            val amount = input.text.toString().toDoubleOrNull()
            if (amount != null && amount > 0) {
                budgetLimit = BudgetLimit(limit = amount, spent = 0.0)
                budgetRef.setValue(budgetLimit)
                updateBudgetInfo()
            } else {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun addExpense() {
        val amountText = etAmount.text.toString().trim()
        val note = etNote.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()

        if (amountText.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter a valid positive amount", Toast.LENGTH_SHORT).show()
            return
        }

        val record = BudgetRecord(
            amount = amount,
            note = note,
            category = category,
            timestamp = System.currentTimeMillis()
        )
        recordsRef.push().setValue(record)

        budgetLimit.spent = (budgetLimit.spent ?: 0.0) + amount
        budgetRef.setValue(budgetLimit)
        updateBudgetInfo()

        etAmount.text.clear()
        etNote.text.clear()
    }

    private fun loadBudget() {
        budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                budgetLimit = snapshot.getValue(BudgetLimit::class.java) ?: BudgetLimit()
                updateBudgetInfo()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BudgetActivity, "Failed to load budget", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadRecords() {
        recordsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recordList.clear()
                for (child in snapshot.children) {
                    val record = child.getValue(BudgetRecord::class.java)
                    if (record != null) recordList.add(0, record)
                }
                recordAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BudgetActivity, "Failed to load records", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBudgetInfo() {
        val spent = budgetLimit.spent ?: 0.0
        val limit = budgetLimit.limit ?: 0.0
        val remaining = limit - spent
        tvBudgetInfo.text = "Budget: ₹$limit | Spent: ₹$spent | Remaining: ₹$remaining"
    }
}
