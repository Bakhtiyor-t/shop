package tairov.baxti.shop.firms

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.FirmDetailItemBinding
import tairov.baxti.shop.databinding.FirmDetailItemWithImageBinding
import kotlin.coroutines.coroutineContext

class FirmDetailAdapter(val context: Context, private val onClickListener: ClickFirmDetail): RecyclerView.Adapter<FirmDetailAdapter.FirmDetailHolder>() {
    private val invoices = ArrayList<Invoice>()
//    val products = arrayListOf(1,2,3,4,5,6,7)
    class FirmDetailHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = FirmDetailItemWithImageBinding.bind(item)
        fun bind(invoice: Invoice){
            Picasso.get().load(invoice.imageUri).into(binding.invoiceImage)
            binding.payment.text = invoice.payment.toString()
            binding.paidFor.text = invoice.paidFor.toString()
            binding.previousDebt.text = invoice.previousDebt.toString()
            binding.totalDebt.text = invoice.totalDebt.toString()
            binding.date.text = invoice.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmDetailHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.firm_detail_item_with_image, parent, false)
        return FirmDetailHolder(view)
    }

    override fun onBindViewHolder(holder: FirmDetailHolder, position: Int) {
        holder.bind(invoices[position])
        holder.binding.invoiceImage.setOnClickListener {
            val bitmap = (holder.binding.invoiceImage.drawable as BitmapDrawable).bitmap
            onClickListener.imageClick(invoices[position].imageUri, bitmap)
        }
        holder.binding.delete.setOnClickListener {
            onClickListener.delete(invoices[position].id, invoices[position].imageId)
        }
//        initChildAdapter(holder.binding.productsList, products)
    }

    override fun getItemCount(): Int {
        return invoices.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(invoice: ArrayList<Invoice>){
        invoices.clear()
        invoices.addAll(invoice)
        notifyDataSetChanged()
    }

//    private fun initChildAdapter(products: RecyclerView, items: ArrayList<Int>){
//        val adapter = FirmInvoiceAdapter()
//        adapter.products.addAll(items)
//        products.layoutManager = LinearLayoutManager(context)
//        products.adapter = adapter
//    }
}