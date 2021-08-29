package tairov.baxti.shop.firms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tairov.baxti.shop.databinding.ActivityAddFirmBinding

class AddFirm : AppCompatActivity() {
    private lateinit var binding: ActivityAddFirmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddFirmBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.addNewFirm.setOnClickListener {
            addNewFirm()
        }
    }

    fun addNewFirm() = with(binding){
        if(!isFieldEmpty()){
            val firm = Firm(
                1234,
                editTitle.text.toString(),
                editPay.text.toString().toDouble(),
                editDuty.text.toString().toDouble(),
            )
            val intent = Intent().apply {
                putExtra("firm", firm)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun isFieldEmpty(): Boolean{
        binding.apply {
            if(editTitle.text.isNullOrEmpty()) editTitle.error = "Поле должно быть заполнено"
            if(editPay.text.isNullOrEmpty()) editPay.error = "Поле должно быть заполнено"
            if(editDuty.text.isNullOrEmpty()) editDuty.error = "Поле должно быть заполнено"
            return  editTitle.text.isNullOrEmpty() || editPay.text.isNullOrEmpty() || editDuty.text.isNullOrEmpty()
        }
    }
}