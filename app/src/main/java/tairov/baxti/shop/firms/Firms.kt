package tairov.baxti.shop.firms

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityFirmsBinding
import tairov.baxti.shop.dialogs.EditFirmDialog

class Firms : AppCompatActivity(),
    EditFirmDialog.EditFirmDialogListener
{
    private lateinit var binding: ActivityFirmsBinding
    private var adapter = FirmAdapter(initClickListeners())
    private lateinit var db: FirebaseFirestore
    private val firmsRef: String = "firms"
    private val firmDetail: String = "firmDetail"
    private val firms = ArrayList<Firm>()
    private val editFirmDialog = EditFirmDialog()
    private var totalPaidCount = 0.0
    private var totalDebtCount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addFirm.setOnClickListener {
            addLauncher.launch(Intent(this, AddFirm::class.java))
        }
        db = FirebaseFirestore.getInstance()
        initAdapter()
        getFromDatabase()
    }

    private fun initAdapter(){
        binding.firmsList.layoutManager = LinearLayoutManager(this)
        binding.firmsList.adapter = adapter
    }

    private fun initClickListeners(): ClickFirm {
        return object: ClickFirm {
            override fun onClick(firm: Firm) {
                val intent = Intent(this@Firms, FirmDetail::class.java)
                intent.putExtra(firmDetail, firm)
                startActivity((intent))
            }

            override fun onEdit(firmId: String) {
                editFirmDialog.firmId = firmId
                editFirmDialog.show(supportFragmentManager, "editFirmDialog_tag")
            }

            override fun onDelete(firmId: String) {
                db.collection(firmsRef).document(firmId).delete()
            }
        }
    }

    private fun getFromDatabase(){
        db.collection(firmsRef).addSnapshotListener { firmsList, error ->
            totalPaidCount = 0.0
            totalDebtCount = 0.0
            if(error != null){
                Log.d("Mylog", "$error")
                return@addSnapshotListener
            }
            for (firmItem in firmsList!!){
                val firm = firmItem.toObject<Firm>()
                firm.id = firmItem.id
                totalPaidCount += firm.pay
                totalDebtCount += firm.debt
                firms.add(0, firm)
            }
            adapter.addAllFirm(firms)
            firms.clear()
            binding.totalPaid.text = totalPaidCount.toString()
            binding.totalDebt.text = totalDebtCount.toString()
        }
    }

    //    private var editLauncher: ActivityResultLauncher<Intent>? = null
    private val addLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            setFromDatabase(it.data?.getSerializableExtra("firm") as Firm)
        }
    }

    private fun setFromDatabase(firm: Firm){
        db.collection("firms")
            .add(firm)
            .addOnSuccessListener { documentReference ->
                Log.d("Mylog", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Mylog", "Error adding document", e)
            }
    }

    override fun done(dialog: EditFirmDialog, firmId: String) {
        val firmName = dialog.binding.firmName.text.toString()
        val paid = dialog.binding.paid.text.toString()
        val debt = dialog.binding.debt.text.toString()
        val updatingFirmField = mutableMapOf<String, Any>()

        if(firmName.isNotEmpty())
            updatingFirmField["name"] = firmName
        if (paid.isNotEmpty())
            updatingFirmField["pay"] = paid.toDouble()
        if(debt.isNotEmpty())
            updatingFirmField["debt"] = debt.toDouble()

        db.collection(firmsRef).document(firmId).update(updatingFirmField)
        dialog.dismiss()
    }


//    @SuppressLint("CutPasteId")
//    private fun goToFirmDetail(item: View) {
//        val intent = Intent(this, FirmDetail::class.java)
//        val firmDetail = ArrayList<String>()
//        val d = item.findViewById<TextView>(R.id.debtorItemId)
//        Log.d("Mylog", "${d.text}")
//        firmDetail.add(item.findViewById<TextView>(R.id.tv_title).text.toString())
//        firmDetail.add(item.findViewById<TextView>(R.id.tv_title).text.toString())
//        firmDetail.add(item.findViewById<TextView>(R.id.tv_title).text.toString())
//        firmDetail.add(item.findViewById<TextView>(R.id.tv_title).text.toString())
//        intent.putStringArrayListExtra("firmDetail", firmDetail)
//        Log.d("Mylog", "$firmDetail")
//        startActivity((intent))
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putSerializable(firmsKey, adapter.firms)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        firms = savedInstanceState.getSerializable(firmsKey) as ArrayList<Firm>
//        adapter.addAllFirm(firms)
//    }

//    override fun onPause() {
//        super.onPause()
//        Log.d("Mylog", "onPause")
//    }

//    override fun onStop() {
//        super.onStop()
//        Bundle().putSerializable(firmsKey, adapter.firms)
//        super.onSaveInstanceState(Bundle())
//        Log.d("Mylog", "dsdsds: $firms")
//    }
//    fun add(){
//        adapter.addFirm(Firm(
//                "Blihhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhss8",
//                1234567.561,
//                1234567.561,
//        ))
//    }
}