package com.example.notesandroidapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignIn : Fragment() {
    lateinit var googleSignIn: SignInButton
    lateinit var button: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var viewOfLayout: View
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        } else {
            Toast.makeText(context, "Google sign-in failed.", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        auth = FirebaseAuth.getInstance()
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        // Initialize sharedPreferences
        viewOfLayout = inflater.inflate(R.layout.fragment_google_sign_in, container, false)
        button = viewOfLayout.findViewById(R.id.button)
        googleSignIn = viewOfLayout.findViewById(R.id.btnSignIn)
        googleSignIn.setOnClickListener {
            signIn()
        }
        button.setOnClickListener {
            Toast.makeText(context,"Login to notes Screen",Toast.LENGTH_SHORT).show()
        }
        return viewOfLayout
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Sign in failed
            Log.w("GoogleSignInFragment", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(context, "Google sign-in failed.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    sharedPreferencesHelper.saveUserInfoToSharedPreferences(user)
                    updateUI(user)
                } else {
                    Log.w("GoogleSignInFragment", "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            openNotesFragment()
        } else {
            Toast.makeText(context, "Sign in to access notes", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openNotesFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, NotesFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
}