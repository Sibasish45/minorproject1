package com.example.loginsignupapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class DocumentVaultActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DocumentAdapter
    private val documents = mutableListOf<File>()

    private val PICK_DOCUMENT_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_vault)

        recyclerView = findViewById(R.id.recyclerViewDocuments)
        val btnAdd = findViewById<Button>(R.id.btnAddDocument)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DocumentAdapter(documents,
            onViewClick = { openDocument(it) },
            onDeleteClick = { deleteDocument(it) }
        )
        recyclerView.adapter = adapter

        loadDocuments()

        btnAdd.setOnClickListener {
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // allow all types
        }
        startActivityForResult(intent, PICK_DOCUMENT_REQUEST)
    }

    private fun loadDocuments() {
        val dir = filesDir
        documents.clear()
        dir?.listFiles()?.forEach {
            if (it.isFile) {
                documents.add(it)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun deleteDocument(file: File) {
        if (file.delete()) {
            Toast.makeText(this, "Deleted ${file.name}", Toast.LENGTH_SHORT).show()
            loadDocuments()
        } else {
            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDocument(file: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, contentResolver.getType(uri) ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No app found to open this file type.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return
            saveFileToInternalStorage(uri)
        }
    }

    private fun saveFileToInternalStorage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val fileName = uri.path?.split("/")?.last() ?: "document_${System.currentTimeMillis()}"
            val outputFile = File(filesDir, fileName)

            val outputStream = FileOutputStream(outputFile)
            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            Toast.makeText(this, "Saved $fileName", Toast.LENGTH_SHORT).show()
            loadDocuments()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show()
        }
    }
}
