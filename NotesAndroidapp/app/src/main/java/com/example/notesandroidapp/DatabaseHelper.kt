package com.example.notesandroidapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "notesApp.db"
        private const val TABLE_USER = "user"
        private const val TABLE_NOTE = "note"

        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_NAME = "name"
        private const val COLUMN_USER_EMAIL = "email"

        const val COLUMN_NOTE_ID = "note_id"
        const val COLUMN_NOTE_TITLE = "title"
        const val COLUMN_NOTE_CONTENT = "content"
        private const val COLUMN_NOTE_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = ("CREATE TABLE $TABLE_USER ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_NAME TEXT, "
                + "$COLUMN_USER_EMAIL TEXT)")

        val createNoteTable = ("CREATE TABLE $TABLE_NOTE ("
                + "$COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NOTE_TITLE TEXT, "
                + "$COLUMN_NOTE_CONTENT TEXT, "
                + "$COLUMN_NOTE_USER_ID INTEGER, "
                + "FOREIGN KEY($COLUMN_NOTE_USER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID))")

        db.execSQL(createUserTable)
        db.execSQL(createNoteTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun addUser(name: String, email: String): Long {
        val db = this.writableDatabase

        // Check if user already exists
        val cursor = db.query(TABLE_USER, arrayOf(COLUMN_USER_ID), "$COLUMN_USER_EMAIL=?", arrayOf(email), null, null, null)
        val userExists = cursor.count > 0
        cursor.close()

        if (userExists) {
            db.close()
            return -1 // Return -1 to indicate the user already exists
        }

        val values = ContentValues()
        values.put(COLUMN_USER_NAME, name)
        values.put(COLUMN_USER_EMAIL, email)

        val success = db.insert(TABLE_USER, null, values)
        db.close()
        return success
    }

    fun getUser(email: String): Cursor? {
        val db = this.readableDatabase
        return db.query(TABLE_USER, arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_EMAIL), "$COLUMN_USER_EMAIL=?", arrayOf(email), null, null, null)
    }

    fun addNote(title: String, content: String, userId: String?): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE_TITLE, title)
        values.put(COLUMN_NOTE_CONTENT, content)
        values.put(COLUMN_NOTE_USER_ID, userId)

        val success = db.insert(TABLE_NOTE, null, values)
        db.close()
        return success
    }

    fun getNotes(userId: String?): Cursor? {
        val db = this.readableDatabase
        return db.query(TABLE_NOTE, arrayOf(COLUMN_NOTE_ID, COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT), "$COLUMN_NOTE_USER_ID=?", arrayOf(userId.toString()), null, null, null)
    }

    fun updateNote(noteId: String, title: String, content: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE_TITLE, title)
        values.put(COLUMN_NOTE_CONTENT, content)

        val success = db.update(TABLE_NOTE, values, "$COLUMN_NOTE_ID=?", arrayOf(noteId.toString()))
        db.close()
        return success
    }

    fun deleteNote(noteId: String): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NOTE, "$COLUMN_NOTE_ID=?", arrayOf(noteId.toString()))
        db.close()
        return success
    }
}
