package tairov.baxti.shop.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.databinding.EditFirmDialogBinding

class EditFirmDialog: DialogFragment() {
    lateinit var binding: EditFirmDialogBinding
    var firmId = ""
    private lateinit var listener: EditFirmDialogListener
    interface EditFirmDialogListener {
        fun done(dialog: EditFirmDialog, firmId: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditFirmDialogBinding.inflate(inflater)
        binding.done.setOnClickListener {
            if(isFieldEmpty()){
                listener.done(this, firmId)
                resetSettings()
            }
        }
        binding.cancel.setOnClickListener {
            resetSettings()
            dismiss()
        }
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as EditFirmDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement EditFirmDialogListener"))
        }
    }

    private fun isFieldEmpty(): Boolean{
        binding.apply {
            if(firmName.text.isNullOrEmpty()) firmName.error = "Поле должно быть заполнено"
            if(paid.text.isNullOrEmpty()) paid.error = "Поле должно быть заполнено"
            if(debt.text.isNullOrEmpty()) debt.error = "Поле должно быть заполнено"
            return  !paid.text.isNullOrEmpty() || !debt.text.isNullOrEmpty() || !firmName.text.isNullOrEmpty()
        }
    }

    private fun resetSettings(){
        binding.firmName.text = null
        binding.firmName.error = null
        binding.paid.text = null
        binding.paid.error = null
        binding.debt.text = null
        binding.debt.error = null
    }
}