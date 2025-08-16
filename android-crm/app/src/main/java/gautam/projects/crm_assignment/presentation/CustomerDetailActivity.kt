package gautam.projects.crm_assignment.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import gautam.projects.crm_assignment.data.local.room.CrmDatabase
import gautam.projects.crm_assignment.data.local.room.Customer

import gautam.projects.crm_assignment.databinding.ActivityCustomerDetailBinding
import kotlinx.coroutines.launch

class CustomerDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerDetailBinding
    private val orderAdapter = OrderAdapter()
    private var currentCustomer: Customer? = null // Variable to hold the current customer

    companion object {
        const val EXTRA_CUSTOMER_ID = "customer_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customerId = intent.getIntExtra(EXTRA_CUSTOMER_ID, -1)
        if (customerId == -1) {
            Toast.makeText(this, "Error: Customer ID not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val dao = CrmDatabase.getInstance(applicationContext).crmDao()
        val viewModel: CustomerDetailViewModel by viewModels {
            CustomerDetailViewModelFactory(dao, customerId)
        }

        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ordersRecyclerView.adapter = orderAdapter

        observeData(viewModel)

        binding.addOrderFab.setOnClickListener {
            val intent = Intent(this, AddEditOrderActivity::class.java)
            intent.putExtra(AddEditOrderActivity.EXTRA_CUSTOMER_ID, customerId)
            startActivity(intent)
        }

        // SET UP THE DELETE BUTTON CLICK
        binding.deleteCustomerButton.setOnClickListener {
            currentCustomer?.let { customerToDelete ->
                viewModel.deleteCustomer(customerToDelete)
                Toast.makeText(this, "${customerToDelete.name} deleted", Toast.LENGTH_SHORT).show()
                finish() // Go back to the list screen
            }
        }
    }

    private fun observeData(viewModel: CustomerDetailViewModel) {
        lifecycleScope.launch {
            viewModel.customer.collect { customer ->
                customer?.let {
                    currentCustomer = it // Store the loaded customer
                    binding.customerNameDetailTextView.text = it.name
                    binding.customerEmailDetailTextView.text = it.email
                    binding.customerPhoneDetailTextView.text = it.phone
                }
            }
        }

        lifecycleScope.launch {
            viewModel.orders.collect { orders ->
                orderAdapter.submitList(orders)
            }
        }
    }
}