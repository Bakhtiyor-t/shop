package tairov.baxti.shop.expenses

import com.google.firebase.Timestamp

data class Expense(
    var id: String = "",
    var firmId: String = "",
    var firms: Boolean = false,
    var name: String = "",
    var price: Double = 0.0,
    var date: Timestamp = Timestamp.now()
)