package com.example.nexusbi.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val neighborhood: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
