package tairov.baxti.shop.firms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tairov.baxti.shop.databinding.ActivityFirmDetailBinding

class FirmDetail : AppCompatActivity() {
    private lateinit var binding: ActivityFirmDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFirmDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.textView.text = intent.getStringExtra("title")
    }

}