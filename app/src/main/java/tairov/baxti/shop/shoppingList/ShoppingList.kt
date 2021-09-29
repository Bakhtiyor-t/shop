package tairov.baxti.shop.shoppingList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityShoppingListBinding
import tairov.baxti.shop.debetors.ClickDebtorItem
import tairov.baxti.shop.dialogs.AddItemToShoppingList
import tairov.baxti.shop.dialogs.EditItemToShoppingList

class ShoppingList : AppCompatActivity(),
        AddItemToShoppingList.AddShoppingListItemListener,
        EditItemToShoppingList.EditDebtorDialogListener
{
    private lateinit var binding: ActivityShoppingListBinding
    private var adapter = ShoppingListAdapter(initClickListeners())
    private lateinit var db: FirebaseDatabase
    private lateinit var productsRef: DatabaseReference
    private var addNewShoppingListItem = AddItemToShoppingList()
    private var editShoppingListItem = EditItemToShoppingList()
    private val products = ArrayList<ShoppingListItem>()
    private val doneProducts = ArrayList<ShoppingListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseDatabase.getInstance()
        productsRef = db.getReference(ShoppingListConsts.SHOPPING_LIST)
        initAdapter()
        binding.addProduct.setOnClickListener {
            val fm = supportFragmentManager
            addNewShoppingListItem.show(fm, "addNewShoppingListItem_tag")
        }
        getFromDatabase()
    }

    private fun initAdapter(){
        binding.shoppingList.layoutManager = LinearLayoutManager(this)
        binding.shoppingList.adapter = adapter
    }

    private fun getFromDatabase(){
        productsRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (product in snapshot.children){
                    val prod = product.getValue<ShoppingListItem>()
                    if (prod != null) {
                        prod.productId = product.key.toString()
                        if(prod.done){
                            doneProducts.add(0, prod)
                        }else{
                            products.add(0, prod)
                        }
                    }
                }
                adapter.addAllProducts(products, doneProducts)
                products.clear()
                doneProducts.clear()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initClickListeners(): ClickShoppingListItem {
        return object : ClickShoppingListItem {
            override fun done(productId: String) {
               val product = productsRef.child(productId)
                   .child(ShoppingListConsts.DONE)
                product.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val done = snapshot.value.toString().toBoolean()
                        productsRef.child(productId)
                            .child(ShoppingListConsts.DONE)
                            .setValue(!done)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            override fun onDelete(productId: String) {
                productsRef.child(productId).removeValue()
                Toast.makeText(baseContext, "Запись удалена", Toast.LENGTH_SHORT).show()
            }
            override fun onEdit(productId: String) {
                val fm = supportFragmentManager
                editShoppingListItem.productId = productId
                editShoppingListItem.show(fm, "editShoppingListItem_tag")
            }
        }
    }

    override fun add(dialog: AddItemToShoppingList) {
        val productName = dialog.binding.newProductName.text.toString()
        productsRef.push().setValue(ShoppingListItem(productName = productName))
        dialog.dismiss()
    }

    override fun edit(dialog: EditItemToShoppingList) {
        val productName = dialog.binding.newProductName.text.toString()
        productsRef.child(dialog.productId)
            .child(ShoppingListConsts.PRODUCT_NAME)
            .setValue(productName)
        dialog.dismiss()
    }
}
