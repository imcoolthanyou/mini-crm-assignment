package gautam.projects.crm_assignment.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "email")
    val email: String = "",

    @ColumnInfo(name = "phone")
    val phone: String = "",

    @ColumnInfo(name = "company")
    val company: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firebase
    constructor() : this(
        id = 0,
        name = "",
        email = "",
        phone = "",
        company = "",
        createdAt = System.currentTimeMillis()
    )
}