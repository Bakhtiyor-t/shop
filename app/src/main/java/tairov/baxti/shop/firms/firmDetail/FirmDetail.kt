package tairov.baxti.shop.firms.firmDetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import tairov.baxti.shop.databinding.ActivityFirmDetailBinding
import java.util.*
import kotlin.collections.ArrayList

import android.widget.Toast
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.dialogs.EditFirmDetailDialog
import tairov.baxti.shop.firms.Firm
import tairov.baxti.shop.firms.FirmsConsts

class FirmDetail : AppCompatActivity(),
    EditFirmDetailDialog.EditFirmDetailDialogListener
{
    private lateinit var binding: ActivityFirmDetailBinding
    private var adapter = FirmDetailAdapter(this, initListeners())
    private lateinit var storageRef: StorageReference

    val editFirmDetailDialog = EditFirmDetailDialog()

    private var firmDetail = Firm()
    private var invoicesList = ArrayList<Invoice>()

    private lateinit var db: FirebaseFirestore
//    private var downloadUri: Uri? = null
    private var totalPaid = 0.0
    private var totalDebt = 0.0
    private val updateFirmData = mutableMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFirmDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference(InvoicesConsts.INVOICES)

        firmDetail = intent.getSerializableExtra(FirmsConsts.FIRM_DETAIL) as Firm

        binding.firmName.text = firmDetail.name
        binding.add.setOnClickListener {
//            addInvoice.launch(Intent(this, AddInvoice::class.java))
            val intent = Intent(this, AddInvoice::class.java)
            intent.putExtra(FirmsConsts.FIRM_DETAIL, firmDetail)
            startActivity(intent)
        }

        binding.invoices.layoutManager = LinearLayoutManager(this)
        binding.invoices.adapter = adapter
        getFromDatabase()
    }

    private fun getFromDatabase(){
        db.collection(InvoicesConsts.INVOICES).whereEqualTo(InvoicesConsts.FIRM_ID, firmDetail.id)
            .orderBy(InvoicesConsts.DATE)
            .addSnapshotListener { invoices, error ->
            totalPaid = 0.0
            totalDebt = 0.0
            if(error != null){
                Log.d(MainConsts.LOG_TAG, "$error")
                return@addSnapshotListener
            }
            for (invoice in invoices!!){
                val inv = invoice.toObject<Invoice>()
                totalPaid += inv.paidFor
                totalDebt += inv.totalDebt
                invoicesList.add(0, inv)
            }
            adapter.addAll(invoicesList)
            invoicesList.clear()
            updateFirmData[FirmsConsts.FIRM_PAY] = totalPaid
            updateFirmData[FirmsConsts.FIRM_DEBT] = totalDebt
            updateFirm(updateFirmData)
        }
    }

    private fun updateFirm(data: MutableMap<String, Any>){
        db.collection(FirmsConsts.FIRMS).document(firmDetail.id)
            .update(data)
    }

    private fun initListeners(): ClickFirmDetail {
        return object: ClickFirmDetail {
            override fun imageClick(imageUri: String) {
                val intent = Intent(this@FirmDetail, InvoiceImage::class.java)
//                val bitmap = (imageBitmap as BitmapDrawable).bitmap
//                val baos = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                val data = baos.toByteArray()
//                intent.putExtra("imageBitmap", data)
                intent.putExtra(InvoicesConsts.IMAGE_URI, imageUri)
                startActivity(intent)
                Toast.makeText(this@FirmDetail, "Image URL: $imageUri", Toast.LENGTH_SHORT).show()
            }

            override fun delete(invoiceId: String, imageId: String) {
                db.collection(InvoicesConsts.INVOICES).document(invoiceId).delete()
                storageRef.child("$imageId.jpg").delete()
            }

            override fun edit(invoiceId: String) {
                val fm = supportFragmentManager
                editFirmDetailDialog.invoiceId = invoiceId
                editFirmDetailDialog.show(fm, "editFirmDetailDialog")
            }
        }
    }

    override fun done(dialog: EditFirmDetailDialog) {
        val updatedData = mutableMapOf<String, Any>()
        val payment = dialog.binding.edPayment.text.toString()
        val paidFor = dialog.binding.edPaidFor.text.toString()
        val previousDebt = dialog.binding.edPreviousDebt.text.toString()
//        val totalDebt = dialog.binding.edTotalDebt.text.toString()

//        if(payment.isNotEmpty())
//            updatedData["payment"] = payment.toDouble()
//        if (paidFor.isNotEmpty())
//            updatedData["paidFor"] = paidFor.toDouble()
//        if(previousDebt.isNotEmpty())
//            updatedData["previousDebt"] = previousDebt.toDouble()
//        if(totalDebt.isNotEmpty())
//            updatedData["totalDebt"] = totalDebt.toDouble()

        updatedData[InvoicesConsts.PAYMENT] = payment.toDouble()
        updatedData[InvoicesConsts.PAID_FOR] = paidFor.toDouble()
        updatedData[InvoicesConsts.PREVIOUS_DEBT] = previousDebt.toDouble()
        updatedData[InvoicesConsts.TOTAL_DEBT] = payment.toDouble() +
                                   previousDebt.toDouble() -
                                   paidFor.toDouble()

        db.collection(InvoicesConsts.INVOICES)
            .document(dialog.invoiceId)
            .update(updatedData)

        dialog.dismiss()
    }

//    private val addInvoice = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ){ result ->
//        if (result.resultCode == RESULT_OK){
//            val invoice = result.data?.getSerializableExtra("invoice") as Invoice
//            Log.d("Mylog", invoice.toString())
//            uploadInvoiceImage(invoice)
//
//        }
//    }

//    private fun uploadInvoiceImage(invoice: Invoice){
//        val imageId = UUID.randomUUID()
//        val invoiceRef = storageRef.child("$imageId.jpg")
////        val imagaUri = invoice.imageUri.toUri()
////        val fileName = File(imagaUri.path!!)
////        val compressedImageFile = Compressor.compress(this, fileName) {
////            quality(80)
////            format(Bitmap.CompressFormat.JPEG)
////        }
//
//        val uploadTask = invoiceRef.putFile(invoice.imageUri.toUri())
//        val urlTask = uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            invoiceRef.downloadUrl
//        }.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                downloadUri = task.result
//                val invoiceId = db.collection("invoices").document().id
//                invoice.id = invoiceId
//                invoice.firmId = firmDetail.id
//                invoice.imageUri = downloadUri.toString()
//                setFromDatabase(invoice)
//            }
//        }
//    }

//
//    private fun setFromDatabase(invoice: Invoice){
//        db.collection("invoices").document(invoice.id)
//            .set(invoice)
//            .addOnSuccessListener {
//                Log.d("Mylog", "DocumentSnapshot added with ID: ")
//            }
//            .addOnFailureListener { e ->
//                Log.w("Mylog", "Error adding document", e)
//            }
//    }
}