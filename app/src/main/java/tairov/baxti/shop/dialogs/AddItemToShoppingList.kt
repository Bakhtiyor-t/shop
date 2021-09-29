package tairov.baxti.shop.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import tairov.baxti.shop.databinding.AddShoppingListItemBinding

class AddItemToShoppingList: DialogFragment() {
    lateinit var binding: AddShoppingListItemBinding
    private lateinit var listener: AddShoppingListItemListener
    interface AddShoppingListItemListener{
        fun add(dialog: AddItemToShoppingList)
    }

    var title: String? = null
    var hint: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddShoppingListItemBinding.inflate(inflater)
        if(title != null && hint != null ){
            binding.productTitle.text = title
            binding.newProductName.hint = hint
        }
        binding.addNewProduct.setOnClickListener {
            if(!isFieldEmpty()){
                listener.add(this)
                resetSettings()
            }
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AddShoppingListItemListener
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