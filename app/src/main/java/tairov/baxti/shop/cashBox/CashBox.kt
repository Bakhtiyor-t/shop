package tairov.baxti.shop.cashBox

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityCashBoxBinding
import tairov.baxti.shop.expenses.Expense
import tairov.baxti.shop.firms.FirmsConsts
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class CashBox : AppCompatActivity(),
    AddCashBoxDialog.AddCashBoxDialogListener
{
    private lateinit var binding: ActivityCashBoxBinding
    private val cashBoxList = ArrayList<CashBoxItem>()
    private val cashBoxAdapter = CashBoxAdapter(initClickListeners())
    private lateinit var db: FirebaseFirestore
    private val addCashBoxDialog = AddCashBoxDialog()
    private var finalDate = Timestamp.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        initAdapter()
        getFromDatabase()

        binding.add.setOnClickListener {
            addCashBoxDialog.show(supportFragmentManager, "addCashBoxDialog_tag")
        }
    }

    private fun initAdapter() {
        binding.cashBoxList.layoutManager = LinearLayoutManager(this)
        binding.cashBoxList.adapter = cashBoxAdapter
    }

    private fun initClickListeners(): CashBoxClick {
        return object: CashBoxClick {
            override fun onDelete(cashBoxId: String) {
                db.collection(CashBoxConsts.CASH_BOX)
                    .document(cashBoxId)
                    .delete()
            }
        }
    }

    private fun getFromDatabase(){
        db.collection(CashBoxConsts.CASH_BOX)
            .addSnapshotListener { result, error ->
                if (error != null) {
                    Log.d(MainConsts.LOG_TAG, "$error")
                    return@addSnapshotListener
                }
                for (expenseItem in result!!){
                    val cashBoxItem = expenseItem.toObject<CashBoxItem>()
                    cashBoxList.add(cashBoxItem)
                }
                cashBoxList.sortByDescending { item -> item.date }
                cashBoxAdapter.addAll(cashBoxList)
                cashBoxList.clear()
            }
    }

    private fun setFromDatabase(cashBoxItem: CashBoxItem){
        db.collection(CashBoxConsts.CASH_BOX)
            .document(cashBoxItem.id)
            .set(cashBoxItem)
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Данные успешно сохранены", Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this, "Что-то пошло не так попробуйте ещё раз", Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun changeDate(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year1 = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val localDate = LocalDate.of(year, monthOfYear+1, dayOfMonth)
            val date = LocalDateTime.of(localDate, LocalTime.now())
            editText.setText(date.format(FirmsConsts.DATE_TIME_FORMATTER))
            Log.d(MainConsts.LOG_TAG, "$date")
            val dateToSeconds = date.toInstant(ZoneOffset.UTC).epochSecond
            finalDate = Timestamp(dateToSeconds, 0)
            Log.d(MainConsts.LOG_TAG, "$finalDate")
        }, year1, month, day)
        dpd.show()
    }

    override fun done(dialog: AddCashBoxDialog) {
        val cash = dialog.binding.cash.text.toString().toDouble()
        val card = dialog.binding.card.text.toString().toDouble()
        val id = db.collection(CashBoxConsts.CASH_BOX).document().id

        setFromDatabase(CashBoxItem(id=id, cash=cash, card=card, date=finalDate))
        dialog.dismiss()
    }
}