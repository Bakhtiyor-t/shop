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
import com.google.firebase.database.ktx.getValue
import tairov.baxti.shop.databinding.ActivityDebtorsBinding
import tairov.baxti.shop.dialogs.EditDebtorDialog
import kotlin.collections.ArrayList

class Debtors : AppCompatActivity(), EditDebtorDialog.EditDebtorDialogListener {
    private lateinit var binding: ActivityDebtorsBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var debtorsRef: DatabaseReference
    private var debtorsList = ArrayList<Debtor>()
    private val editDebtorDialog = EditDebtorDialog()

    private var adapter = DebtorAdapter(initClickListeners())

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDebtorsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance("https://shop-15b50-default-rtdb.europe-west1.firebasedatabase.app")
        debtorsRef = db.getReference("Debtors")

        binding.debtorsList.layoutManager = LinearLayoutManager(this)
        binding.debtorsList.adapter = adapter

        binding.addDebtor.setOnClickListener {
            val debtor = Debtor("ghgh", "Sohiba", 123000.0, 230000.0)
            debtorsRef.push().setValue(debtor)
//            adapter.addDebtor(debtor)
        }
        getDataFormDB()
    }

    private fun getDataFormDB(){
        debtorsRef.addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Mylog", "${snapshot.childrenCount}")

                Log.d("Mylog", "--------------------------------------------------")
                for(debtor in snapshot.children) {

                    Log.d("Mylog", "${debtor.key}")
                    val deb = debtor.getValue<Debtor>()
                    if (deb != null) {
                        deb.id = debtor.key.toString()
                        Log.d("Mylog", "$deb")
                        debtorsList.add(deb)
                    }
                }
                Log.d("Mylog", "--------------------------------------------------")
                adapter.addAllDebtors(debtorsList)
                debtorsList.clear()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initClickListeners(): ClickDebtorItem {
        return object : ClickDebtorItem {
            override fun onDelete(itemId: String) {
                debtorsRef.child(itemId).removeValue()
                Toast.makeText(baseContext, "Запись удалена", Toast.LENGTH_SHORT).show()
            }

            override fun onEdit(itemId: String) {
                val fm = supportFragmentManager
                editDebtorDialog.show(fm, "editDebtorDialog_tag")
                Toast.makeText(baseContext, "Edit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun cancel(dialog: DialogFragment) {
        Toast.makeText(baseContext, "Exit!", Toast.LENGTH_SHORT).show()
        editDebtorDialog.dismiss()
    }

    override fun done(dialog: DialogFragment) {
        Toast.makeText(baseContext, "Done!", Toast.LENGTH_SHORT).show()
        editDebtorDialog.dismiss()
    }
}