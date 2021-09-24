package tairov.baxti.shop.firms

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityAddInvoiceBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.zip.DataFormatException
import android.os.Environment
import androidx.core.net.toFile


class AddInvoice : AppCompatActivity() {
    private lateinit var binding: ActivityAddInvoiceBinding
    private lateinit var storageRef: StorageReference

    private lateinit var db: FirebaseFirestore
    private var downloadUri: Uri? = null

    private val firmDetailKey: String = "firmDetail"
    private var firmDetail = Firm()

    private var imageUri: Uri? = null
    private var date: String = ""
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference("invoices")
        firmDetail = intent.getSerializableExtra(firmDetailKey) as Firm

        binding.addImage.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            val mUri = Uri.fromFile(File("/document/photo.jpeg"))
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            addLauncher.launch(intent)
        }
        binding.add.setOnClickListener {
            addNewInvoice()
        }

        binding.addNewDate.setOnClickListener {
            addDate()
        }
    }

    private fun addDate(){
        val calendar = Calendar.getInstance()
        val year1 = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
//                date = LocalDate.of(year, monthOfYear, dayOfMonth)
            date = "$dayOfMonth/$monthOfYear/$year"
            Log.d("Mylog", date)
            binding.edDate.setText(date)
        }, year1, month, day)
        dpd.show()
    }

    private fun addNewInvoice(){
        if(!isFieldEmpty()){
            val invoice = Invoice(
                id = "",
                firmId = "",
                payment = binding.edPayment.text.toString().toDouble(),
                paidFor = binding.edPaidFor.text.toString().toDouble(),
                previousDebt = binding.edPreviousDebt.text.toString().toDouble(),
                totalDebt = binding.edTotalDebt.text.toString().toDouble(),
                date = date
            )

            uploadInvoiceImage(invoice)
            finish()
//            val intent = Intent().apply {
//                putExtra("invoice", invoice)
//            }
//            setResult(RESULT_OK, intent)
//            finish()
        }
    }

    private lateinit var bitmap: Bitmap
    private val addLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK && result != null){
            if(result.data != null){
                bitmap = result.data!!.extras!!.get("data") as Bitmap
                binding.addImage.setImageBitmap(bitmap)
                imageUri = "sad".toUri()
//                binding.addImage.setImageURI(result.data!!.data)
//                imageUri = result.data!!.data
//                imageUri = result.data!!.data
            }
        }else{
            Log.d("Mylog", "result: $result")
        }
    }

    private fun uploadInvoiceImage(invoice: Invoice){
        val imageId = UUID.randomUUID()
        val invoiceRef = storageRef.child("$imageId.jpg")

//        val bitmap = (binding.addImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = invoiceRef.putBytes(data)
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            invoiceRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUri = task.result
                val invoiceId = db.collection("invoices").document().id
                invoice.id = invoiceId
                invoice.firmId = firmDetail.id
                invoice.imageUri = downloadUri.toString()
                invoice.imageId = imageId.toString()
                setFromDatabase(invoice)
            }
        }
    }

    private fun setFromDatabase(invoice: Invoice){
        db.collection("invoices").document(invoice.id)
            .set(invoice)
            .addOnSuccessListener {
                Log.d("Mylog", "DocumentSnapshot added with ID: ")
            }
            .addOnFailureListener { e ->
                Log.w("Mylog", "Error adding document", e)
            }
    }

    private fun isFieldEmpty(): Boolean{
        var imageFlag = false
        binding.apply {
            if(imageUri == null){
                Toast.makeText(
                    this@AddInvoice,
                    "Пожалуйста выберите Изображения",
                    Toast.LENGTH_LONG
                ).show()
                imageFlag = true
            }
            if(edPayment.text.isNullOrEmpty()) edPayment.error = "Поле должно быть заполнено"
            if(edPaidFor.text.isNullOrEmpty()) edPaidFor.error = "Поле должно быть заполнено"
            if(edPreviousDebt.text.isNullOrEmpty()) edPreviousDebt.error = "Поле должно быть заполнено"
            if(edTotalDebt.text.isNullOrEmpty()) edTotalDebt.error = "Поле должно быть заполнено"
            if(edDate.text.isNullOrEmpty()) edDate.error = "Поле должно быть заполнено"
            return  (
                    edPayment.text.isNullOrEmpty() ||
                            edPaidFor.text.isNullOrEmpty() ||
                            edPreviousDebt.text.isNullOrEmpty() ||
                            edTotalDebt.text.isNullOrEmpty() ||
                            edDate.text.isNullOrEmpty() ||
                            imageFlag
                    )
        }
    }
}