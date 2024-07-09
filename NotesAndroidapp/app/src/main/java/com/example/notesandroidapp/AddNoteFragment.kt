package com.example.notesandroidapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentTransaction

class AddNoteFragment : Fragment() {
    private lateinit var view: View
    lateinit var button: Button

    lateinit var dbHelper: DatabaseHelper
    lateinit var editTextNoteTitle: EditText
    lateinit var editTextNoteContent: EditText
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_add_note, container, false)
        editTextNoteTitle = view.findViewById(R.id.editTextNoteTitle)
        editTextNoteContent = view.findViewById(R.id.editTextNoteContent)
        dbHelper = DatabaseHelper(requireContext())
        button = view.findViewById(R.id.buttonSaveNote)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        button.setOnClickListener {
            val title = editTextNoteTitle.text.toString().trim()
            val content = editTextNoteContent.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val userId = sharedPreferencesHelper.getUserId() // Replace with actual logic to get the logged-in user's ID
                dbHelper.addNote(title, content, userId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, NotesFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
            }
        }
        return view
    }

}