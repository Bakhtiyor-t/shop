package tairov.baxti.shop.debetors

import android.util.Log
import java.io.Serializable

class Debtor {
    var id: String
    var name: String
    var pay: Double = 0.0
    var debt: Double = 0.0

    constructor(){
        this.id = null.toString()
        this.name = null.toString()
        this.pay = 0.0
        this.debt = 0.0
    }

    constructor(id: String, name: String, pay: Double, debt: Double) {
        this.id = id
        this.name = name
        this.pay = pay
        this.debt = debt
    }
}
