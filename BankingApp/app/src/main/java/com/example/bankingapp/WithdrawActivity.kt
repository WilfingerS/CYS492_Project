package com.example.bankingapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class WithdrawActivity : AppCompatActivity() {

    private val debugTag = "WithdrawActivity"
    // Get a Cloud Firestore instance
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_withdraw)
        val username = intent.getStringExtra("username")?:"Failed Pass"
        val userDocument = userCollection.document(username)

        val withdrawalAmountET = findViewById<EditText>(R.id.WithdrawalAmount)
        findViewById<Button>(R.id.SubmitWithdrawal).setOnClickListener{
            // retrieve user's key
            var symmetricKey: String
            var encryptedBalance: String
            userDocument.get()
            .addOnSuccessListener {result ->
                symmetricKey = result.get("key").toString()

                // Retrieve most recent transaction's ending balance
                // (last document when auto-sorted by date)
                userDocument.collection("TransactionHistory").get()
                .addOnSuccessListener { docs ->
                    // ~~~~~(Retrieve user's currentBalance)~~~~~
                    encryptedBalance = docs.documents
                        .last()?.get("endingBalance").toString()
                    // Decrypt current balance with AES-GCM and user's key
                    val decryptedBalance = aesDecrypt(encryptedBalance, symmetricKey).toDouble()

                    // Retrieve withdrawal value
                    var withdrawalValue = withdrawalAmountET.text.toString().toDoubleOrNull()
                    // ~~~(check if user has sufficient funds to process withdrawal)~~~
                    if (withdrawalValue != null){
                        if (withdrawalValue <= 5000.0) {
                            if (withdrawalValue in 0.01..decryptedBalance) {
                                withdrawalValue = String.format("%.2f",withdrawalValue).toDouble()
                                // only send withdraw data once
                                // current balance and key have been retrieved
                                // and value constraints have been checked
                                tryWithdraw(userDocument, decryptedBalance, withdrawalValue, symmetricKey)
                                //~~~~~(?send confirmation message back to ChooseActionActivity?)~~~~~
                                finish() // kill the activity and return to ChooseActionActivity
                            }
                            else if (withdrawalValue < 0.01) Toast.makeText(this, "Withdrawal minimum is $0.01", Toast.LENGTH_SHORT).show()
                            else Toast.makeText(this, "Insufficient funds for withdrawal", Toast.LENGTH_SHORT).show()
                        }
                        else Toast.makeText(this, "Single withdrawal limit is $5000.00", Toast.LENGTH_SHORT).show()
                    }
                    else Toast.makeText(this, "Withdrawal minimum is $0.01", Toast.LENGTH_SHORT).show()
                } // End of transaction history retrieval SuccessListener
            } // End of key retrieval SuccessListener
        } // End of SubmitDeposit ClickListener
    } // End of onCreate()

    private fun tryWithdraw(userDocument: DocumentReference, decryptedBalance: Double,
                            withdrawalValue: Double, symmetricKey: String){
        val endingBalance = decryptedBalance - withdrawalValue
        val transactionData = hashMapOf(
            "amount" to String.format("%.2f", withdrawalValue),
            "startingBalance" to String.format("%.2f", decryptedBalance),
            "endingBalance" to String.format("%.2f", endingBalance),
        )
        // Encrypt each data entry value and convert byte[] -> contentString
        for (entry in transactionData.entries)
            entry.setValue(aesEncrypt(entry.value, symmetricKey))

        // Store deposit data in new FireBase document and Log the callback result
        userDocument
            .collection("TransactionHistory")
            .document(LocalDateTime.now().toString()).set(transactionData)
            .addOnSuccessListener { Log.d(debugTag, String.format("Successfully withdrawn!" +
                    "\nAmount = %.2f\nBalance = %.2f",withdrawalValue, endingBalance)) }
            .addOnFailureListener { e -> Log.w(debugTag, "Error withdrawing", e) }
        //~~~~~(?send confirmation message back to ChooseActionActivity?)~~~~~
        finish() // kill the activity and return to ChooseActionActivity
    }

    private fun aesEncrypt(data: String, contentKey: String): String {
        val dataBytes = data.toByteArray()
        val keyBytes = contentKey.contentStringToByteArray()
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = "AAAABBBBCCCCDDDD".toByteArray()
        val ivParameterSpec = IvParameterSpec(iv) // 16
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(dataBytes).contentToString()
    }

    private fun aesDecrypt(encryptedData: String, contentKey: String): String {
        val encryptedDataBytes = encryptedData.contentStringToByteArray()
        val keyBytes = contentKey.contentStringToByteArray()
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = "AAAABBBBCCCCDDDD".toByteArray()
        val ivParameterSpec = IvParameterSpec(iv) // 16, Use the same IV as used in encryption?
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(encryptedDataBytes).toString(Charsets.UTF_8) // ??? charset might matter ???
    }

    private fun String.contentStringToByteArray(): ByteArray{
        val newArray = this.removeSurrounding("[", "]").split(", ").toTypedArray()
        val newByteArray = ByteArray(newArray.size)
        for ((index, x) in newArray.withIndex()) { newByteArray[index]= x.toByte()}
        return newByteArray
    }

} // End of Withdraw Activity