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
import tairov.baxti.shop.cashBox.CashBox
import tairov.baxti.shop.shoppingList.ShoppingList
import tairov.baxti.shop.firms.Firms
import tairov.baxti.shop.databinding.ActivityMainBinding
import tairov.baxti.shop.debetors.Debtors
import tairov.baxti.shop.expenses.Expenses
import tairov.baxti.shop.firms.firmDetail.InvoicesConsts

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
            startActivity(
                Intent(this, Firms::class.java)
            )
        }
        binding.debtors.setOnClickListener{
            startActivity(
                Intent(this, Debtors::class.java)
            )
        }
        binding.shoppingList.setOnClickListener{
            startActivity(
                Intent(this, ShoppingList::class.java)
            )
        }
        binding.expensesBtn.setOnClickListener {
            startActivity(
                Intent(this, Expenses::class.java)
            )
        }
        binding.cashBox.setOnClickListener {
            startActivity(
                Intent(this, CashBox::class.java)
            )
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
        db.collection(InvoicesConsts.INVOICES)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    totalPayment = 0.0
                    totalPreviousDebt = 0.0
                    totalPaid = 0.0
                    totalDebt = 0.0
                    for (snapshot in snapshots){
//                        totalPayment += snapshot["payment"].toString().toDouble()
                        totalPaid += snapshot[InvoicesConsts.PAID_FOR].toString().toDouble()
//                        totalPreviousDebt += snapshot["previousDebt"].toString().toDouble()
                        totalDebt += snapshot[InvoicesConsts.TOTAL_DEBT].toString().toDouble()
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