package gautam.projects.crm_assignment.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import gautam.projects.crm_assignment.data.local.room.CrmDatabase
import gautam.projects.crm_assignment.data.local.room.Order

import gautam.projects.crm_assignment.databinding.ActivityAddEditOrderBinding

class AddEditOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditOrderBinding
    private var customerId: Int = -1

    // The missing piece is added here
    companion object {
        const val EXTRA_CUSTOMER_ID = "customer_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the customer ID from the intent
        customerId = intent.getIntExtra(EXTRA_CUSTOMER_ID, -1)
        if (customerId == -1) {
            Toast.makeText(this, "Error: Customer ID not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Get the ViewModel
        val dao = CrmDatabase.getInstance(applicationContext).crmDao()
        val viewModel: AddEditOrderViewModel by viewModels {
            AddEditOrderViewModelFactory(dao)
        }

        // Set the save button click listener
        binding.saveOrderButton.setOnClickListener {
            saveOrder(viewModel)
        }
    }

    private fun saveOrder(viewModel: AddEditOrderViewModel) {
        val title = binding.orderTitleEditText.text.toString()
        val amountString = binding.orderAmountEditText.text.toString()

        if (title.isBlank() || amountString.isBlank()) {
            Toast.makeText(this, "Title and amount cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountString.toIntOrNull()
        if (amount == null) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the new Order, including  customerId
        val newOrder = Order(
            customerId = this.customerId,
            orderTitle = title,
            orderAmount = amount
        )

        viewModel.saveOrder(newOrder)
        Toast.makeText(this, "Order saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}