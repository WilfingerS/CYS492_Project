package com.example.bankingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    // initialize launcher to use in tryLogin()
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login) // Recall of the xml layout resource

    } // End of onCreate()

    fun tryLogin(view: View) {
        val tryUsername = findViewById<EditText>(R.id.Username_et).text.toString()
        val tryPassword = findViewById<EditText>(R.id.Password_et).text.toString()
        val tryPassHash = /* add salt and do hash thingy */ (tryPassword)
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("Users")
        usersCollection.document(tryUsername).get()
            .addOnSuccessListener { result ->
                var dbPassHash = "placeholder"
                if (result.exists()) dbPassHash = result.getField<String>("password").toString()
                Log.d(TAG, "Password retrieval successful, = $dbPassHash")
                if (dbPassHash == tryPassHash) {
                    val loginIntent = Intent(this, ChooseActionActivity::class.java)
                    activityLauncher.launch(loginIntent)
                } else Toast.makeText(this, "Username or Password is incorrect", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Something went wrong. \nPlease try again.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "Password retrieval failed with...", exception)
            }
    }
    fun createAccount(view: View){
        val loginIntent = Intent(this, CreateAccountActivity::class.java)
        activityLauncher.launch(loginIntent)
    }
//        var verified = false
//        if (verified) {
//            val loginIntent = when (view) { // choose which intent to send based on Button clicked
//                findViewById<Button>(R.id.Login) -> Intent(this, ChooseActionActivity::class.java)
//                findViewById<Button>(R.id.CreateAccount) -> Intent(
//                    this,
//                    CreateAccountActivity::class.java
//                )
//
//                else -> Intent(this, CreateAccountActivity::class.java)
//            }
//            activityLauncher.launch(loginIntent)
//        }

} // End of Login Activity