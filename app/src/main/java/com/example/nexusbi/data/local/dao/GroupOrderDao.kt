package com.example.nexusbi.data.local.dao

import androidx.room.*
import com.example.nexusbi.data.local.entities.GroupOrderEntity
import com.example.nexusbi.data.local.entities.GroupOrderPledgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupOrderDao {
    @Query("SELECT * FROM group_orders ORDER BY deadlineTimestamp ASC")
    fun getAllGroupOrders(): Flow<List<GroupOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupOrder(order: GroupOrderEntity): Long

    @Update
    suspend fun updateGroupOrder(order: GroupOrderEntity)

    @Query("SELECT * FROM group_order_pledges WHERE orderId = :orderId ORDER BY timestamp DESC")
    fun getPledgesForOrder(orderId: Long): Flow<List<GroupOrderPledgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPledge(pledge: GroupOrderPledgeEntity): Long
}
