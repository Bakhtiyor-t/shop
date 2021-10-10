package tairov.baxti.shop.cashBox

import com.google.firebase.Timestamp


class CashBoxItem(
    val id: String = "",
    val date: Timestamp = Timestamp.now(),
    val cash: Double = 0.0,
    val card: Double = 0.0,
)