package tairov.baxti.shop.firms

import java.io.Serializable
import java.time.LocalDate
import java.util.*

data class Invoice(
    var id: String = "",
    var firmId: String = "",
    var imageId: String = "",
    var imageUri: String = "",
    var paidFor: Double = 0.0,
    var payment: Double = 0.0,
    var previousDebt: Double = 0.0,
    var totalDebt: Double = 0.0,
    var date: String = ""
//    var date: LocalDate? = null
): Serializable {}