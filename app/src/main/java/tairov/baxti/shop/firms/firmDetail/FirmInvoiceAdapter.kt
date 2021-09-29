package tairov.baxti.shop.firms.firmDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.FirmDetailItemItemBinding

class FirmInvoiceAdapter: RecyclerView.Adapter<FirmInvoiceAdapter.FirmInvoiceHolder>() {
    val products = ArrayList<Int>()
    class FirmInvoiceHolder(item: View): RecyclerView.ViewHolder(item){
        val binding = FirmDetailItemItemBinding.bind(item)
        fun bind(product: Int){
            binding.count.text = "0"
            binding.discount.text = "0"
            binding.name.text = "dfsdfdsfff"
            binding.price.text = "134553"
            binding.number.text = "2"
            binding.totalPrice.text = "234234234"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmInvoiceHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.firm_detail_item_item, parent, false)
        return FirmInvoiceHolder(view)
    }

    override fun onBindViewHolder(holder: FirmInvoiceHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }
}