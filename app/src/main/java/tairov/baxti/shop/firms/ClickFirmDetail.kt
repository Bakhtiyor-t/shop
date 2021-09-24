package tairov.baxti.shop.firms

import android.graphics.Bitmap

interface ClickFirmDetail {
    fun imageClick(imageUri: String, imageBitmap: Bitmap)
    fun delete(invoiceId: String, imageId: String)
}