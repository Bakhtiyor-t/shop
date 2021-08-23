package tairov.baxti.shop.firms

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.FirmItemBinding

class FirmAdapter(private val onClickListener: ClickFirm) : RecyclerView.Adapter<FirmAdapter.FirmHolder>() {
    val firmList = ArrayList<Firm>()

    class FirmHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = FirmItemBinding.bind(item)
        fun bind(firm: Firm) = with(binding) {
            tvTitle.text = firm.title
            tvPay.text = firm.pay.toString()
            tvConsumption.text = firm.consumption.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirmHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.firm_item, parent, false)
        return FirmHolder(view)
    }

    override fun onBindViewHolder(holder: FirmHolder, position: Int) {
        holder.bind(firmList[position])
        holder.binding.firmItem.setOnClickListener {
            onClickListener.onClick(it)
        }
    }

    override fun getItemCount(): Int {
        return firmList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFirm(firm: Firm){
        firmList.add(firm)
        notifyDataSetChanged()
    }
}