package gautam.projects.crm_assignment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import gautam.projects.crm_assignment.data.remote.ApiClient
import gautam.projects.crm_assignment.data.remote.dto.User
import gautam.projects.crm_assignment.data.local.room.CrmDao
import gautam.projects.crm_assignment.data.local.room.Customer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditCustomerViewModel(private val dao: CrmDao) : ViewModel() {

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()

    // 2. CHANGE THE STATEFLOW TO HOLD A USER OBJECT
    private val _randomUser = MutableStateFlow<User?>(null)
    val randomUser: StateFlow<User?> = _randomUser.asStateFlow()

    fun loadCustomer(customerId: Int) {
        viewModelScope.launch {
            dao.getCustomerById(customerId).collect { loadedCustomer ->
                _customer.value = loadedCustomer
            }
        }
    }


    fun fetchRandomUser() {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.getRandomUsers()
                if (response.isSuccessful) {
                    // Take the first user from the list, if the list is not empty
                    _randomUser.value = response.body()?.firstOrNull()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch {
            dao.insertOrUpdateCustomer(customer)
        }
    }
}

class AddEditCustomerViewModelFactory(private val dao: CrmDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditCustomerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditCustomerViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}