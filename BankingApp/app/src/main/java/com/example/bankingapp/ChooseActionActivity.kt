package com.example.bankingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ChooseActionActivity : AppCompatActivity() {

    private val debugTag = "ChooseActionActivity"
    // initialize launcher to use in chooseAction()
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private lateinit var username: String
    private lateinit var pointKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_action) // Recall of the xml layout resource

        username = intent.getStringExtra("username")?:""
        pointKey = intent.getStringExtra("pointKey")?:""
        if (username != "") Log.d(debugTag, "USERNAME = $username")
        if (pointKey != "") Log.d(debugTag, "POINT KEY = $pointKey")

        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK){
                val data = result.data
                val action = data?.getStringExtra("action").toString()
                val value = data?.getDoubleExtra("value", 0.0)
                Log.d(debugTag, "Passed USERNAME as... $username")
                if (value != null) Toast.makeText(this,
                    String.format("Successfully %s $%.02f", action, value), Toast.LENGTH_LONG).show()
            }
        }
    } // End of onCreate()

    fun chooseAction(view: View){
        val actionIntent = when (view) { // choose which intent to send based on Button clicked
            findViewById<Button>(R.id.CheckBalance) -> Intent(this, CheckBalanceActivity::class.java)
            findViewById<Button>(R.id.Deposit) -> Intent(this, DepositActivity::class.java)
            findViewById<Button>(R.id.Withdraw) -> Intent(this, WithdrawActivity::class.java)
            else -> Intent(this, LoginActivity::class.java) // covers Logout Button
        }
            .putExtra("username", username)
            .putExtra("pointKey", pointKey)

        activityLauncher.launch(actionIntent)
    }

} // End of Choose Action Activity