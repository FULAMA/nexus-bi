package com.example.nexusbi.data.local.dao

import androidx.room.*
import com.example.nexusbi.data.local.entities.DebtRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtRecordDao {
    @Query("SELECT * FROM debt_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<DebtRecordEntity>>

    @Query("SELECT * FROM debt_records WHERE customerId = :customerId ORDER BY date DESC")
    fun getRecordsForCustomer(customerId: Long): Flow<List<DebtRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DebtRecordEntity): Long

    @Update
    suspend fun updateRecord(record: DebtRecordEntity)

    @Delete
    suspend fun deleteRecord(record: DebtRecordEntity)

    @Query("DELETE FROM debt_records WHERE customerId = :customerId")
    suspend fun deleteRecordsForCustomer(customerId: Long)
}
