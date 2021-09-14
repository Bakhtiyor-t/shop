package tairov.baxti.shop.shoppingList

interface ClickShoppingListItem {
    fun done(productId: String){}
    fun onDelete(productId: String)
    fun onEdit(productId: String)
}