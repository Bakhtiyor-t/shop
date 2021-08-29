package tairov.baxti.shop.debetors

import java.io.Serializable

data class Debtor(val id: Int,val title: String, val pay: Double, val consumption: Double):
    Serializable
