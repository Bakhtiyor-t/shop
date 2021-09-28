package tairov.baxti.shop.firms

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Continuation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import tairov.baxti.shop.databinding.ActivityFirmDetailBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import android.widget.LinearLayout

import android.widget.Toast
import tairov.baxti.shop.dialogs.EditFirmDetailDialog
import java.io.ByteArrayOutputStream

class FirmDetail : AppCompatActivity(),
    EditFirmDetailDialog.EditFirmDetailDialogListener
{
    private lateinit var binding: ActivityFirmDetailBinding
    private var adapter = FirmDetailAdapter(this, initListeners())
    private lateinit var storageRef: StorageReference

    val editFirmDetailDialog = EditFirmDetailDialog()

    private val firmDetailKey: String = "firmDetail"
    private var firmDetail = Firm()
    private var invoicesList = ArrayList<Invoice>()

    private lateinit var db: FirebaseFirestore
//    private var downloadUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFirmDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference("invoices")

        firmDetail = intent.getSerializableExtra(firmDetailKey) as Firm

        binding.firmName.text = firmDetail.name
        binding.add.setOnClickListener {
//            addInvoice.launch(Intent(this, AddInvoice::class.java))
            val intent = Intent(this, AddInvoice::class.java)
            intent.putExtra(firmDetailKey, firmDetail)
            startActivity(intent)
        }

        binding.invoices.layoutManager = LinearLayoutManager(this)
        binding.invoices.adapter = adapter
        getFromDatabase()
    }

    private fun getFromDatabase(){
        db.collection("invoices").whereEqualTo("firmId", firmDetail.id)
            .addSnapshotListener { invoices, error ->
            if(error != null){
                Log.d("Mylog", "$error")
                return@addSnapshotListener
            }
            for (invoice in invoices!!){
                val inv = invoice.toObject<Invoice>()
                invoicesList.add(0, inv)
            }
            adapter.addAll(invoicesList)
            invoicesList.clear()
        }
    }

    private fun initListeners(): ClickFirmDetail {
        return object: ClickFirmDetail{
            override fun imageClick(imageUri: String) {
                val intent = Intent(this@FirmDetail, InvoiceImage::class.java)
//                val bitmap = (imageBitmap as BitmapDrawable).bitmap
//                val baos = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//                val data = baos.toByteArray()
//                intent.putExtra("imageBitmap", data)
                intent.putExtra("imageUri", imageUri)
                startActivity(intent)
                Toast.makeText(this@FirmDetail, "Image URL: $imageUri", Toast.LENGTH_SHORT).show()
            }

            override fun delete(invoiceId: String, imageId: String) {
                db.collection("invoices").document(invoiceId).delete()
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
        val totalDebt = dialog.binding.edTotalDebt.text.toString()

        if(payment.isNotEmpty())
            updatedData["payment"] = payment.toDouble()
        if (paidFor.isNotEmpty())
            updatedData["paidFor"] = paidFor.toDouble()
        if(previousDebt.isNotEmpty())
            updatedData["previousDebt"] = previousDebt.toDouble()
        if(totalDebt.isNotEmpty())
            updatedData["totalDebt"] = totalDebt.toDouble()

        db.collection("invoices")
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