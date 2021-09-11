package tairov.baxti.shop.shoppingList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityShoppingListBinding

class ShoppingList : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}