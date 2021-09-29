package tairov.baxti.shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import tairov.baxti.shop.shoppingList.ShoppingList
import tairov.baxti.shop.firms.Firms
import tairov.baxti.shop.databinding.ActivityMainBinding
import tairov.baxti.shop.debetors.Debtors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var realTimeDb: FirebaseDatabase
    private lateinit var realTimeDbRef: DatabaseReference

    private var totalPayment = 0.0
    private var totalPreviousDebt = 0.0
    private var totalPaid = 0.0
    private var totalProfit = 0.0
    private var totalDebt = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        realTimeDb = FirebaseDatabase.getInstance()
        realTimeDbRef = realTimeDb.reference

        getFromDatabase()
        initClickListeners()
    }

    private fun initClickListeners(){
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
        binding.expensesBtn.setOnClickListener {

        }
        binding.cashBox.setOnClickListener {

        }
        binding.logOut.setOnClickListener {
            logOut()
        }
    }

    private fun setValues(){
        binding.profit.text = totalProfit.toString()
        binding.expenses.text = totalPaid.toString()
        binding.debt.text = totalDebt.toString()
    }

    private fun getFromDatabase(){
        db.collection("invoices")
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    totalPayment = 0.0
                    totalPreviousDebt = 0.0
                    totalPaid = 0.0
                    totalDebt = 0.0
                    for (snapshot in snapshots){
//                        totalPayment += snapshot["payment"].toString().toDouble()
                        totalPaid += snapshot["paidFor"].toString().toDouble()
//                        totalPreviousDebt += snapshot["previousDebt"].toString().toDouble()
                        totalDebt += snapshot["totalDebt"].toString().toDouble()
                    }
                    setValues()
                }
            }
    }

    private fun logOut(){
        auth.signOut()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}