package com.example.notesandroidapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val notesList: MutableList<Note>, private val itemClickListener: OnItemClickListener, val context: Context, val onUpdateNavigationListner: OnUpdateNavigationListner) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    val databaseHelper = DatabaseHelper(context)
    interface OnItemClickListener {
        fun onItemClick(note: Note)
    }
    interface OnUpdateNavigationListner{
        fun onClickUpdateNavigate(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.titleTextView.text = currentNote.title
        holder.contentTextView.text = currentNote.content
        holder.deleteButton.setOnClickListener {
            databaseHelper.deleteNote(currentNote.noteId)
            notesList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
        holder.updateNoteButton.setOnClickListener {
            onUpdateNavigationListner.onClickUpdateNavigate(currentNote)
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(currentNote)
        }
    }

    override fun getItemCount() = notesList.size

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewNoteTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.textViewNoteContent)
        val updateNoteButton: Button = itemView.findViewById(R.id.update_button)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }
}