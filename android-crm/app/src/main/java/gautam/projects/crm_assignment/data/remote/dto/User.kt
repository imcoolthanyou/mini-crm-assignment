package gautam.projects.crm_assignment.data.remote.dto


data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val company: Company
)

data class Company(
    val name: String
)