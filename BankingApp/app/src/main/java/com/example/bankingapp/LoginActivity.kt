package com.example.bankingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    // initialize launcher to use in tryLogin()
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login) // Recall of the xml layout resource

    } // End of onCreate()

    fun tryLogin(view: View){
        val password = findViewById<EditText>(R.id.Password)
        val dbSaltedHash = password // ~~~~~(retrieve password hash from database)~~~~~
        var verified = (password == dbSaltedHash) // substitute for password verification process
        if (verified){
            val loginIntent = when (view) { // choose which intent to send based on Button clicked
                findViewById<Button>(R.id.Login) -> Intent(this, ChooseActionActivity::class.java)
                findViewById<Button>(R.id.CreateAccount) -> Intent(this, CreateAccountActivity::class.java)
                else -> Intent(this, CreateAccountActivity::class.java)
            }
            activityLauncher.launch(loginIntent)
        }
    }

} // End of Login Activity