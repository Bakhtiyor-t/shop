package tairov.baxti.shop.firms.firmDetail

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import tairov.baxti.shop.MainConsts
import tairov.baxti.shop.databinding.ActivityAddInvoiceBinding
import java.io.ByteArrayOutputStream
import java.util.*
import tairov.baxti.shop.firms.Firm
import tairov.baxti.shop.firms.FirmsConsts
import java.time.LocalDate


class AddInvoice : AppCompatActivity() {
    private lateinit var binding: ActivityAddInvoiceBinding
    private lateinit var storageRef: StorageReference

    private lateinit var db: FirebaseFirestore
    private var downloadUri: Uri? = null
    private var firmDetail = Firm()

    private var imageUri: Uri? = null
    private lateinit var date: LocalDate
    private val PERMISSION_CODE = 1000

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().getReference(InvoicesConsts.INVOICES)
        firmDetail = intent.getSerializableExtra(FirmsConsts.FIRM_DETAIL) as Firm

        binding.addImage.setOnClickListener {
            checkPermissions()
        }
        binding.add.setOnClickListener {
            addNewInvoice()
        }
        binding.addNewDate.setOnClickListener {
            addDate()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (imageUri != null)
            binding.addImage.setImageURI(imageUri)
    }

    private fun checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else{
                //permission already granted
                openCamera()
            }
        }
        else{
            //system os is < marshmallow
            openCamera()
        }
    }

    private fun openCamera(){
//        val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivity(intent)
//        addLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_CODE){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDate(){
        val calendar = Calendar.getInstance()
        val year1 = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            date = LocalDate.of(year, monthOfYear, dayOfMonth)
            binding.edDate.setText(date.format(FirmsConsts.FORMATTER))
        }, year1, month, day)
        dpd.show()
    }

    private fun addNewInvoice(){
        if(!isFieldEmpty()){
            val payment = binding.edPayment.text.toString().toDouble()
            val paidFor = binding.edPaidFor.text.toString().toDouble()
            val previousDebt = binding.edPreviousDebt.text.toString().toDouble()
            val totalDebt = payment - paidFor + previousDebt
            val invoice = Invoice(
                payment = payment,
                paidFor = paidFor,
                previousDebt = previousDebt,
                totalDebt = if(binding.edTotalDebt.text.isNullOrEmpty()) totalDebt
                            else binding.edTotalDebt.text.toString().toDouble(),
                date = date
            )
            uploadInvoiceImage(invoice)
            finish()
        }
    }

    private fun uploadInvoiceImage(invoice: Invoice){
        val imageId = UUID.randomUUID()
        val invoiceRef = storageRef.child("$imageId.jpg")

        val bitmap = (binding.addImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos)
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
                val invoiceId = db.collection(InvoicesConsts.INVOICES).document().id
                invoice.id = invoiceId
                invoice.firmId = firmDetail.id
                invoice.imageUri = downloadUri.toString()
                invoice.imageId = imageId.toString()
                setFromDatabase(invoice)
            }
        }
    }

    private fun setFromDatabase(invoice: Invoice){
        db.collection(InvoicesConsts.INVOICES).document(invoice.id)
            .set(invoice)
            .addOnSuccessListener {
                Log.d(MainConsts.LOG_TAG, "DocumentSnapshot added with ID: ")
            }
            .addOnFailureListener { e ->
                Log.w(MainConsts.LOG_TAG, "Error adding document", e)
            }
    }

    private fun isFieldEmpty(): Boolean{
        binding.apply {
            if(imageUri == null){
                Toast.makeText(
                    this@AddInvoice,
                    "Пожалуйста выберите Изображения",
                    Toast.LENGTH_LONG
                ).show()
            }
            if(edPayment.text.isNullOrEmpty()) edPayment.error = "Поле должно быть заполнено"
            if(edPaidFor.text.isNullOrEmpty()) edPaidFor.error = "Поле должно быть заполнено"
            if(edPreviousDebt.text.isNullOrEmpty()) edPreviousDebt.error = "Поле должно быть заполнено"
//            if(edTotalDebt.text.isNullOrEmpty()) edTotalDebt.error = "Поле должно быть заполнено"
            if(edDate.text.isNullOrEmpty()) edDate.error = "Поле должно быть заполнено"
            return  (
                    edPayment.text.isNullOrEmpty() ||
                            edPaidFor.text.isNullOrEmpty() ||
                            edPreviousDebt.text.isNullOrEmpty() ||
//                            edTotalDebt.text.isNullOrEmpty() ||
                            edDate.text.isNullOrEmpty() ||
                            imageUri == null
                    )
        }
    }


    //    private val addLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()){ _ ->
//        Log.d("Mylog", "aaaa $imageUri")
//        binding.addImage.setImageURI(imageUri)
////        if(result.resultCode == RESULT_OK && result != null){
////            if(result.data != null){
////                val bitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
////                Log.d("Mylog", "sssss: $bitmap")
////                binding.addImage.setImageBitmap(bitmap)
////                bitmap = result.data!!.extras!!.get("data") as Bitmap
////                binding.addImage.setImageBitmap(bitmap)
////                imageUri = result.data!!.data
////                imageUri = result.data!!.data
////            }
////        }
//    }
}