package tairov.baxti.shop.firms

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityInvoiceImageBinding
import android.graphics.BitmapFactory
import com.squareup.picasso.Picasso


class InvoiceImage : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("imageUri")
        Picasso.get().load(imageUri).into(binding.imageView)


//        val b = BitmapFactory.decodeByteArray(
//            intent.getByteArrayExtra("imageBitmap"),
//            0,
//            intent.getByteArrayExtra("imageBitmap")!!.size
//        )

//        val imageBitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
//        binding.imageView.setImageBitmap(b)
    }
}