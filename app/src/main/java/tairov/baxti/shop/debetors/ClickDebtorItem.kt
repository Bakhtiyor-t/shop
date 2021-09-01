package tairov.baxti.shop.debetors

import android.view.View

interface ClickDebtorItem {
    fun onDelete(debtorId: String)
    fun onEdit(debtorId: String, debtorName: String)
}