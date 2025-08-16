package gautam.projects.crm_assignment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gautam.projects.crm_assignment.data.local.room.CrmDao
import gautam.projects.crm_assignment.data.local.room.Customer
import kotlinx.coroutines.flow.Flow
//viewmodel
class CustomerListViewModel(private val dao: CrmDao): ViewModel() {

    val allCustomers: Flow <List<Customer>> = dao.getAllCustomers()

}
//Factory to help create the viewmodel
class CustomerListViewModelFactory(private val dao: CrmDao): ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        if(modelClass.isAssignableFrom(CustomerListViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CustomerListViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}