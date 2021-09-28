package tairov.baxti.shop.debetors

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.DebtorItemBinding
import tairov.baxti.shop.firms.Firm

class DebtorAdapter(private val onItemClickListener: ClickDebtorItem): RecyclerView.Adapter<DebtorAdapter.DebtorHolder>() {
    var debtors = ArrayList<Debtor>()

    class DebtorHolder(item: View):RecyclerView.ViewHolder(item){
        val binding = DebtorItemBinding.bind(item)
        fun bind(debtor: Debtor, position: Int) = with(binding){
            val debtorName = "${(position+1)}. ${debtor.name}"
            tvTitle.text = debtorName
            tvPay.text = debtor.pay.toString()
            tvDebt.text = debtor.debt.toString()
            debtorItemId.text = debtor.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtorHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.debtor_item, parent, false)
        return DebtorHolder(view)
    }

    override fun onBindViewHolder(holder: DebtorHolder, position: Int) {
        holder.bind(debtors[position], position)
        holder.binding.delete.setOnClickListener {
            onItemClickListener.onDelete(holder.binding.debtorItemId.text.toString())
        }
        holder.binding.edit.setOnClickListener {
            val debtorId = holder.binding.debtorItemId.text.toString()
            val debtorName = holder.binding.tvTitle.text.toString()
            onItemClickListener.onEdit(debtorId, debtorName)
        }
    }

    override fun getItemCount(): Int {
        return debtors.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addDebtor(debtor: Debtor){
        debtors.add(0,debtor)
        notifyItemInserted(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllDebtors(debtorsList: ArrayList<Debtor>){
        debtors.clear()
        debtors.addAll(debtorsList)
        notifyDataSetChanged()
    }
}