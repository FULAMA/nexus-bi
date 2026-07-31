package com.example.nexusbi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nexusbi.data.local.dao.CustomerDao
import com.example.nexusbi.data.local.dao.DebtRecordDao
import com.example.nexusbi.data.local.dao.GroupOrderDao
import com.example.nexusbi.data.local.entities.CustomerEntity
import com.example.nexusbi.data.local.entities.DebtRecordEntity
import com.example.nexusbi.data.local.entities.GroupOrderEntity
import com.example.nexusbi.data.local.entities.GroupOrderPledgeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CustomerEntity::class,
        DebtRecordEntity::class,
        GroupOrderEntity::class,
        GroupOrderPledgeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CarnetDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun debtRecordDao(): DebtRecordDao
    abstract fun groupOrderDao(): GroupOrderDao

    companion object {
        @Volatile
        private var INSTANCE: CarnetDatabase? = null

        fun getDatabase(context: Context): CarnetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarnetDatabase::class.java,
                    "carnet_kredi_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(db: CarnetDatabase) {
                // No default sample data - real user data only
            }
        }
    }
}
