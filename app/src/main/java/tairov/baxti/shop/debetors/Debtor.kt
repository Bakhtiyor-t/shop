package tairov.baxti.shop.debetors

import android.util.Log
import java.io.Serializable

class Debtor {
    var id: String
    var name: String
    var pay: Int
    var debt: Int

    constructor(){
        this.id = ""
        this.name = ""
        this.pay = 0
        this.debt = 0
    }

    constructor(id: String, name: String, pay: Int, debt: Int) {
        this.id = id
        this.name = name
        this.pay = pay
        this.debt = debt
    }
}
