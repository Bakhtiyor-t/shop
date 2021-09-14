package tairov.baxti.shop.shoppingList

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ShoppingListItemBinding
import tairov.baxti.shop.debetors.Debtor

class ShoppingListAdapter(private val onItemClickListener: ClickShoppingListItem): RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>() {
    private val shoppingList = ArrayList<ShoppingListItem>()

    class ShoppingListHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = ShoppingListItemBinding.bind(item)
        fun bind(product: ShoppingListItem) = with(binding) {
            productItemId.text = product.productId.toString()
//            if(product.done){
//                productName.text = Html.fromHtml(
//                    "<del>${product.productName}</del>",
//                    HtmlCompat.FROM_HTML_MODE_LEGACY
//                )
//            }else{
//                productName.text = product.productName
//            }
//            productName.text = product.productName
//            if(product.done){
//                productName.setBackgroundResource(R.drawable.line)
//            }
            productName.text = product.productName
            if(product.done){
                productName.paintFlags = productName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }else{
                productName.paintFlags = productName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            bought.isChecked = product.done
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shopping_list_item, parent, false)
        return ShoppingListHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListHolder, position: Int) {
        holder.bind(shoppingList[position])
        val productId = holder.binding.productItemId.text.toString()
        holder.binding.bought.setOnClickListener{
            onItemClickListener.done(productId)
        }
        holder.binding.delete.setOnClickListener {
            onItemClickListener.onDelete(productId)
        }
        holder.binding.edit.setOnClickListener {
            onItemClickListener.onEdit(productId)
        }
    }

    override fun getItemCount(): Int {
        return shoppingList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addProduct(product: ShoppingListItem){
        shoppingList.add(product)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addAllProducts(products: ArrayList<ShoppingListItem>, doneProducts: ArrayList<ShoppingListItem>){
        shoppingList.clear()
        shoppingList.addAll(products + doneProducts)
        notifyDataSetChanged()
    }
}