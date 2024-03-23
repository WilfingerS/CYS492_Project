package com.example.bankingapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DepositActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deposit)

        val depositAmount = findViewById<EditText>(R.id.DepositAmount)
        findViewById<Button>(R.id.SubmitDeposit).setOnClickListener{
            // assume that user has sufficient funds to make deposit
            val depositValue = depositAmount.text.toString().toDoubleOrNull()
            // ~~~(evaluate and submit deposit)~~~
            if (depositValue != null){
                if (depositValue in 0.01..5000.0) {
                    //~~~~~(add deposit amount to account balance)~~~~~
                    //~~~~~(send confirmation message back to ChooseActionActivity)~~~~~
                    finish() // kill the activity and return to ChooseActionActivity
                }
                else if (5000.0 < depositValue) Toast.makeText(this, "Single deposit limit is $5,000.00", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "Deposit minimum is $0.01", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(this, "Deposit minimum is $0.01", Toast.LENGTH_SHORT).show()
        }

    } // End of onCreate()

} // End of Deposit Activity