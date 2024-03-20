package com.example.bankingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChooseActionActivity : AppCompatActivity() {

    // initialize launcher to use in chooseAction()
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_action) // Recall of the xml layout resource

    } // End of onCreate()

    fun chooseAction(view: View){
        val actionIntent = when (view) { // choose which intent to send based on Button clicked
            findViewById<Button>(R.id.CheckBalance) -> Intent(this, CheckBalanceActivity::class.java)
            findViewById<Button>(R.id.Deposit) -> Intent(this, DepositActivity::class.java)
            findViewById<Button>(R.id.Withdraw) -> Intent(this, WithdrawActivity::class.java)
            else -> Intent(this, LoginActivity::class.java) // covers Logout Button
        }
        activityLauncher.launch(actionIntent)
    }

} // End of Choose Action Activity