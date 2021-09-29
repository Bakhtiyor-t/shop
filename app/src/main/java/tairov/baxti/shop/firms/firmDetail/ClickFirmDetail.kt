package tairov.baxti.shop.firms.firmDetail

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

interface ClickFirmDetail {
    fun imageClick(imageUri: String)
    fun delete(invoiceId: String, imageId: String)
    fun edit(invoiceId: String)
}