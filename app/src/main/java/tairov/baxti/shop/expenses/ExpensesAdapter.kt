package tairov.baxti.shop.expenses

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ExpensesListItemBinding
import tairov.baxti.shop.firms.firmDetail.InvoicesConsts

class ExpensesAdapter(private val onClickListener: ClickExpense): RecyclerView.Adapter<ExpensesAdapter.ExpensesHolder>() {
    private val expensesList = ArrayList<Expense>()

    class ExpensesHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = ExpensesListItemBinding.bind(item)
        fun bind(expense: Expense){
            binding.title.text = expense.name
            binding.expense.text = expense.price.toString()
            binding.date.text = MainConsts.SIMPLE_DATE_FORMAT.format(expense.date.toDate())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.expenses_list_item,
            parent,
            false
        )
        return ExpensesHolder(view)
    }

    override fun onBindViewHolder(holder: ExpensesHolder, position: Int) {
        holder.bind(expensesList[position])
        holder.binding.delete.setOnClickListener {
            onClickListener.onDelete(expensesList[position].id)
        }
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(expenseList: ArrayList<Expense>) {
        expensesList.clear()
        expensesList.addAll(expenseList)
        notifyDataSetChanged()
    }
}