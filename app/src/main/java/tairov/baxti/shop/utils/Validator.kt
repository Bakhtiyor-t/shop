package tairov.baxti.shop.utils

import android.widget.EditText

object Validator {
    fun checkAllFieldIsFull(editTexts: ArrayList<EditText>): Boolean {
        var flag = true
        for (editText in editTexts){
            if(editText.text.isNullOrEmpty()){
                editText.error = "Поле должно быть заполнено"
                flag = false
            }
        }
        return if(flag) flag
        else false
    }

    fun checkOneFieldIsFull(editTexts: ArrayList<EditText>): Boolean {
        for (editText in editTexts){
            if(!editText.text.isNullOrEmpty())
                return true
            if(editText.text.isNullOrEmpty()){
                editText.error = "Поле должно быть заполнено"
            }
        }
        return false
    }

    fun resetSettings(editTexts: ArrayList<EditText>) {
        for (editText in editTexts){
            editText.text = null
            editText.error = null
        }
    }
}