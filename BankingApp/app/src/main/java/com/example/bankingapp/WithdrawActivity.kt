package com.example.bankingapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class WithdrawActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_withdraw)

        val withdrawalAmount = findViewById<EditText>(R.id.WithdrawalAmount)

        findViewById<Button>(R.id.SubmitWithdrawal).setOnClickListener{
            // Retrieve withdrawal value
            val withdrawalValue = withdrawalAmount.text.toString().toDoubleOrNull()
            // ~~~~~(Retrieve user's currentBalance)~~~~~
            var currentBalance = 4000.00 // just a placeholder
            // ~~~(check if user has sufficient funds to process withdrawal)~~~
            if (withdrawalValue != null){
                if (withdrawalValue <= 5000.0) {
                    if (withdrawalValue in 0.01..currentBalance) {
                        //~~~~~(subtract withdrawal amount from account balance)~~~~~
                        currentBalance -= withdrawalValue
                        //~~~~~(send confirmation message back to ChooseActionActivity)~~~~~
                        Toast.makeText(this, String.format("Withdrawal complete. Balance = %.2f", currentBalance), Toast.LENGTH_SHORT).show()
                        finish() // kill the activity and return to ChooseActionActivity
                    }
                    else if (withdrawalValue < 0.01) Toast.makeText(this, "Withdrawal minimum is $0.01", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this, "Insufficient funds for withdrawal", Toast.LENGTH_SHORT).show()
                }
                else Toast.makeText(this, "Single withdrawal limit is $5000.00", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(this, "Withdrawal minimum is $0.01", Toast.LENGTH_SHORT).show()
        }

    } // End of onCreate()

} // End of Withdraw Activity