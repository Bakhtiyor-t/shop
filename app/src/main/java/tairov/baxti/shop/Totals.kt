package tairov.baxti.shop

data class Totals(
    val id: String = "",
    var profit: Double = 0.0,
    var income: Double = 0.0,
    var expenses: Double = 0.0,
    var debt: Double = 0.0
)