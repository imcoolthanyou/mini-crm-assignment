package gautam.projects.crm_assignment.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import gautam.projects.crm_assignment.data.local.room.CrmDatabase
import gautam.projects.crm_assignment.data.local.room.Customer

import gautam.projects.crm_assignment.databinding.ActivityAddEditCustomerBinding
import kotlinx.coroutines.launch

class AddEditCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditCustomerBinding
    private var customerId: Int = -1

    companion object {
        const val EXTRA_CUSTOMER_ID = "customer_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customerId = intent.getIntExtra(EXTRA_CUSTOMER_ID, -1)

        val dao = CrmDatabase.getInstance(applicationContext).crmDao()
        val viewModel: AddEditCustomerViewModel by viewModels {
            AddEditCustomerViewModelFactory(dao)
        }

        if (customerId != -1) {
            viewModel.loadCustomer(customerId)
        }

        observeViewModel(viewModel)

        binding.fetchButton.setOnClickListener {
            viewModel.fetchRandomUser() // Call the renamed function
            Toast.makeText(this, "Fetching data...", Toast.LENGTH_SHORT).show()
        }

        binding.saveCustomerButton.setOnClickListener {
            saveCustomer(viewModel)
        }
    }

    private fun observeViewModel(viewModel: AddEditCustomerViewModel) {
        // Observer for loading an existing customer
        lifecycleScope.launch {
            viewModel.customer.collect { customer ->
                customer?.let {
                    binding.nameEditText.setText(it.name)
                    binding.emailEditText.setText(it.email)
                    binding.phoneEditText.setText(it.phone)
                    binding.companyEditText.setText(it.company)
                }
            }
        }

        // *** THIS BLOCK IS UPDATED FOR THE NEW USER OBJECT ***
        lifecycleScope.launch {
            viewModel.randomUser.collect { randomUser ->
                randomUser?.let {
                    binding.nameEditText.setText(it.name)
                    binding.phoneEditText.setText(it.phone)
                    binding.emailEditText.setText(it.email)
                    binding.companyEditText.setText(it.company.name) // Use it.company.name
                }
            }
        }
    }

    private fun saveCustomer(viewModel: AddEditCustomerViewModel) {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()
        val company = binding.companyEditText.text.toString()

        if (name.isBlank()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val customerToSave = Customer(
            id = if (customerId != -1) customerId else 0,
            name = name,
            email = email,
            phone = phone,
            company = company
        )

        viewModel.saveCustomer(customerToSave)
        Toast.makeText(this, "Customer saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}