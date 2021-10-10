package tairov.baxti.shop.expenses

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.databinding.ActivityExpensesBinding
import tairov.baxti.shop.dialogs.AddExpenseDialog
import tairov.baxti.shop.dialogs.SortDialog
import tairov.baxti.shop.firms.FirmsConsts
import java.time.*
import java.util.*
import kotlin.collections.ArrayList

class Expenses : AppCompatActivity(),
    AddExpenseDialog.AddExpenseDialogListener,
    SortDialog.SortDialogListener
{
    private lateinit var binding: ActivityExpensesBinding
    private lateinit var db: FirebaseFirestore
    private val expensesList = ArrayList<Expense>()
    private val expensesAdapter = ExpensesAdapter(initClickListeners())
    private val addExpenseDialog = AddExpenseDialog()
    private val sortDialog = SortDialog()
    private var totalExpense = 0.0
    private var localDateTime = Timestamp.now()
    private var finalDate = Timestamp.now()
    private var date = Timestamp.now()
    private val dayInSeconds:Long = 86400
    private val changedDates = mutableMapOf<String, Timestamp>()
//    @RequiresApi(Build.VERSION_CODES.O)
//    private val zoneSetting = ZoneId.ofOffset("UTC", ZoneOffset.of("+05"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        initAdapter()

        localDateTime = parseLocalDateTime()
        Log.d(MainConsts.LOG_TAG, "Local: $localDateTime")
        Log.d(MainConsts.LOG_TAG, "Date: $date")
        getFromDatabase()

        binding.add.setOnClickListener {
            addExpenseDialog.show(supportFragmentManager, "addExpenseDialog_tag")
        }
        binding.sort.setOnClickListener {
            sortDialog.show(supportFragmentManager, "sortDialog_tag")
        }
    }

    private fun initAdapter() {
        binding.expensesList.layoutManager = LinearLayoutManager(this)
        binding.expensesList.adapter = expensesAdapter
    }

    private fun initClickListeners(): ClickExpense {
        return object : ClickExpense {
            override fun onDelete(expenseId: String) {
                db.collection(ExpensesConsts.EXPENSES)
                    .document(expenseId).delete()
            }
        }
    }

    private fun parseLocalDateTime(dateTime: LocalDateTime? = null): Timestamp {
        return if (dateTime != null) {
            val dateToSeconds = dateTime.toInstant(ZoneOffset.UTC).epochSecond
            Timestamp(dateToSeconds, 0)
        } else{
            val localDate = LocalDate.now()
            val dateToSeconds = localDate.atStartOfDay().toInstant(ZoneOffset.UTC).epochSecond
            date = Timestamp(dateToSeconds+dayInSeconds, 0)
            Timestamp(dateToSeconds, 0)
        }
    }

    private fun getFromDatabase(){
        db.collection(ExpensesConsts.EXPENSES)
            .whereGreaterThanOrEqualTo(ExpensesConsts.DATE, localDateTime)
            .whereLessThanOrEqualTo(ExpensesConsts.DATE, date)
            .addSnapshotListener{ result, error ->
                totalExpense = 0.0
                if (error != null) {
                    Log.d(MainConsts.LOG_TAG, "$error")
                    return@addSnapshotListener
                }
                for (expenseItem in result!!){
                    val expense = expenseItem.toObject<Expense>()
                    totalExpense += expense.price
                    expensesList.add(expense)
                }
                expensesList.sortByDescending { item -> item.date }
                expensesAdapter.addAll(expensesList)
                expensesList.clear()
                binding.totalExpense.text = totalExpense.toString()
            }
    }

    private suspend fun getFirmId(firmName: String) =
        db.collection(FirmsConsts.FIRMS)
            .whereEqualTo(FirmsConsts.FIRM_NAME, firmName)
            .limit(1)
            .get()
            .await()
            .documents

    private fun setFromDatabase(expense: Expense){
        db.collection(ExpensesConsts.EXPENSES)
            .document(expense.id)
            .set(expense)
    }

    override fun done(dialog: AddExpenseDialog) {
        val firmName = dialog.binding.expenseName.text.toString()
        val amountExpense = dialog.binding.amount.text.toString().toDouble()
        val expenseId = db.collection(ExpensesConsts.EXPENSES).document().id

        if(dialog.binding.firmCheckBox.isChecked)
        {
            val expense = Expense(
                id=expenseId, name=firmName,
                price=amountExpense, date=finalDate,
                firms=true
            )
            CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
                val result = getFirmId(firmName)
                if(result.size > 0){
                    expense.firmId = result[0].id
                    Log.d(MainConsts.LOG_TAG, expense.firmId)
                }
                if(expense.firmId.isNotEmpty()){
                    setFromDatabase(expense)
                    dialog.dismiss()
                }else{
                    runOnUiThread {
                        Toast.makeText(
                            this@Expenses,
                            "Неверное название фирмы, попробуйте заново",
                            Toast.LENGTH_LONG).show()
                    }
//                    Handler(Looper.getMainLooper()).post {
//                        Toast.makeText(
//                            this@Expenses,
//                            "Неверное название фирмы, попробуйте заново",
//                            Toast.LENGTH_LONG).show()
//                    }
                }
            }
        }
        else{
            val expense = Expense(id=expenseId, name=firmName, price=amountExpense, date=finalDate)
            setFromDatabase(expense)
            dialog.dismiss()
        }
    }

    override fun changeDate(dialog: AddExpenseDialog) {
        addDate(dialog.binding.date)
    }

    private fun addDate(edDate: EditText){
        val calendar = Calendar.getInstance()
        val year1 = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val localDate = LocalDate.of(year, monthOfYear+1, dayOfMonth)
            val date = LocalDateTime.of(localDate, LocalTime.now())
            edDate.setText(date.format(FirmsConsts.DATE_TIME_FORMATTER))
            Log.d(MainConsts.LOG_TAG, "$date")
            finalDate = parseLocalDateTime(date)
            Log.d(MainConsts.LOG_TAG, "$finalDate")
        }, year1, month, day)
        dpd.show()
    }

    override fun dateDone(dialog: SortDialog) {
        localDateTime = changedDates[ExpensesConsts.CHANGED_DATE_1]!!
        date = changedDates[ExpensesConsts.CHANGED_DATE_2]!!
        getFromDatabase()
        dialog.dismiss()
    }

    override fun changeDate(editText: EditText) {
        datePicker(editText, ExpensesConsts.CHANGED_DATE_1)
    }

    override fun changeDate2(editText: EditText) {
        datePicker(editText, ExpensesConsts.CHANGED_DATE_2)
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
            dateTime = if(key == ExpensesConsts.CHANGED_DATE_1) {
                LocalDateTime.of(localDate, LocalTime.MIN)
            }else{
                LocalDateTime.of(localDate, LocalTime.MAX)
            }
            editText.setText(dateTime.format(FirmsConsts.DATE_TIME_FORMATTER))
            date = parseLocalDateTime(dateTime)
            changedDates[key] = date
            Log.d(MainConsts.LOG_TAG, "$date")
        }, year1, month, day)
        dpd.show()
    }
}