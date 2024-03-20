package com.example.bankingapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateAccountActivity : AppCompatActivity() {
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
            if (terms.isChecked) successful = true // check terms&conditions
            else terms.setTextColor(Color.RED)
            if (successful) finish() // kill the activity and return to Login
        }

    } // End of onCreate()

} // End of Create Account Activity