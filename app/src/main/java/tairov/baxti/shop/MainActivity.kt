package tairov.baxti.shop

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import tairov.baxti.shop.cashBox.CashBox
import tairov.baxti.shop.cashBox.CashBoxConsts
import tairov.baxti.shop.cashBox.CashBoxItem
import tairov.baxti.shop.shoppingList.ShoppingList
import tairov.baxti.shop.firms.Firms
import tairov.baxti.shop.databinding.ActivityMainBinding
import tairov.baxti.shop.debetors.Debtors
import tairov.baxti.shop.dialogs.SortDialog
import tairov.baxti.shop.expenses.Expense
import tairov.baxti.shop.expenses.Expenses
import tairov.baxti.shop.expenses.ExpensesConsts
import tairov.baxti.shop.firms.FirmsConsts
import tairov.baxti.shop.firms.firmDetail.Invoice
import tairov.baxti.shop.firms.firmDetail.InvoicesConsts
import java.time.*
import java.util.*

class MainActivity : AppCompatActivity(),
        SortDialog.SortDialogListener
{
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var realTimeDb: FirebaseDatabase
    private lateinit var realTimeDbRef: DatabaseReference

    private var totalIncomePrev = 0.0
    private var totalExpensePrev = 0.0
    private var totalPaidPrev = 0.0
    private var totalDebtPrev = 0.0

    private var fromDate = Timestamp.now()
    private var endDate = Timestamp.now()
    private val sortDialog = SortDialog()
    private val changedDates = mutableMapOf<String, Timestamp>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDate()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        realTimeDb = FirebaseDatabase.getInstance()
        realTimeDbRef = realTimeDb.reference

        binding.apply {
            profit.text = "0.0"
            income.text = "0.0"
            expenses.text = "0.0"
            debt.text = "0.0"
        }

        getFromDatabase()
        initClickListeners()

        binding.sort.setOnClickListener {
            sortDialog.show(supportFragmentManager, "sortDialog_tag")
        }
    }

    private fun initDate(){
        val localDate = LocalDate.now().minusDays(30).atStartOfDay()
        val localDate2 = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        fromDate = Timestamp(localDate.toInstant(ZoneOffset.UTC).epochSecond, 0)
        endDate = Timestamp(localDate2.epochSecond, 0)
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

    private fun setValues(
        expenses: Double? = null,
        income: Double? = null,
        debt: Double? = null,
    ){
        if (expenses != null){
            val expensesItem = binding.expenses.text.toString().toDouble()
            binding.expenses.text = (expensesItem+expenses).toString()
        }
        if (income != null){
            val incomeItem = binding.income.text.toString().toDouble()
            binding.income.text = (incomeItem+income).toString()
        }
        if (debt != null){
            val debtItem = binding.debt.text.toString().toDouble()
            binding.debt.text = (debtItem+debt).toString()
        }
    }

    private fun setProfit(){
        val totalIncome = binding.income.text.toString().toDouble()
        val totalDebt = binding.debt.text.toString().toDouble()
        binding.profit.text = (totalIncome-totalDebt).toString()
    }

    private fun getFromDatabase(){
        db.collection(InvoicesConsts.INVOICES)
            .whereGreaterThanOrEqualTo(InvoicesConsts.DATE, fromDate)
            .whereLessThanOrEqualTo(InvoicesConsts.DATE, endDate)
            .addSnapshotListener { results, error ->
                if(error != null){
                    Log.d(MainConsts.LOG_TAG, "$error")
                    return@addSnapshotListener
                }
                var totalPaid = 0.0
                var totalDebt = 0.0
                for (item in results!!){
                    val invoice = item.toObject<Invoice>()
                    totalPaid += invoice.paidFor
                    totalDebt += invoice.totalDebt
                }
                if(totalPaidPrev > 0 && totalDebtPrev > 0)
                    setValues(expenses=totalPaid-totalPaidPrev, debt=totalDebt-totalDebtPrev)
                else if(totalPaidPrev>0 && totalDebtPrev == 0.0)
                    setValues(expenses=totalPaid-totalPaidPrev, debt=totalDebt)
                else if (totalDebtPrev > 0 && totalPaidPrev == 0.0)
                    setValues(expenses=totalPaid, debt=totalDebt-totalDebtPrev)
                else
                    setValues(expenses=totalPaid, debt=totalDebt)
                totalPaidPrev = totalPaid
                totalDebtPrev = totalDebt
                setProfit()
            }

        db.collection(ExpensesConsts.EXPENSES)
            .whereGreaterThanOrEqualTo(ExpensesConsts.DATE, fromDate)
            .whereLessThanOrEqualTo(ExpensesConsts.DATE, endDate)
            .addSnapshotListener { results, error ->
                if(error != null){
                    Log.d(MainConsts.LOG_TAG, "$error")
                    return@addSnapshotListener
                }
                var totalExpenses = 0.0
                for (item in results!!){
                    val expenses = item.toObject<Expense>()
                    totalExpenses += expenses.price
                }
                if(totalExpensePrev > 0)
                    setValues(expenses=totalExpenses-totalExpensePrev)
                else
                    setValues(expenses=totalExpenses)
                totalExpensePrev = totalExpenses
            }

        db.collection(CashBoxConsts.CASH_BOX)
            .whereGreaterThanOrEqualTo(CashBoxConsts.DATE, fromDate)
            .whereLessThanOrEqualTo(CashBoxConsts.DATE, endDate)
            .addSnapshotListener { results, error ->
                if(error != null){
                    Log.d(MainConsts.LOG_TAG, "$error")
                    return@addSnapshotListener
                }
                var totalCashBox = 0.0
                for (item in results!!){
                    val cashBox = item.toObject<CashBoxItem>()
                    totalCashBox += cashBox.card
                    totalCashBox += cashBox.cash
                }
                if (totalIncomePrev > 0)
                    setValues(income=totalCashBox-totalIncomePrev)
                else
                    setValues(income=totalCashBox)
                totalIncomePrev = totalCashBox
                setProfit()
            }
    }

    override fun dateDone(dialog: SortDialog) {
        fromDate = changedDates[MainConsts.FROM_DATE]!!
        endDate = changedDates[MainConsts.END_DATE]!!
        getFromDatabase()
        dialog.dismiss()
    }

    override fun changeDate(editText: EditText) {
        datePicker(editText, MainConsts.FROM_DATE)
    }

    override fun changeDate2(editText: EditText) {
        datePicker(editText, MainConsts.END_DATE)
    }

    private fun datePicker(editText: EditText, key: String){
        val calendar = Calendar.getInstance()
        val year1 = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var dateTime: LocalDateTime
        var date: Timestamp

        val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val localDate = LocalDate.of(year, monthOfYear+1, dayOfMonth)
            dateTime = if(key == MainConsts.FROM_DATE) {
                LocalDateTime.of(localDate, LocalTime.MIN)
            }else{
                LocalDateTime.of(localDate, LocalTime.MAX)
            }
            editText.setText(dateTime.format(FirmsConsts.DATE_TIME_FORMATTER))
            date = Timestamp(dateTime.toInstant(ZoneOffset.UTC).epochSecond, 0)
            changedDates[key] = date
        }, year1, month, day)
        dpd.show()
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