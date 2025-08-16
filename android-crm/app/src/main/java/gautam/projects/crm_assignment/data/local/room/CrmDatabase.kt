package gautam.projects.crm_assignment.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Customer::class, Order::class], version = 1, exportSchema = false) // 1. List all entities
abstract class CrmDatabase : RoomDatabase() {

    abstract fun crmDao(): CrmDao // 2. Provide the DAO

    // 3. This Singleton Object will be used to get a reference to the database
    companion object {
        @Volatile
        private var INSTANCE: CrmDatabase? = null

        fun getInstance(context: Context): CrmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CrmDatabase::class.java,
                    "crm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}