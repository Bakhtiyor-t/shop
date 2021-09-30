package tairov.baxti.shop.firms.firmDetail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.FirmDetailItemWithImageBinding
import tairov.baxti.shop.firms.FirmsConsts
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class FirmDetailAdapter(val context: Context, private val onClickListener: ClickFirmDetail): RecyclerView.Adapter<FirmDetailAdapter.FirmDetailHolder>() {
    private val invoices = ArrayList<Invoice>()

    class FirmDetailHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = FirmDetailItemWithImageBinding.bind(item)
        @SuppressLint("SimpleDateFormat")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(invoice: Invoice){
            Picasso.get().load(invoice.imageUri).into(binding.invoiceImage)
            binding.payment.text = invoice.payment.toString()
            binding.paidFor.text = invoice.paidFor.toString()
            binding.previousDebt.text = invoice.previousDebt.toString()
            binding.totalDebt.text = invoice.totalDebt.toString()
            binding.date.text = InvoicesConsts.SIMPLE_DATE_FORMAT.format(invoice.date!!.toDate())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmDetailHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.firm_detail_item_with_image, parent, false)
        return FirmDetailHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FirmDetailHolder, position: Int) {
        holder.bind(invoices[position])
        holder.binding.invoiceImage.setOnClickListener {
            onClickListener.imageClick(invoices[position].imageUri,)
        }
        holder.binding.delete.setOnClickListener {
            onClickListener.delete(invoices[position].id, invoices[position].imageId)
        }
        holder.binding.edit.setOnClickListener {
            onClickListener.edit(invoices[position].id)
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