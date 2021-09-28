package tairov.baxti.shop.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.AddShoppingListItemBinding

class EditItemToShoppingList: DialogFragment() {
    lateinit var binding: AddShoppingListItemBinding
    lateinit var listener: EditDebtorDialogListener
    var productId = ""

    interface EditDebtorDialogListener{
        fun edit(dialog: EditItemToShoppingList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddShoppingListItemBinding.inflate(inflater)
        binding.productTitle.text = getString(R.string.editProductName)
        binding.newProductName.hint = getString(R.string.productName)
        binding.addNewProduct.setOnClickListener {
            if(!isFieldEmpty()){
                listener.edit(this)
                resetSettings()
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
                    " must implement NoticeDialogListener"))
        }
    }

    private fun isFieldEmpty(): Boolean{
        binding.apply {
            if(newProductName.text.isNullOrEmpty()) newProductName.error = "Поле должно быть заполнено"
            return  newProductName.text.isNullOrEmpty()
        }
    }

    private fun resetSettings(){
        binding.newProductName.text = null
        binding.newProductName.error = null
    }
}