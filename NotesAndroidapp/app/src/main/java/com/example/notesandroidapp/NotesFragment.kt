package com.example.notesandroidapp

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class NotesFragment : Fragment(), NoteAdapter.OnItemClickListener, NoteAdapter.OnUpdateNavigationListner {
    lateinit var dbHelper: DatabaseHelper
    lateinit var recyclerView: RecyclerView
    lateinit var noteAdapter: NoteAdapter
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var logoutTextView: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.recyclerViewNotes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        logoutTextView = view.findViewById(R.id.logoutTextView)
        auth = FirebaseAuth.getInstance()
        val text = "Logout"
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle click event
                auth.signOut()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view,GoogleSignIn())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
                Toast.makeText(requireContext(), "Logout clicked", Toast.LENGTH_SHORT).show()
            }
        }
        spannableString.setSpan(clickableSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        logoutTextView.text = spannableString
        logoutTextView.setOnClickListener {
            // Manually handle click event for accessibility support
            clickableSpan.onClick(it)
        }

        // Make TextView clickable
        logoutTextView.isClickable = true
        val addNoteButton: FloatingActionButton = view.findViewById(R.id.buttonAddNote)
        addNoteButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, AddNoteFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
        loadNotes()
    }
    private fun loadNotes() {
        val userId = sharedPreferencesHelper.getUserId() // Replace with actual logic to get the logged-in user's ID
        val notesCursor = dbHelper.getNotes(userId)
        val notesList = mutableListOf<Note>()

        notesCursor?.moveToFirst()
        while (notesCursor?.isAfterLast == false) {
            val noteId = notesCursor.getLong(notesCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_ID))
            val noteTitle = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_TITLE))
            val noteContent = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE_CONTENT))
            notesList.add(Note(noteId.toString(), noteTitle, noteContent))
            notesCursor.moveToNext()
        }
        notesCursor?.close()

        noteAdapter = NoteAdapter(notesList, this,requireContext(),this)
        recyclerView.adapter = noteAdapter
    }
    override fun onItemClick(note: Note) {
        Toast.makeText(requireContext(), "Clicked on: ${note.title}", Toast.LENGTH_SHORT).show()
    }

    override fun onClickUpdateNavigate(note: Note) {
        val updateNoteFragment = UpdateNoteFragment()
        val bundle = Bundle()
        bundle.putString(Constants.NOTE_TITLE,note.title)
        bundle.putString(Constants.NOTE_CONTENT,note.content)
        bundle.putString(Constants.NOTE_ID,note.noteId)
        updateNoteFragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, updateNoteFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}