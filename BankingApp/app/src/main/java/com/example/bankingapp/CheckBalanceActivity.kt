package com.example.bankingapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class CheckBalanceActivity : AppCompatActivity() {

    private val debugTag = "CheckBalanceActivity"
    // Get a Firestore Instance
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("Users")
    private val cipher = Cipher.getInstance("AES/GCM/NoPadding")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check_balance)

        val username = intent.getStringExtra("username")?:"Username Pass Failed"
        val pointKey = intent.getStringExtra("pointKey")?:""
        if (username != "")Log.d(debugTag, "USERNAME = $username")
        if (pointKey != "") Log.d(debugTag, "POINT KEY = $pointKey")


        usersCollection.document(username).collection("TransactionHistory").get()
        .addOnSuccessListener { result ->
            // Retrieve balance, dbKey, and contentKey for E/D
            val balance = result.documents.last().get("endingBalance").toString()
            val encryptedBalanceString = balance.toByteArray().toString(Charsets.UTF_16) // only for Log display
            Log.d(debugTag, "Balance ENCRYPTED as... $encryptedBalanceString")
            val decryptedBalance = aesDecrypt(balance, pointKey, username)
            findViewById<TextView>(R.id.CurrentBalance).text = "$$decryptedBalance"
            Log.d(debugTag, "Balance DECRYPTED as... $decryptedBalance")
        }
        .addOnFailureListener {
                e -> Log.w(debugTag, "Error decrypting balance", e)
        }
    } // End of onCreate()


//                usersCollection.document(username).get()
//                    .addOnSuccessListener { passResult ->
//                        // retrieve key from Firebase (maybe not safe to store there but oh well)
//                        val dbKey = passResult.get("key").toString()
//                        Log.d(debugTag, "Key retrieved as... $dbKey")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.d(debugTag, "Key retrieval failed with... ", e) }

    private fun aesDecrypt(encryptedData: String, contentKey: String, username: String): String {
        val encryptedDataBytes = encryptedData.contentStringToByteArray()
        val keyBytes = contentKey.contentStringToByteArray()
        val secretKey = SecretKeySpec(keyBytes, "AES")
//        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = username.toByteArray(Charsets.UTF_8)
        val ivParameterSpec = IvParameterSpec(iv) // Use the same IV as used in encryption
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(encryptedDataBytes).toString(Charsets.UTF_8)
    }

    private fun String.contentStringToByteArray(): ByteArray{
        val newArray = this.removeSurrounding("[", "]").split(", ").toTypedArray()
        val newByteArray = ByteArray(newArray.size)
        for ((index, x) in newArray.withIndex()) { newByteArray[index]= x.toByte()}
        return newByteArray
    }
} // End of Check Balance Activity