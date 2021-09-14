package tairov.baxti.shop.firms

import java.io.Serializable

data class Firm(
    var id: String = "",
    var name: String = "",
    var pay: Double = 0.0,
    var debt: Double = 0.0
): Serializable{}
