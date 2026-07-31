package com.example.nexusbi.data.repository

import com.example.nexusbi.data.local.dao.CustomerDao
import com.example.nexusbi.data.local.dao.DebtRecordDao
import com.example.nexusbi.data.local.dao.GroupOrderDao
import com.example.nexusbi.data.local.entities.CustomerEntity
import com.example.nexusbi.data.local.entities.DebtRecordEntity
import com.example.nexusbi.data.local.entities.GroupOrderEntity
import com.example.nexusbi.data.local.entities.GroupOrderPledgeEntity
import kotlinx.coroutines.flow.Flow

class CarnetRepository(
    private val customerDao: CustomerDao,
    private val debtRecordDao: DebtRecordDao,
    private val groupOrderDao: GroupOrderDao
) {
    // Customer operations
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()

    fun getCustomerById(id: Long): Flow<CustomerEntity?> = customerDao.getCustomerById(id)

    suspend fun insertCustomer(customer: CustomerEntity): Long = customerDao.insertCustomer(customer)

    suspend fun updateCustomer(customer: CustomerEntity) = customerDao.updateCustomer(customer)

    suspend fun deleteCustomer(customer: CustomerEntity) {
        debtRecordDao.deleteRecordsForCustomer(customer.id)
        customerDao.deleteCustomer(customer)
    }

    // Debt record operations
    val allRecords: Flow<List<DebtRecordEntity>> = debtRecordDao.getAllRecords()

    fun getRecordsForCustomer(customerId: Long): Flow<List<DebtRecordEntity>> =
        debtRecordDao.getRecordsForCustomer(customerId)

    suspend fun insertRecord(record: DebtRecordEntity): Long = debtRecordDao.insertRecord(record)

    suspend fun updateRecord(record: DebtRecordEntity) = debtRecordDao.updateRecord(record)

    suspend fun deleteRecord(record: DebtRecordEntity) = debtRecordDao.deleteRecord(record)

    // Group order operations
    val allGroupOrders: Flow<List<GroupOrderEntity>> = groupOrderDao.getAllGroupOrders()

    fun getPledgesForOrder(orderId: Long): Flow<List<GroupOrderPledgeEntity>> =
        groupOrderDao.getPledgesForOrder(orderId)

    suspend fun insertGroupOrder(order: GroupOrderEntity): Long = groupOrderDao.insertGroupOrder(order)

    suspend fun pledgeToOrder(pledge: GroupOrderPledgeEntity, currentOrder: GroupOrderEntity) {
        groupOrderDao.insertPledge(pledge)
        val updatedQty = currentOrder.currentQuantity + pledge.quantity
        val updatedStatus = if (updatedQty >= currentOrder.targetQuantity) "FUNDED" else currentOrder.status
        groupOrderDao.updateGroupOrder(
            currentOrder.copy(
                currentQuantity = updatedQty,
                status = updatedStatus
            )
        )
    }
}
