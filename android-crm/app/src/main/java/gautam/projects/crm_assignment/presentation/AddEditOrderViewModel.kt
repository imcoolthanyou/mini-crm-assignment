package gautam.projects.crm_assignment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import gautam.projects.crm_assignment.data.local.room.CrmDao
import gautam.projects.crm_assignment.data.local.room.Order
import kotlinx.coroutines.launch

class AddEditOrderViewModel(private val dao: CrmDao): ViewModel() {

    fun saveOrder(order: Order){
        viewModelScope.launch {
            dao.insertOrUpdateOrder(order)
        }
    }
}

class AddEditOrderViewModelFactory(private val dao: CrmDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditOrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditOrderViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}