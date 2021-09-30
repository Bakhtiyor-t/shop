package tairov.baxti.shop.expenses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityExpensesBinding

class Expenses : AppCompatActivity() {
    private lateinit var binding: ActivityExpensesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}