package gautam.projects.crm_assignment.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import gautam.projects.crm_assignment.data.local.room.CrmDao
import gautam.projects.crm_assignment.data.local.room.Customer
import gautam.projects.crm_assignment.data.local.room.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class CrmRepository(
    private val dao: CrmDao,
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference()
) {

    companion object {
        private const val TIMEOUT_DURATION = 30000L // 30 seconds
        private const val TAG = "CrmRepository"
    }

    // Push Local Customer To Firebase
    suspend fun pushCustomerToFirebase(customers: List<Customer>) {
        try {
            Log.d(TAG, "pushCustomerToFirebase: Starting to push ${customers.size} customers")

            withContext(Dispatchers.IO) {
                withTimeoutOrNull(TIMEOUT_DURATION) {
                    customers.forEachIndexed { index, customer ->
                        Log.d(TAG, "Pushing customer ${index + 1}/${customers.size}: ${customer.id}")
                        try {
                            dbRef.child("customers").child(customer.id.toString())
                                .setValue(customer).await()
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to push customer ${customer.id}: ${e.message}")
                            throw e
                        }
                    }
                } ?: throw Exception("Timeout while pushing customers to Firebase")
            }

            Log.d(TAG, "pushCustomerToFirebase: Successfully pushed all customers")
        } catch (e: Exception) {
            Log.e(TAG, "pushCustomerToFirebase: Error - ${e.message}", e)
            throw e
        }
    }

    // Push local orders to firebase
    suspend fun pushOrdersToFirebase(orders: List<Order>) {
        try {
            Log.d(TAG, "pushOrdersToFirebase: Starting to push ${orders.size} orders")

            withContext(Dispatchers.IO) {
                withTimeoutOrNull(TIMEOUT_DURATION) {
                    orders.forEachIndexed { index, order ->
                        Log.d(TAG, "Pushing order ${index + 1}/${orders.size}: ${order.id}")
                        try {
                            dbRef.child("orders").child(order.id.toString())
                                .setValue(order).await()
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to push order ${order.id}: ${e.message}")
                            throw e
                        }
                    }
                } ?: throw Exception("Timeout while pushing orders to Firebase")
            }

            Log.d(TAG, "pushOrdersToFirebase: Successfully pushed all orders")
        } catch (e: Exception) {
            Log.e(TAG, "pushOrdersToFirebase: Error - ${e.message}", e)
            throw e
        }
    }

    // Pull customers from firebase
    suspend fun pullCustomerFromFirebase() {
        try {
            Log.d(TAG, "pullCustomerFromFirebase: Starting to pull customers")

            withContext(Dispatchers.IO) {
                val customers = withTimeoutOrNull(TIMEOUT_DURATION) {
                    val snapshot = dbRef.child("customers").get().await()
                    Log.d(TAG, "Got Firebase snapshot with ${snapshot.childrenCount} customers")

                    snapshot.children.mapNotNull { childSnapshot ->
                        try {
                            childSnapshot.getValue(Customer::class.java)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse customer: ${childSnapshot.key}, error: ${e.message}")
                            null
                        }
                    }
                } ?: throw Exception("Timeout while pulling customers from Firebase")

                Log.d(TAG, "Parsed ${customers.size} customers, inserting to local DB")
                customers.forEachIndexed { index, customer ->
                    try {
                        dao.insertOrUpdateCustomer(customer)
                        Log.d(TAG, "Inserted customer ${index + 1}/${customers.size}: ${customer.id}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to insert customer ${customer.id}: ${e.message}")
                        throw e
                    }
                }
            }

            Log.d(TAG, "pullCustomerFromFirebase: Successfully completed")
        } catch (e: Exception) {
            Log.e(TAG, "pullCustomerFromFirebase: Error - ${e.message}", e)
            throw e
        }
    }

    // Pull orders from firebase
    suspend fun pullOrdersFromFirebase() {
        try {
            Log.d(TAG, "pullOrdersFromFirebase: Starting to pull orders")

            withContext(Dispatchers.IO) {
                val orders = withTimeoutOrNull(TIMEOUT_DURATION) {
                    val snapshot = dbRef.child("orders").get().await()
                    Log.d(TAG, "Got Firebase snapshot with ${snapshot.childrenCount} orders")

                    snapshot.children.mapNotNull { childSnapshot ->
                        try {
                            childSnapshot.getValue(Order::class.java)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse order: ${childSnapshot.key}, error: ${e.message}")
                            null
                        }
                    }
                } ?: throw Exception("Timeout while pulling orders from Firebase")

                Log.d(TAG, "Parsed ${orders.size} orders, inserting to local DB")
                orders.forEachIndexed { index, order ->
                    try {
                        dao.insertOrUpdateOrder(order)
                        Log.d(TAG, "Inserted order ${index + 1}/${orders.size}: ${order.id}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to insert order ${order.id}: ${e.message}")
                        throw e
                    }
                }
            }

            Log.d(TAG, "pullOrdersFromFirebase: Successfully completed")
        } catch (e: Exception) {
            Log.e(TAG, "pullOrdersFromFirebase: Error - ${e.message}", e)
            throw e
        }
    }
}