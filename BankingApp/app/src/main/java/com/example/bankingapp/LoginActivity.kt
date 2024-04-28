package com.example.bankingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private val debugTag = "LoginActivity"
    // Get a Firestore instance
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("Users")

    // initialize launcher to use in tryLogin()
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login) // Recall of the xml layout resource

        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                val data = result.data
                username = data?.getStringExtra("username")?: "Username Pass Failed"
                Log.d(debugTag, "USERNAME = $username")
            }
        }
    } // End of onCreate()

    fun tryLogin(view: View) {
        val tryUsernameET = findViewById<EditText>(R.id.Username_et)
        val tryUsername = tryUsernameET.text.toString()
        tryUsernameET.hideKeyboard()
        val tryPasswordET = findViewById<EditText>(R.id.Password_et)
        val tryPassword = tryPasswordET.text.toString()
        tryPasswordET.hideKeyboard()

        // Get username and password from Firestore to authenticate user
        usersCollection.document(tryUsername).get()
            .addOnSuccessListener { snapshot ->
                val dbSalt = snapshot.getField<String>("salt")?: "Salt Retreival Failed"
                val dbPassHash = snapshot.getField<String>("password")?: "Password Retrieval Failed"
                Log.d(debugTag, "Password retrieved as... $dbPassHash")

                /* get user's salt from database and do hash thingy */
                val tryPassHash = doHashAndSalt(tryPassword, dbSalt)

                if (tryPassHash == dbPassHash) {
                    val loginIntent = Intent(this, ChooseActionActivity::class.java)
                        .putExtra("username", username)
                    activityLauncher.launch(loginIntent)
                }
                else Toast.makeText(this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Something went wrong. \nPlease try again.", Toast.LENGTH_SHORT).show()
                Log.d(debugTag, "Password retrieval failed with...", exception)
            }
    }

    fun createAccount(view: View){
        val createAccountIntent = Intent(this, CreateAccountActivity::class.java)
        activityLauncher.launch(createAccountIntent)
    }

    private fun doHashAndSalt(plaintext: String, contentSalt: String): String {
        val saltBytes = contentSalt.contentStringToByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        md.update(saltBytes)
        return md.digest(plaintext.toByteArray()).contentToString()
//        val sb = StringBuilder()
//        for (aByte in bytes) {
//            sb.append(((aByte.toInt() and 0xff) + 0x100).toString(16).substring(1))
//        }
//        return sb.toString()
    }

    private fun String.contentStringToByteArray(): ByteArray{
        val newArray = this.removeSurrounding("[", "]").split(", ").toTypedArray()
        val newByteArray = ByteArray(newArray.size)
        for ((index, x) in newArray.withIndex()) { newByteArray[index]= x.toByte()}
        return newByteArray
    }

    private fun View.hideKeyboard() {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }

} // End of Login Activity