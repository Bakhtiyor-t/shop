package tairov.baxti.shop.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.EditDebtorDialogBinding

class EditDebtorDialog: DialogFragment() {

    internal lateinit var listener: EditDebtorDialogListener

    interface EditDebtorDialogListener {
        fun cancel(dialog: DialogFragment)
        fun done(dialog: DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = EditDebtorDialogBinding.inflate(inflater)
        binding.cancel.setOnClickListener {
            listener.cancel(this)
        }
        binding.done.setOnClickListener {
            listener.done(this)
        }
        return binding.root
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.setTitle("Изменить")
//        return dialog
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as EditDebtorDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }
}