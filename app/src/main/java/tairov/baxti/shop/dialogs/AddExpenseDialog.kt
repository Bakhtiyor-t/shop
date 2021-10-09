package tairov.baxti.shop.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.databinding.AddExpenseDialogBinding
import tairov.baxti.shop.utils.Validator
import java.lang.ClassCastException

class AddExpenseDialog: DialogFragment() {
    lateinit var binding: AddExpenseDialogBinding
    private lateinit var listener: AddExpenseDialogListener
    interface AddExpenseDialogListener {
        fun done(dialog: AddExpenseDialog)
        fun changeDate(dialog: AddExpenseDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddExpenseDialogBinding.inflate(inflater)
        val editTexts = arrayListOf(binding.expenseName, binding.amount, binding.date)
        binding.cancel.setOnClickListener {
            dismiss()
            Validator.resetSettings(editTexts)
        }
        binding.done.setOnClickListener {
            if(Validator.checkAllFieldIsFull(editTexts)){
                listener.done(this)
                Validator.resetSettings(editTexts)
            }
        }
        binding.changeDate.setOnClickListener {
            listener.changeDate(this)
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            listener = context as AddExpenseDialogListener
        }catch (e: ClassCastException){
            throw ClassCastException(context.toString() +
            " must implement AddExpenseDialogListener")
        }
    }
}