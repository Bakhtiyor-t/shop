package tairov.baxti.shop.cashBox

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.CashBoxItemBinding

class CashBoxAdapter(private val onClickListener: CashBoxClick): RecyclerView.Adapter<CashBoxAdapter.CashBoxHolder>() {
    private val cashBoxList = ArrayList<CashBoxItem>()

    class CashBoxHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = CashBoxItemBinding.bind(item)
        fun bind(cashBoxItem: CashBoxItem){
            binding.date.text = MainConsts.SIMPLE_DATE_FORMAT.format(cashBoxItem.date.toDate())
            binding.cash.text = cashBoxItem.cash.toString()
            binding.card.text = cashBoxItem.card.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashBoxHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cash_box_item, parent, false)
        return CashBoxHolder(view)
    }

    override fun onBindViewHolder(holder: CashBoxHolder, position: Int) {
        holder.bind(cashBoxList[position])
        holder.binding.delete.setOnClickListener {
            onClickListener.onDelete(cashBoxList[position].id)
        }
    }

    override fun getItemCount(): Int {
        return cashBoxList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(cashBoxItemList: ArrayList<CashBoxItem>){
        cashBoxList.clear()
        cashBoxList.addAll(cashBoxItemList)
        notifyDataSetChanged()
    }
}