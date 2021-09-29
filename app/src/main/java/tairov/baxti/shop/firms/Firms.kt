package tairov.baxti.shop.firms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityFirmsBinding
import tairov.baxti.shop.dialogs.AddItemToShoppingList
import tairov.baxti.shop.dialogs.EditFirmDialog
import tairov.baxti.shop.firms.firmDetail.FirmDetail
import tairov.baxti.shop.firms.firmDetail.InvoicesConsts

class Firms : AppCompatActivity(),
    EditFirmDialog.EditFirmDialogListener,
    AddItemToShoppingList.AddShoppingListItemListener
{
    private lateinit var binding: ActivityFirmsBinding
    private var adapter = FirmAdapter(initClickListeners())
    private val addNewFirm = AddItemToShoppingList()
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private val firms = ArrayList<Firm>()
    private val editFirmDialog = EditFirmDialog()
    private var totalPaidCount = 0.0
    private var totalDebtCount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addFirm.setOnClickListener {
//            addLauncher.launch(Intent(this, AddFirm::class.java))
            addNewFirm.title = getString(R.string.addFirm)
            addNewFirm.hint = getString(R.string.title)
            addNewFirm.show(supportFragmentManager, "addNewFirm_tag")
        }
        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference(InvoicesConsts.INVOICES)
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
                intent.putExtra(FirmsConsts.FIRM_DETAIL, firm)
                startActivity((intent))
            }

            override fun onEdit(firmId: String) {
                editFirmDialog.firmId = firmId
                editFirmDialog.show(supportFragmentManager, "editFirmDialog_tag")
            }

            override fun onDelete(firmId: String) {
                db.collection(FirmsConsts.FIRMS).document(firmId).delete()
                db.collection(InvoicesConsts.INVOICES)
                    .whereEqualTo(InvoicesConsts.FIRM_ID, firmId)
                    .get()
                    .addOnSuccessListener { snapshots ->
                        for(snapshot in snapshots){
                            storageRef.child(
                                "${snapshot[InvoicesConsts.IMAGE_ID].toString()}.jpg"
                            ).delete()
                            snapshot.reference.delete()
                        }
                    }
            }
        }
    }

    private fun getFromDatabase(){
        db.collection(FirmsConsts.FIRMS).addSnapshotListener { firmsList, error ->
            totalPaidCount = 0.0
            totalDebtCount = 0.0
            if(error != null){
                Log.d(MainConsts.LOG_TAG, "$error")
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

//    private val addLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()){
//        if(it.resultCode == RESULT_OK){
//            setFromDatabase(it.data?.getSerializableExtra("firm") as Firm)
//        }
//    }

    private fun setFromDatabase(firm: Firm){
        db.collection(FirmsConsts.FIRMS)
            .add(firm)
            .addOnSuccessListener { documentReference ->
                Log.d(MainConsts.LOG_TAG,
                    "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(MainConsts.LOG_TAG, "Error adding document", e)
            }
    }

    override fun done(dialog: EditFirmDialog, firmId: String) {
        val firmName = dialog.binding.firmName.text.toString()
//        val paid = dialog.binding.paid.text.toString()
//        val debt = dialog.binding.debt.text.toString()
        val updatingFirmField = mutableMapOf<String, Any>()

        if(firmName.isNotEmpty())
            updatingFirmField[FirmsConsts.FIRM_NAME] = firmName
//        if (paid.isNotEmpty())
//            updatingFirmField[FirmsConst.FIRM_PAY] = paid.toDouble()
//        if(debt.isNotEmpty())
//            updatingFirmField[FirmsConst.FIRM_DEBT] = debt.toDouble()

        db.collection(FirmsConsts.FIRMS).document(firmId).update(updatingFirmField)
        dialog.dismiss()
    }

    override fun add(dialog: AddItemToShoppingList) {
        val firmName = dialog.binding.newProductName.text.toString()
        setFromDatabase(Firm(name=firmName))
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