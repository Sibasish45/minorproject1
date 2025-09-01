package com.example.loginsignupapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupapp.adapter.BudgetCategoryAdapter
import com.example.loginsignupapp.adapter.BudgetRecordAdapter
import com.example.loginsignupapp.model.BudgetCategory
import com.example.loginsignupapp.model.BudgetRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BudgetActivity : AppCompatActivity() {
    private lateinit var btnSetBudget: Button
    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnAddIncome: Button
    private lateinit var btnAddExpense: Button
    private lateinit var budgetRecyclerView: RecyclerView
    private lateinit var recordRecyclerView: RecyclerView

    private val budgetList = mutableListOf<BudgetCategory>()
    private val recordList = mutableListOf<BudgetRecord>()

    private lateinit var budgetAdapter: BudgetCategoryAdapter
    private lateinit var recordAdapter: BudgetRecordAdapter

    // Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId: String get() = auth.currentUser?.uid ?: "guest"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        // Initialize Views
        btnSetBudget = findViewById(R.id.btnBudget)
        etAmount = findViewById(R.id.etAmount)
        etNote = findViewById(R.id.note)
        spinnerCategory = findViewById(R.id.spincategory)
        btnAddIncome = findViewById(R.id.btnaddinc)
        btnAddExpense = findViewById(R.id.btnaddexp)
        budgetRecyclerView = findViewById(R.id.budrecyclerview)
        recordRecyclerView = findViewById(R.id.recordrecycler)

        // Spinner setup
        val categories = listOf("Food", "Shopping", "Transport", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        // RecyclerView setup
        budgetAdapter = BudgetCategoryAdapter(this, budgetList)
        recordAdapter = BudgetRecordAdapter(recordList)
        budgetRecyclerView.layoutManager = LinearLayoutManager(this)
        recordRecyclerView.layoutManager = LinearLayoutManager(this)
        budgetRecyclerView.adapter = budgetAdapter
        recordRecyclerView.adapter = recordAdapter

        // Load existing data from Firebase
        loadBudgets()
        loadRecords()

        // Button click listeners
        btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        btnAddIncome.setOnClickListener {
            addRecord(isIncome = true)
        }

        btnAddExpense.setOnClickListener {
            addRecord(isIncome = false)
        }
    }

    private fun showSetBudgetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set Budget Limit")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Enter amount (₹)"
        builder.setView(input)

        builder.setPositiveButton("Set") { dialog, _ ->
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val amount = input.text.toString().toDoubleOrNull()
            if (amount != null && amount > 0) {
                val existing = budgetList.find { it.name == selectedCategory }
                if (existing != null) {
                    existing.limit = amount
                    existing.spent = 0.0
                    updateBudgetInFirestore(existing)
                } else {
                    val newBudget = BudgetCategory(selectedCategory, amount, 0.0)
                    budgetList.add(newBudget)
                    saveBudgetToFirestore(newBudget)
                }
                budgetAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addRecord(isIncome: Boolean) {
        val amountText = etAmount.text.toString().trim()
        val note = etNote.text.toString().trim()
        val selectedCategory = spinnerCategory.selectedItem.toString()

        if (amountText.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Enter a valid positive amount", Toast.LENGTH_SHORT).show()
            return
        }

        val record = BudgetRecord(amount, note, isIncome, selectedCategory)
        recordList.add(0, record)
        recordAdapter.notifyItemInserted(0)
        saveRecordToFirestore(record)

        // Update spent in budget if it's an expense
        if (!isIncome) {
            val budget = budgetList.find { it.name == selectedCategory }
            if (budget != null) {
                budget.spent += amount
                budgetAdapter.notifyDataSetChanged()
                updateBudgetInFirestore(budget)
            }
        }

        etAmount.text.clear()
        etNote.text.clear()
    }

    // ---------------- Firebase Methods ----------------

    private fun saveBudgetToFirestore(budget: BudgetCategory) {
        firestore.collection("users").document(userId)
            .collection("budgets").document(budget.name)
            .set(budget)
    }

    private fun updateBudgetInFirestore(budget: BudgetCategory) {
        firestore.collection("users").document(userId)
            .collection("budgets").document(budget.name)
            .set(budget)
    }

    private fun saveRecordToFirestore(record: BudgetRecord) {
        firestore.collection("users").document(userId)
            .collection("records").add(record)
    }

    private fun loadBudgets() {
        firestore.collection("users").document(userId)
            .collection("budgets")
            .get()
            .addOnSuccessListener { snapshot ->
                budgetList.clear()
                for (doc in snapshot) {
                    val budget = doc.toObject(BudgetCategory::class.java)
                    budgetList.add(budget)
                }
                budgetAdapter.notifyDataSetChanged()
            }
    }

    private fun loadRecords() {
        firestore.collection("users").document(userId)
            .collection("records")
            .get()
            .addOnSuccessListener { snapshot ->
                recordList.clear()
                for (doc in snapshot) {
                    val record = doc.toObject(BudgetRecord::class.java)
                    recordList.add(0, record)
                }
                recordAdapter.notifyDataSetChanged()
            }
    }
}
