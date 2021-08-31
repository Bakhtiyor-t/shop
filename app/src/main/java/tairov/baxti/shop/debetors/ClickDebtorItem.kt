package tairov.baxti.shop.debetors

import android.view.View

interface ClickDebtorItem {
    fun onDelete(itemId: String)
    fun onEdit(itemId: String)
}