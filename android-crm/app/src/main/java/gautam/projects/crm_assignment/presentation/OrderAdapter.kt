package gautam.projects.crm_assignment.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gautam.projects.crm_assignment.R
import gautam.projects.crm_assignment.data.local.room.Order
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter : ListAdapter<Order, OrderAdapter.OrderViewHolder>(DiffCallback) {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.orderTitleTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.orderDateTextView)
        private val amountTextView: TextView = itemView.findViewById(R.id.orderAmountTextView)

        // Helper to format the date from a Long to a readable String
        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        fun bind(order: Order) {
            titleTextView.text = order.orderTitle
            amountTextView.text = "$${order.orderAmount}" // Format amount as currency
            dateTextView.text = dateFormat.format(Date(order.orderDate)) // Format the date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}