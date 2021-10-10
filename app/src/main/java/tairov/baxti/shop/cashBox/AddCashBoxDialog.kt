package tairov.baxti.shop.cashBox

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.databinding.CashBoxAddDialogBinding
import tairov.baxti.shop.utils.Validator
import java.lang.ClassCastException

class AddCashBoxDialog : DialogFragment() {
    lateinit var binding: CashBoxAddDialogBinding
    private lateinit var listener: AddCashBoxDialogListener
    interface AddCashBoxDialogListener {
        fun changeDate(editText: EditText)
        fun done(dialog: AddCashBoxDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CashBoxAddDialogBinding.inflate(inflater)
        val fields = arrayListOf(binding.date, binding.cash, binding.card)
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.done.setOnClickListener {
            if(Validator.checkAllFieldIsFull(fields)){
                listener.done(this)
                Validator.resetSettings(fields)
            }
        }
        binding.changeDate.setOnClickListener {
            listener.changeDate(binding.date)
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AddCashBoxDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                "  must implement AddCashBoxDialogListener"
            )
        }
    }
}