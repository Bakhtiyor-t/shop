package tairov.baxti.shop.firms

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityInvoiceImageBinding

class InvoiceImage : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageBitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        binding.imageView.setImageBitmap(imageBitmap)
    }
}