package gautam.projects.crm_assignment.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gautam.projects.crm_assignment.R
import gautam.projects.crm_assignment.data.local.room.Customer



// 1. ADD A SECOND FUNCTION FOR LONG CLICKS
class CustomerAdapter(
    private val onItemClicked: (Customer) -> Unit,
    private val onItemLongClicked: (Customer) -> Unit
) : ListAdapter<Customer, CustomerAdapter.CustomerViewHolder>(DiffCallback) {

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.customerNameTextView)
        private val detailTextView: TextView = itemView.findViewById(R.id.customerDetailTextView)

        fun bind(
            customer: Customer,
            onItemClicked: (Customer) -> Unit,
            onItemLongClicked: (Customer) -> Unit
        ) {
            nameTextView.text = customer.name
            detailTextView.text = customer.email
            // Regular click listener
            itemView.setOnClickListener {
                onItemClicked(customer)
            }
            // 2. ADD THE LONG CLICK LISTENER
            itemView.setOnLongClickListener {
                onItemLongClicked(customer)
                true // Return true to indicate we've handled the event
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = getItem(position)
        // 3. PASS THE LONG CLICK FUNCTION TO THE BIND METHOD
        holder.bind(customer, onItemClicked, onItemLongClicked)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }
    }
}