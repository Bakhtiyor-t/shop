package tairov.baxti.shop.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.databinding.SortDialogBinding
import tairov.baxti.shop.utils.Validator

class SortDialog: DialogFragment() {
    lateinit var binding: SortDialogBinding
    interface SortDialogListener{
        fun dateDone(dialog: SortDialog)
        fun changeDate(editText: EditText)
        fun changeDate2(editText: EditText)
    }
    private lateinit var listener: SortDialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SortDialogBinding.inflate(inflater)
        val fields = arrayListOf<EditText>(binding.date, binding.date2)
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.changeDate.setOnClickListener {
            listener.changeDate(binding.date)
        }
        binding.changeDate2.setOnClickListener {
            listener.changeDate2(binding.date2)
        }
        binding.done.setOnClickListener {
            if(Validator.checkAllFieldIsFull(fields)){
                listener.dateDone(this)
                Validator.resetSettings(fields)
            }
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SortDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement SortDialogListener"))
        }
    }
}