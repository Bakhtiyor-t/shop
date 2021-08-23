package tairov.baxti.shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tairov.baxti.shop.firms.Firms
import tairov.baxti.shop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val data = arrayOf(231454444,4554)
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val f = 0
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.profit.text = (data[0] - data[1]).toString()
        binding.income.text = data[0].toString()
        binding.consumption.text = data[1].toString()
        binding.firms.setOnClickListener{
            val firms = Intent(this, Firms::class.java)
            startActivity(firms)
        }
    }
}