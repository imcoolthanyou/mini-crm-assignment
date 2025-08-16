package gautam.projects.crm_assignment.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import gautam.projects.crm_assignment.data.local.room.CrmDao
import gautam.projects.crm_assignment.data.local.room.Customer
import gautam.projects.crm_assignment.data.local.room.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CustomerDetailViewModel(
    private val dao: CrmDao,
    private val customerId: Int
) : ViewModel(){

    val customer : Flow<Customer> = dao.getCustomerById(customerId)

    val orders : Flow<List<Order>> = dao.getOrdersByCustomerId(customerId)

    fun deleteCustomer(customer: Customer){
        viewModelScope.launch {
            dao.deleteCustomer(customer)
        }
    }

}

class CustomerDetailViewModelFactory(
    private val dao: CrmDao,
    private val customerId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerDetailViewModel(dao, customerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}