package com.example.myexpensetracker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddTransactionActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val add_tansaction = findViewById<Button>(R.id.add_transaction_btn)
        val labelInput = findViewById<TextInputEditText>(R.id.labelInput)
        val amountInput = findViewById<TextInputEditText>(R.id.amountInput)
        val descriptionInput = findViewById<TextInputEditText>(R.id.descriptionInput)
        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val descriptionLayout = findViewById<TextInputLayout>(R.id.descriptionLayout)
        val close = findViewById<ImageButton>(R.id.closebtn)

        labelInput.addTextChangedListener {
            if(it!!.count() > 0)
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            if(it!!.count() > 0)
                amountLayout.error = null
        }

        add_tansaction.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if(label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if(amount == null)
                amountLayout.error = "Please enter valid amount"
            else{
                val transaction = Transaction(0, label, amount, description)
                insert(transaction)
            }
        }

        close.setOnClickListener {
            finish()
        }
    }

    private fun insert(transaction: Transaction) {
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertALL(transaction)
            finish()
        }
    }


}