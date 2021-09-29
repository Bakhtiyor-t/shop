package tairov.baxti.shop.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.databinding.EditFirmDetailDialogBinding
import java.lang.ClassCastException

class EditFirmDetailDialog: DialogFragment() {
    lateinit var binding: EditFirmDetailDialogBinding
    var invoiceId: String = ""
    lateinit var listener: EditFirmDetailDialogListener
    interface EditFirmDetailDialogListener {
        fun done(dialog: EditFirmDetailDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditFirmDetailDialogBinding.inflate(inflater)
        binding.cancel.setOnClickListener {
            dismiss()
            resetSettings()
        }
        binding.done.setOnClickListener {
            if(!isFieldEmpty()){
                listener.done(this)
                resetSettings()
            }
        }
        return  binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            listener = context as EditFirmDetailDialogListener
        }catch (e: ClassCastException){
            throw ClassCastException(context.toString()+
            "must implement EditFirmDetailDialogListener")
        }
    }

    private fun isFieldEmpty(): Boolean{
        binding.apply {
            if(edPayment.text.isNullOrEmpty()) edPayment.error = "Поле должно быть заполнено"
            if(edPaidFor.text.isNullOrEmpty()) edPaidFor.error = "Поле должно быть заполнено"
            if(edPreviousDebt.text.isNullOrEmpty()) edPreviousDebt.error = "Поле должно быть заполнено"
//            if(edTotalDebt.text.isNullOrEmpty()) edTotalDebt.error = "Поле должно быть заполнено"
            return  (   edPayment.text.isNullOrEmpty() ||
                        edPaidFor.text.isNullOrEmpty() ||
                        edPreviousDebt.text.isNullOrEmpty()
//                        !edTotalDebt.text.isNullOrEmpty()
                    )
        }
    }

    private fun resetSettings(){
        binding.edPayment.text = null
        binding.edPayment.error = null
        binding.edPaidFor.text = null
        binding.edPaidFor.error = null
        binding.edPreviousDebt.text = null
        binding.edPreviousDebt.error = null
        binding.edTotalDebt.text = null
        binding.edTotalDebt.error = null
    }
}