package gautam.projects.crm_assignment.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "customer_id")
    val customerId: Int = 0,

    @ColumnInfo(name = "order_title")
    val orderTitle: String = "",

    @ColumnInfo(name = "order_amount")
    val orderAmount: Int = 0,

    @ColumnInfo(name = "order_date")
    val orderDate: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firebase
    constructor() : this(
        id = 0,
        customerId = 0,
        orderTitle = "",
        orderAmount = 0,
        orderDate = System.currentTimeMillis()
    )
}