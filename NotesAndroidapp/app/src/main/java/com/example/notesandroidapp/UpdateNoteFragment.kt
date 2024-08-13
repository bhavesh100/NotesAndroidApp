package com.example.notesandroidapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentTransaction

class UpdateNoteFragment : Fragment() {
    lateinit var editTextTitle: EditText
    lateinit var editTextContent: EditText
    lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val databaseHelper = DatabaseHelper(requireContext())
        val noteId = arguments?.getString(Constants.NOTE_ID)
        val noteTitle = arguments?.getString(Constants.NOTE_TITLE)
        val noteContent = arguments?.getString(Constants.NOTE_CONTENT)

        val view = inflater.inflate(R.layout.fragment_update_note, container, false)

        editTextContent = view.findViewById(R.id.editTextNoteContent1)
        editTextTitle = view.findViewById(R.id.editTextNoteTitle1)
        updateButton = view.findViewById(R.id.buttonUpdateNote)

        editTextTitle.setText(noteTitle)
        editTextContent.setText(noteContent)

        updateButton.setOnClickListener {
            var isValid = true

            if (editTextTitle.text.toString().trim().isEmpty()) {
                // Display an error message on the title field
                editTextTitle.error = "Title cannot be empty"
                isValid = false
            } else {
                editTextTitle.error = null
            }

            if (editTextContent.text.toString().trim().isEmpty()) {
                // Display an error message on the content field
                editTextContent.error = "Content cannot be empty"
                isValid = false
            } else {
                editTextContent.error = null
            }

            if (isValid) {
                databaseHelper.updateNote(
                    noteId.toString(),
                    editTextTitle.text.toString(),
                    editTextContent.text.toString()
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, NotesFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
            }
        }

        return view
    }
}
