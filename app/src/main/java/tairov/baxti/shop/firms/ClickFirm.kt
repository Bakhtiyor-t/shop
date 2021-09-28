package tairov.baxti.shop.firms

import android.view.View

interface ClickFirm {
    fun onClick(firm: Firm)
    fun onEdit(firmId: String)
    fun onDelete(firmId: String)
}