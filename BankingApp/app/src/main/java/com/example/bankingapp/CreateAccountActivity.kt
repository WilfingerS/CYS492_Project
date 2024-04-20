package com.example.bankingapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


class CreateAccountActivity : AppCompatActivity() {
    private val TAG = "CreateAccountActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account) // Recall of the xml layout resource

        val terms = findViewById<CheckBox>(R.id.Terms)
        terms.setOnClickListener {
            if (!(terms.isChecked)) terms.setTextColor(Color.RED)
            else terms.setTextColor(Color.BLACK)
        }

        // Listener for the Submit Account Button
        findViewById<Button>(R.id.SubmitAccount).setOnClickListener{
            var successful = false // hold result of creating account (no duplicate usernames allowed)
            // ~~~~~(Retrieve passwords (hashes instead of direct text))~~~~~
            val username = findViewById<EditText>(R.id.CreateUsername).text.toString()
            val firstPassword = findViewById<EditText>(R.id.CreatePassword).text.toString()
            val secondPassword = findViewById<EditText>(R.id.ConfirmPassword).text.toString()
            if (firstPassword == secondPassword){
                if (terms.isChecked) successful = true // check terms & conditions
                else terms.setTextColor(Color.RED)
            }
            else Toast.makeText(this, "Password entries do not match", Toast.LENGTH_SHORT).show()
            if (successful) {
                // Get a Cloud Firestore instance
                val db = FirebaseFirestore.getInstance()
                val usersCollection = db.collection("Users")
                val userData = hashMapOf(
                    "balance" to 4000,
                    "name" to username,
                    "password" to firstPassword,
                    "salt" to "pseudoRandomSalt"
                )
                usersCollection.document(username).set(userData)
                    .addOnSuccessListener { Log.d(TAG, "Account successfully created!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error creating account", e) }
                finish () // kill the activity and return to Login
            }
        }

    } // End of onCreate()

} // End of Create Account Activity