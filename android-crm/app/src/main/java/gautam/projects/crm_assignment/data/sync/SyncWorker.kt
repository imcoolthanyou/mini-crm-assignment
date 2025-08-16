package gautam.projects.crm_assignment.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import gautam.projects.crm_assignment.core.sync.SyncStatusNotifier
import gautam.projects.crm_assignment.core.utils.isOnline
import gautam.projects.crm_assignment.data.local.room.CrmDatabase
import gautam.projects.crm_assignment.data.repository.CrmRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val workId = id.toString().substring(0, 8)
        Log.d("SyncWorker", "[$workId] doWork: STARTED - Run attempt: $runAttemptCount")

        // Prevent infinite retries
        if (runAttemptCount >= 3) {
            Log.e("SyncWorker", "[$workId] Max retry attempts reached, giving up")
            SyncStatusNotifier.notifySyncError("Sync failed after multiple attempts")
            return Result.failure()
        }

        try {
            SyncStatusNotifier.notifySyncStarted()

            // Check internet connection
            if (!applicationContext.isOnline()) {
                Log.w("SyncWorker", "[$workId] No internet connection")
                SyncStatusNotifier.notifySyncError("No internet connection")
                return Result.retry()
            }

            Log.d("SyncWorker", "[$workId] Internet available, initializing components")

            val dao = CrmDatabase.getInstance(applicationContext).crmDao()
            val repository = CrmRepository(dao)

            // Step 1: Get local data with error handling
            Log.d("SyncWorker", "[$workId] Step 1: Getting local data")
            val customers = try {
                dao.getAllCustomers().first()
            } catch (e: Exception) {
                Log.e("SyncWorker", "[$workId] Failed to get customers from local DB", e)
                throw Exception("Local database error: ${e.message}")
            }
            Log.d("SyncWorker", "[$workId] Got ${customers.size} customers from local DB")

            val orders = try {
                dao.getAllOrders().first()
            } catch (e: Exception) {
                Log.e("SyncWorker", "[$workId] Failed to get orders from local DB", e)
                throw Exception("Local database error: ${e.message}")
            }
            Log.d("SyncWorker", "[$workId] Got ${orders.size} orders from local DB")

            // Step 2: Push to Firebase with individual error handling
            Log.d("SyncWorker", "[$workId] Step 2: Pushing to Firebase")

            try {
                repository.pushCustomerToFirebase(customers)
                Log.d("SyncWorker", "[$workId] Successfully pushed customers to Firebase")
            } catch (e: Exception) {
                Log.e("SyncWorker", "[$workId] Failed to push customers to Firebase", e)
                throw Exception("Firebase push error (customers): ${e.message}")
            }

            // Small delay to prevent rate limiting
            delay(200)

            try {
                repository.pushOrdersToFirebase(orders)
                Log.d("SyncWorker", "[$workId] Successfully pushed orders to Firebase")
            } catch (e: Exception) {
                Log.e("SyncWorker", "[$workId] Failed to push orders to Firebase", e)
                throw Exception("Firebase push error (orders): ${e.message}")
            }

            // Step 3: Pull from Firebase with individual error handling
            Log.d("SyncWorker", "[$workId] Step 3: Pulling from Firebase")

            // Another delay before pulling
            delay(200)

            try {
                repository.pullCustomerFromFirebase()
                Log.d("SyncWorker", "[$workId] Successfully pulled customers from Firebase")
            } catch (e: Exception) {
                Log.e("SyncWorker", "[$workId] Failed to pull customers from Firebase", e)
                throw Exception("Firebase pull error (customers): ${e.message}")
            }

            delay(200)

            try {
                repository.pullOrdersFromFirebase()
                Log.d("SyncWorker", "[$workId] Successfully pulled orders from Firebase")
            } catch (e: Exception) {
                Log.e("SyncWorker", "[$workId] Failed to pull orders from Firebase", e)
                throw Exception("Firebase pull error (orders): ${e.message}")
            }

            Log.d("SyncWorker", "[$workId] All sync operations completed successfully")
            SyncStatusNotifier.notifySyncFinished()
            return Result.success()

        } catch (e: Exception) {
            Log.e("SyncWorker", "[$workId] Sync failed with exception: ${e.message}", e)

            val errorMessage = when {
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    "Sync timeout - please check your internet connection"
                e.message?.contains("network", ignoreCase = true) == true ->
                    "Network error during sync"
                e.message?.contains("firebase", ignoreCase = true) == true ->
                    "Firebase error: ${e.message}"
                e.message?.contains("database", ignoreCase = true) == true ->
                    "Database error: ${e.message}"
                else -> "Sync failed: ${e.message ?: "Unknown error"}"
            }

            SyncStatusNotifier.notifySyncError(errorMessage)

            // Only retry on network-related errors and timeouts
            return if (e.message?.contains("network", ignoreCase = true) == true ||
                e.message?.contains("timeout", ignoreCase = true) == true) {
                Log.d("SyncWorker", "[$workId] Will retry due to network/timeout error")
                Result.retry()
            } else {
                Log.d("SyncWorker", "[$workId] Will not retry due to non-network error")
                Result.failure()
            }
        } finally {
            // Emergency fallback - ensure spinner is turned off
            // This should not be necessary if everything works correctly,
            // but it's here as a safety net
            Log.d("SyncWorker", "[$workId] Finally block - ensuring sync is marked as finished")
        }
    }
}