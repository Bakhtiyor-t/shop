package tairov.baxti.shop.firms

import java.io.Serializable

data class Firm(val id: Int,val title: String, val pay: Double, val consumption: Double): Serializable
