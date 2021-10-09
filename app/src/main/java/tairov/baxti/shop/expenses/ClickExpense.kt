package tairov.baxti.shop.expenses

import tairov.baxti.shop.firms.Firm

interface ClickExpense {
    fun onDelete(expenseId: String)
}