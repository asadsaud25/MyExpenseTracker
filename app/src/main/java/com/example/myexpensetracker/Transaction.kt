package com.example.myexpensetracker

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id:Int,
    val label: String,
    val amount: Double,
    val description: String) {

}