package tairov.baxti.shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import tairov.baxti.shop.ShoppingList.ShoppingList
import tairov.baxti.shop.auth.SignInOrSignUp
import tairov.baxti.shop.firms.Firms
import tairov.baxti.shop.databinding.ActivityMainBinding
import tairov.baxti.shop.debetors.Debtors

class MainActivity : AppCompatActivity() {
    val data = arrayOf(231454444,4554)
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.profit.text = (data[0] - data[1]).toString()
        binding.income.text = data[0].toString()
        binding.consumption.text = data[1].toString()
        binding.firms.setOnClickListener{
            val firms = Intent(this, Firms::class.java)
            startActivity(firms)
        }
        binding.debtors.setOnClickListener{
            val debtors = Intent(this, Debtors::class.java)
            startActivity(debtors)
        }
        binding.shoppingList.setOnClickListener{
            val shoppingList = Intent(this, ShoppingList::class.java)
            startActivity(shoppingList)
        }

        binding.logOut.setOnClickListener {
            logOut()
        }
    }

    private fun logOut(){
        auth.signOut()
        updateUI()
    }

    private fun updateUI(){
        finish()
    }

}