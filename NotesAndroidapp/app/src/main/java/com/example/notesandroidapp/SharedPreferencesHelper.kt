package com.example.notesandroidapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser

class SharedPreferencesHelper(context: Context) {
    val context = context
    val dbHelper: DatabaseHelper = DatabaseHelper(context)

    private val preferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    fun getUserId(): String? {
        return preferences.getString(Constants.USER_ID, null)
    }

    fun saveUserId(userId: Long) {
        preferences.edit().putLong("userId", userId).apply()
    }

    fun clearUserId() {
        preferences.edit().remove("userId").apply()
    }

    fun saveUserInfoToSharedPreferences(user: FirebaseUser?) {
        user?.let {
            val editor = preferences.edit()
            editor.putString(Constants.USER_ID, user.uid)
            editor.putString(Constants.USER_NAME, user.displayName)
            editor.putString(Constants.USER_EMAIL, user.email)
            editor.putString(Constants.USER_PHOTO_URL, user.photoUrl.toString())
            editor.apply()
        }
        val userId = dbHelper.addUser(user?.displayName!!, user.email!!)
        if (userId == -1L) {
            Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Database", "User added with ID: $userId")
        }
    }
}