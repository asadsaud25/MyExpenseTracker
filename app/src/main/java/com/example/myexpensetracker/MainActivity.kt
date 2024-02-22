package com.example.myexpensetracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions: List<Transaction>
    private lateinit var oldtransactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearlayoutManager: LinearLayoutManager
    private lateinit var myrecyclerView: RecyclerView
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val add = findViewById<FloatingActionButton>(R.id.addbtn)

        transactions = arrayListOf()


        transactionAdapter = TransactionAdapter(transactions)
        linearlayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        myrecyclerView = findViewById(R.id.recyclerView)
        myrecyclerView.apply {
            adapter = transactionAdapter
            layoutManager = linearlayoutManager
        }

        //swipe to delete
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }


        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(myrecyclerView)
        add.setOnClickListener {
            val intent = Intent(this@MainActivity, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchALL() {
        GlobalScope.launch {

            transactions = db.transactionDao().getALL()

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }
    private fun updateDashboard() {
        val totalAmount= transactions.map{it.amount}.sum()
        val budgetAmount = transactions.filter { it.amount>0 }.map{it.amount}.sum()
        val expenseAmount = totalAmount - budgetAmount
        val balance: TextView = findViewById(R.id.currBal)
        val budget: TextView = findViewById(R.id.balance)
        val expense: TextView = findViewById(R.id.expense)

        balance.text = "₹ %.2f/-".format(totalAmount)
        budget.text = "₹ %.2f/-".format(budgetAmount)
        expense.text = "₹ %.2f/-".format(expenseAmount)

    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertALL(deletedTransaction)

            transactions = oldtransactions
            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }
    private fun showSnackbar() {
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view, "Transaction deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }
    private fun deleteTransaction(transaction: Transaction) {
        deletedTransaction = transaction
        oldtransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)

            transactions = transactions.filter {
                it.id != transaction.id
            }
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackbar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchALL()
    }

}