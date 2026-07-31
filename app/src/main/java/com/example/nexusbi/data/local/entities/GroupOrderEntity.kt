package com.example.nexusbi.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_orders")
data class GroupOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val supplierName: String,
    val regularUnitPrice: Double,
    val groupUnitPrice: Double,
    val unitName: String, // e.g., "sac 50kg", "carton", "bidon 25L"
    val targetQuantity: Int,
    val currentQuantity: Int = 0,
    val deadlineTimestamp: Long,
    val status: String = "OPEN", // "OPEN", "FUNDED", "DELIVERED"
    val category: String = "Alimentaire"
)

@Entity(tableName = "group_order_pledges")
data class GroupOrderPledgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val boutiqueName: String,
    val phone: String,
    val quantity: Int,
    val timestamp: Long = System.currentTimeMillis()
)
