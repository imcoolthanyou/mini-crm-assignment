package gautam.projects.crm_assignment.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CrmDao {

    // Customer Functions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCustomer(customer: Customer)

    @Query("SELECT * FROM customers ORDER BY id ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Delete
    suspend fun deleteCustomer(customer: Customer)


    // Order Functions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateOrder(order: Order)

    @Query("SELECT * FROM orders WHERE customer_id = :customerId ORDER BY id ASC")
    fun getOrdersByCustomerId(customerId: Int): Flow<List<Order>>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun getCustomerById(customerId: Int): Flow<Customer>

    @Query("SELECT * FROM orders ORDER BY id ASC")
    fun getAllOrders(): Flow<List<Order>>


}
