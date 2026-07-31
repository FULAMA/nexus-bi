package com.example.nexusbi.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debt_records")
data class DebtRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val amount: Double,
    val itemDescription: String,
    val date: Long = System.currentTimeMillis(),
    val isPaid: Boolean = false,
    val amountPaid: Double = 0.0,
    val type: String = "CREDIT", // "CREDIT" or "PAYMENT"
    val dueDate: Long? = null,
    val reminderSentCount: Int = 0
)
