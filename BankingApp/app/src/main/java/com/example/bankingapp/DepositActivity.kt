package com.example.bankingapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DepositActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deposit)

        //val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }
        // Listener for Submit Deposit Button
        findViewById<Button>(R.id.SubmitDeposit).setOnClickListener{
            // assume that user has sufficient funds to make deposit
            // ~~~(evaluate and submit deposit)~~~

            // Launcher above and Intent Below could be used for separate confirmation screen
            // but i think it'll be easier to just return to ChooseAction and display a message with...
            finish() // kill the activity and return to ChooseActionActivity

            //val confirmIntent = Intent(this, ConfirmationActivity::class.java)
            //activityLauncher.launch(confirmIntent) //

        }

    } // End of onCreate()

} // End of Deposit Activity