package com.example.bankingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ChooseActionActivity : AppCompatActivity() {

    private val debugTag = "ChooseActionActivity"
    // initialize launcher to use in chooseAction()
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

    private lateinit var username: String
    private lateinit var contentKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_action) // Recall of the xml layout resource

        username = intent.getStringExtra("username")?: "Username Pass Failed"
        Log.d(debugTag, "USERNAME = $username")

    } // End of onCreate()

    fun chooseAction(view: View){
        val actionIntent = when (view) { // choose which intent to send based on Button clicked
            findViewById<Button>(R.id.CheckBalance) -> { Intent(this, CheckBalanceActivity::class.java)
                    .putExtra("username", username) }
            findViewById<Button>(R.id.Deposit) -> Intent(this, DepositActivity::class.java)
            findViewById<Button>(R.id.Withdraw) -> Intent(this, WithdrawActivity::class.java)
            else -> Intent(this, LoginActivity::class.java) // covers Logout Button
        }
        activityLauncher.launch(actionIntent)
    }

} // End of Choose Action Activity