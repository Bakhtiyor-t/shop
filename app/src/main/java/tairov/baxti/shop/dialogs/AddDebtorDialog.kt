package tairov.baxti.shop.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.databinding.AddDebtorDialogBinding

class AddDebtorDialog: DialogFragment() {
    lateinit var binding: AddDebtorDialogBinding
    private lateinit var listener: AddDebtorDialogListener

    interface AddDebtorDialogListener {
        fun addNewDebtor(dialog: AddDebtorDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddDebtorDialogBinding.inflate(inflater)
        binding.addNewDebtor.setOnClickListener {
            if(!isFieldEmpty()){
                listener.addNewDebtor(this)
                resetSettings()
                Toast.makeText(context, "Добавлено", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AddDebtorDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    private fun isFieldEmpty(): Boolean{
        binding.apply {
            if(edDebtorName.text.isNullOrEmpty()) edDebtorName.error = "Поле должно быть заполнено"
            if(edNewDebt.text.isNullOrEmpty()) edNewDebt.error = "Поле должно быть заполнено"
            return  edDebtorName.text.isNullOrEmpty() || edNewDebt.text.isNullOrEmpty()
        }
    }

    private fun resetSettings(){
        binding.edDebtorName.text = null
        binding.edDebtorName.error = null
        binding.edNewDebt.text = null
        binding.edNewDebt.error = null
    }
}