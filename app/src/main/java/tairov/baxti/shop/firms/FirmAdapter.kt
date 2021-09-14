package tairov.baxti.shop.firms

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.FirmItemBinding

class FirmAdapter(private val onClickListener: ClickFirm) : RecyclerView.Adapter<FirmAdapter.FirmHolder>() {
    var firms = ArrayList<Firm>()

    class FirmHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = FirmItemBinding.bind(item)
        fun bind(firm: Firm) = with(binding) {
            firmItemId.text = firm.id.toString()
            tvTitle.text = firm.title
            tvPay.text = firm.pay.toString()
            tvConsumption.text = firm.debt.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.firm_item, parent, false)
        return FirmHolder(view)
    }

    override fun onBindViewHolder(holder: FirmHolder, position: Int) {
        holder.bind(firms[position])
        holder.binding.firmItem.setOnClickListener {
            onClickListener.onClick(it)
        }
    }

    override fun getItemCount(): Int {
        return firms.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFirm(firm: Firm){
        firms.add(0, firm)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllFirm(firmsLists: ArrayList<Firm>){
        firms.clear()
        firms.addAll(firmsLists)
        notifyDataSetChanged()
    }
}