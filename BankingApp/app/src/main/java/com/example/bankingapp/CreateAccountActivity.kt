package com.example.bankingapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.time.LocalDateTime
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs
import kotlin.math.log10


class CreateAccountActivity : AppCompatActivity() {
    private val debugTag = "CreateAccountActivity"
    // Get a Cloud Firestore instance
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("Users")


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
            var successful = false // hold result of password match verification
            // retrieve passwords
            val usernameET = findViewById<EditText>(R.id.CreateUsername_et)
            val username = usernameET.text.toString()
            usernameET.hideKeyboard()
            val firstPasswordET = findViewById<EditText>(R.id.CreatePassword_et)
            val firstPassword = firstPasswordET.text.toString()
            firstPasswordET.hideKeyboard()
            val secondPasswordET = findViewById<EditText>(R.id.ConfirmPassword_et)
            val secondPassword = secondPasswordET.text.toString()
            secondPasswordET.hideKeyboard()

            if (firstPassword == secondPassword){ // verify that entered passwords match
                if (terms.isChecked) successful = true // check terms & conditions
                else terms.setTextColor(Color.RED)
            }
            else Toast.makeText(this, "Password entries do not match", Toast.LENGTH_SHORT).show()

            if (successful) {
                val salt = createSalt()
                val passHash = doHashAndSalt(firstPassword, salt)
                val pointHash = doHashAndSalt(username+firstPassword, salt)
                //val symmetricKey = generateAESKey()

                // create 2-out-of-2 secret sharing points
                val userX = passHash.contentStringToInt()
                val userY = pointHash.contentStringToInt()
                val randX = SecureRandom(passHash.contentStringToByteArray()).nextInt()
                val randY = SecureRandom(pointHash.contentStringToByteArray()).nextInt()
                // calculate the shared secret (y-Intercept)
                val slope = (randY-userY).toDouble()/(randX-userX) // m = (y2-y1)/(x2-x1)
                val yIntercept = (randY-slope*randX).coerceIn(-1.0E15+1, 1.0E15-1) // b = y - mx
                val leadingDigits = log10(abs(yIntercept)).toInt()+1
                // format secret into 16-Byte content string
                val pointKey = String.format("%.0${15-leadingDigits}f",abs(yIntercept))
                    .padStart(16,'0')
                    .toByteArray(Charsets.UTF_8).contentToString()
                Log.d(debugTag, "Slope = $slope\n" +
                        "Y-Intercept = $yIntercept\n" +
                        "Point Key = ${pointKey.contentStringToByteArray().toString(Charsets.UTF_8)}")

                // send username and key back to LoginActivity for convenience and testing
                val resultIntent = Intent().putExtra("username", username)
                    .putExtra("pointKey", pointKey)

                // Store user data in FireBase and Log the callback result
                val userData = hashMapOf(
                    "salt" to salt,
                    "password" to passHash,
                    "pointX" to "$randX",
                    "pointY" to "$randY"
                    //"key" to symmetricKey,
                )
                usersCollection.document(username).set(userData)
                    .addOnSuccessListener { Log.d(debugTag,
                        "Account successfully created!\n" +
                                "Password Salted and Hashed as... " +
                                passHash.contentStringToByteArray().toString(Charsets.UTF_8)
                    ) }
                    .addOnFailureListener { e -> Log.w(debugTag, "Error creating account", e) }

                val initialBalanceData = hashMapOf(
                    "amount" to "4000.00",
                    "startingBalance" to "0.00",
                    "endingBalance" to "4000.00",
                )
                // Encrypt each data entry value
                for (entry in initialBalanceData.entries)
                    entry.setValue(aesEncrypt(entry.value, pointKey, username))

                // Store balance data in FireBase and Log the callback result
                usersCollection.document(username)
                    .collection("TransactionHistory")
                        .document(LocalDateTime.now().toString()).set(initialBalanceData)
                            .addOnSuccessListener { Log.d(debugTag, "Initial balance successfully deposited!") }
                            .addOnFailureListener { e -> Log.w(debugTag, "Error depositing balance", e) }

                setResult(RESULT_OK, resultIntent)
                finish () // kill the activity, return to Login with result info
            }
        }

    } // End of onCreate()

    private fun createSalt(): String {
        val sr: SecureRandom
        val salt = ByteArray(16)
        try {
            sr = SecureRandom.getInstanceStrong()
            sr.nextBytes(salt)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return salt.contentToString()
    }

    private fun doHashAndSalt(plaintext: String, contentSalt: String): String {
        val saltBytes = contentSalt.contentStringToByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        md.update(saltBytes)
        return md.digest(plaintext.toByteArray()).contentToString()
    }

//    private fun generateAESKey(): String {
//        val keySize = 128
//        val keyGenerator = KeyGenerator.getInstance("AES")
//        keyGenerator.init(keySize)
//        return keyGenerator.generateKey().encoded.contentToString()
//    }

    private fun aesEncrypt(data: String, contentKey: String, username: String): String {
        val dataBytes = data.toByteArray()
        val keyBytes = contentKey.contentStringToByteArray()
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = username.toByteArray(Charsets.UTF_8)
        val ivParameterSpec = IvParameterSpec(iv) // 16
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(dataBytes).contentToString()
    }

    private fun String.contentStringToByteArray(): ByteArray{
        val newArray = this.removeSurrounding("[", "]").split(", ").toTypedArray()
        val newByteArray = ByteArray(newArray.size)
        for ((index, x) in newArray.withIndex()) { newByteArray[index]= x.toByte()}
        return newByteArray
    }

    private fun String.contentStringToInt(): Int{
        val newArray = this.removeSurrounding("[", "]").split(", ").toTypedArray()
        var newValue = 1
        for (x in newArray) newValue *= x.toInt()
        return newValue
    }

    private fun View.hideKeyboard() {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }

} // End of Create Account Activity