package com.example.loginsignupapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DocumentAdapter(
    private val documents: List<File>,
    private val onViewClick: (File) -> Unit,
    private val onDeleteClick: (File) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtDocumentName)
        val btnView: ImageView = itemView.findViewById(R.id.btnView)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val file = documents[position]
        holder.txtName.text = file.name

        holder.btnView.setOnClickListener {
            onViewClick(file)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(file)
        }
    }

    override fun getItemCount(): Int = documents.size
}
