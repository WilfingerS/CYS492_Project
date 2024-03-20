package com.example.bankingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WithdrawActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_withdraw)

        //val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }
        // Listener for Submit Withdrawal Button
        findViewById<Button>(R.id.SubmitWithdrawal).setOnClickListener{
            // ~~~(check if user has sufficient funds to process withdrawal)~~~
            val successful = true
            if (successful) {
                // similar to SubmitDeposit Listener in DepositActivity where i think
                // it'll be easier to return and show a message than launch a new activity + Intent
                finish() // kill the activity and return to ChooseActionActivity
                //val confirmIntent = Intent(this, ConfirmationActivity::class.java)
                //activityLauncher.launch(confirmIntent)
            }
        }

    } // End of onCreate()

} // End of Withdraw Activity