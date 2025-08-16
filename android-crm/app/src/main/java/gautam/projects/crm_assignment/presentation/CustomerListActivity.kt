package gautam.projects.crm_assignment.presentation

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import gautam.projects.crm_assignment.core.sync.SyncStatusNotifier
import gautam.projects.crm_assignment.core.utils.isOnline
import gautam.projects.crm_assignment.data.local.room.CrmDatabase
import gautam.projects.crm_assignment.data.sync.SyncWorker
import gautam.projects.crm_assignment.databinding.ActivityCustomerListBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import android.util.Log

class CustomerListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerListBinding
    private var wasOffline = false // Track offline state
    private var currentSnackbar: Snackbar? = null // Track current snackbar

    private val adapter = CustomerAdapter(
        onItemClicked = { customer ->
            val intent = Intent(this, CustomerDetailActivity::class.java)
            intent.putExtra(CustomerDetailActivity.EXTRA_CUSTOMER_ID, customer.id)
            startActivity(intent)
        },
        onItemLongClicked = { customer ->
            val intent = Intent(this, AddEditCustomerActivity::class.java)
            intent.putExtra(AddEditCustomerActivity.EXTRA_CUSTOMER_ID, customer.id)
            startActivityForResult(intent, ADD_EDIT_REQUEST)
        }
    )

    private lateinit var connectivityManager: ConnectivityManager
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread {
                Log.d("CustomerListActivity", "Network available")

                if (wasOffline) {
                    // Show "Internet reestablished" message
                    currentSnackbar?.dismiss()
                    currentSnackbar = Snackbar.make(
                        binding.root,
                        "Internet reestablished. Sync in progress...",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    currentSnackbar?.show()
                }

                wasOffline = false
                triggerSync()
            }
        }

        override fun onLost(network: Network) {
            runOnUiThread {
                Log.d("CustomerListActivity", "Network lost")
                wasOffline = true

                currentSnackbar?.dismiss()
                currentSnackbar = Snackbar.make(
                    binding.root,
                    "You are offline. Changes will sync when online.",
                    Snackbar.LENGTH_LONG
                )
                currentSnackbar?.show()
            }
        }
    }

    private val ADD_EDIT_REQUEST = 1001
    private val ORDER_REQUEST = 1002

    companion object {
        private const val SYNC_WORK_NAME = "customer_sync_work"
    }

    private fun triggerSync() {
        if (!isOnline(this)) {
            Log.d("CustomerListActivity", "triggerSync: Not online, skipping")
            return
        }

        Log.d("CustomerListActivity", "triggerSync: Enqueuing SyncWorker")
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                syncRequest
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check initial network state
        wasOffline = !isOnline(this)

        val database = CrmDatabase.getInstance(applicationContext)
        val dao = database.crmDao()

        val viewModel: CustomerListViewModel by viewModels {
            CustomerListViewModelFactory(dao)
        }

        // Set up the RecyclerView
        binding.customerRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.customerRecyclerView.adapter = adapter

        // Observe customers
        lifecycleScope.launch {
            viewModel.allCustomers.collect { customers ->
                adapter.submitList(customers)
            }
        }

        // Enhanced sync progress handling
        lifecycleScope.launch {
            SyncStatusNotifier.syncInProgress.collect { syncInProgress ->
                binding.syncProgressBar.isVisible = syncInProgress
                Log.d("CustomerListActivity", "Sync progress: $syncInProgress")

                if (!syncInProgress && currentSnackbar != null) {
                    // Sync completed, dismiss the "sync in progress" message
                    currentSnackbar?.dismiss()
                    currentSnackbar = null
                }
            }
        }

        // Enhanced error handling
        lifecycleScope.launch {
            SyncStatusNotifier.syncError.collectLatest { errorMsg ->
                if (!errorMsg.isNullOrEmpty()) {
                    currentSnackbar?.dismiss()
                    currentSnackbar = Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG)
                        .setAction("Dismiss") {
                            SyncStatusNotifier.clearSyncError()
                            currentSnackbar = null
                        }
                    currentSnackbar?.show()
                }
            }
        }

        // Register network callback
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Initial sync if online
        if (isOnline(this)) {
            triggerSync()
        }


        // FAB for adding customers
        binding.addCustomerFab.setOnClickListener {
            val intent = Intent(this, AddEditCustomerActivity::class.java)
            startActivityForResult(intent, ADD_EDIT_REQUEST)
        }

       
        binding.addCustomerFab.setOnLongClickListener {
            val intent = Intent(this, AddEditOrderActivity::class.java) // You'll need to create this
            startActivityForResult(intent, ORDER_REQUEST)
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == ADD_EDIT_REQUEST || requestCode == ORDER_REQUEST) && resultCode == RESULT_OK) {
            triggerSync()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        currentSnackbar?.dismiss()
    }
}
