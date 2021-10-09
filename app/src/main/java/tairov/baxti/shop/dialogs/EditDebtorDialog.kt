package tairov.baxti.shop.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.databinding.EditDebtorDialogBinding

class EditDebtorDialog: DialogFragment() {
    lateinit var binding:EditDebtorDialogBinding
    lateinit var debtorName: String
    lateinit var debtorId: String
    private lateinit var listener: EditDebtorDialogListener

    interface EditDebtorDialogListener {
        fun cancel(dialog: EditDebtorDialog)
        fun done(dialog: EditDebtorDialog, debtorId: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditDebtorDialogBinding.inflate(inflater)
        binding.debtorName.text = debtorName
        binding.cancel.setOnClickListener {
            listener.cancel(this)
            resetSettings()
        }
        binding.done.setOnClickListener {
            if(isFieldEmpty()){
                listener.done(this, debtorId=debtorId)
                resetSettings()
            }else{
                Toast.makeText(context, "Заполните хотябы одно поле", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as EditDebtorDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement EditDebtorDialogListener"))
        }
    }

    private fun isFieldEmpty(): Boolean{
        binding.apply {
            if(edPaid.text.isNullOrEmpty()) edPaid.error = "Поле должно быть заполнено"
            if(edDebt.text.isNullOrEmpty()) edDebt.error = "Поле должно быть заполнено"
            return  !edPaid.text.isNullOrEmpty() || !edDebt.text.isNullOrEmpty()
        }
    }

    private fun resetSettings(){
        binding.edPaid.text = null
        binding.edPaid.error = null
        binding.edDebt.text = null
        binding.edDebt.error = null
    }
}