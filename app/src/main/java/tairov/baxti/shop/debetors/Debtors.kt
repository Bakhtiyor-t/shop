package tairov.baxti.shop.debetors

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import tairov.baxti.shop.databinding.ActivityDebtorsBinding
import tairov.baxti.shop.dialogs.AddDebtorDialog
import tairov.baxti.shop.dialogs.EditDebtorDialog
import kotlin.collections.ArrayList

class Debtors : AppCompatActivity(),
    EditDebtorDialog.EditDebtorDialogListener,
    AddDebtorDialog.AddDebtorDialogListener{
    private lateinit var binding: ActivityDebtorsBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var debtorsRef: DatabaseReference
    private var debtorsList = ArrayList<Debtor>()
    private val editDebtorDialog = EditDebtorDialog()
    private val addNewDebtorDialog = AddDebtorDialog()
    private var adapter = DebtorAdapter(initClickListeners())
    private var totalDebtCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDebtorsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance()
        debtorsRef = db.getReference(DebtorsConsts.DEBTORS_TEST)

        binding.debtorsList.layoutManager = LinearLayoutManager(this)
        binding.debtorsList.adapter = adapter

        binding.addDebtor.setOnClickListener {
            val fm = supportFragmentManager
            addNewDebtorDialog.show(fm, "addNewDebtorDialog_tag")
        }
        getDataFormDB()
    }

    private fun getDataFormDB(){
        debtorsRef.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                totalDebtCount = 0
                for(debtor in snapshot.children) {
                    val deb = debtor.getValue<Debtor>()
                    if (deb != null) {
                        deb.id = debtor.key.toString()
                        totalDebtCount += deb.debt
                        debtorsList.add(0, deb)
                    }
                }
                adapter.addAllDebtors(debtorsList)
                debtorsList.clear()
                binding.totalDebt.text = totalDebtCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initClickListeners(): ClickDebtorItem {
        return object : ClickDebtorItem {
            override fun onDelete(debtorId: String) {
                debtorsRef.child(debtorId).removeValue()
                Toast.makeText(baseContext, "Запись удалена", Toast.LENGTH_SHORT).show()
            }

            override fun onEdit(debtorId: String, debtorName: String) {
                val fm = supportFragmentManager
                editDebtorDialog.show(fm, "editDebtorDialog_tag")
                editDebtorDialog.debtorName = debtorName
                editDebtorDialog.debtorId = debtorId
            }
        }
    }

    override fun cancel(dialog: EditDebtorDialog) {
        editDebtorDialog.dismiss()
    }

    override fun done(dialog: EditDebtorDialog, debtorId: String) {
        val paid = dialog.binding.edPaid.text.toString()
        val debt = dialog.binding.edDebt.text.toString()
        if(paid.isNotEmpty()){
            debtorsRef.child(debtorId).child(DebtorsConsts.PAY).setValue(paid.toDouble())
            val dbDebt = debtorsRef.child(debtorId).child(DebtorsConsts.DEBT)
            dbDebt.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   val totalDebt = snapshot.value.toString().toInt() - paid.toInt()
                   debtorsRef.child(debtorId).child(DebtorsConsts.DEBT).setValue(totalDebt)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            Toast.makeText(baseContext, "Данные обновлены", Toast.LENGTH_SHORT).show()
        }
        if(debt.isNotEmpty()){
            debtorsRef.child(debtorId).child(DebtorsConsts.DEBT).setValue(debt.toDouble())
            Toast.makeText(baseContext, "Задолженность обновлена", Toast.LENGTH_SHORT).show()
        }
        editDebtorDialog.dismiss()
    }

    override fun addNewDebtor(dialog: AddDebtorDialog) {
        val debtorName = dialog.binding.edDebtorName.text.toString()
        val debtorDebt = dialog.binding.edNewDebt.text.toString().toInt()
        debtorsRef.push().setValue(Debtor(name = debtorName, debt = debtorDebt))
        addNewDebtorDialog.dismiss()
    }
}