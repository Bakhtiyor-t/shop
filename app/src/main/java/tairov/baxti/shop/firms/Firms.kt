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
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityFirmsBinding

class Firms : AppCompatActivity() {
    private lateinit var binding: ActivityFirmsBinding
    private var adapter = FirmAdapter(initClickListeners())
    private lateinit var db: FirebaseDatabase
    private lateinit var firmsRef: DatabaseReference
    private val firms = ArrayList<Firm>()
    var firmsList = arrayListOf(
        Firm(
            "1234",
            "Blihhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhss8",
            1234567.561,
            1234567.561,
        ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addFirm.setOnClickListener {
            editLauncher.launch(Intent(this, AddFirm::class.java))
        }
        initAdapter()
        getFromDatabase()
    }

    private fun initAdapter(){
        binding.firmsList.layoutManager = LinearLayoutManager(this@Firms)
        binding.firmsList.adapter = adapter
    }

    private fun initClickListeners(): ClickFirm {
        return object: ClickFirm {
            override fun onClick(item: View) {
                goToFirmDetail(item)
            }
        }
    }

    private fun getFromDatabase(){
        firmsRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(firmItem in snapshot.children){
                    val firm = firmItem.getValue<Firm>()
                    if (firm != null) {
                        firm.id = firmItem.key.toString()
                        firms.add(firm)
                    }
                }
                adapter.addAllFirm(firms)
                firms.clear()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    @SuppressLint("CutPasteId")
    private fun goToFirmDetail(item: View) {
        val intent = Intent(this, FirmDetail::class.java)
        val firmDetail = ArrayList<String>()
        val d = item.findViewById<TextView>(R.id.debtorItemId)
        Log.d("Mylog", "${d.text}")
        firmDetail.add(item.findViewById<TextView>(R.id.tv_title).text.toString())
        firmDetail.add(item.findViewById<TextView>(R.id.tv_title).text.toString())
        firmDetail.add(item.findViewById<TextView>(R.id.tv_title).text.toString())
        firmDetail.add(item.findViewById<TextView>(R.id.tv_title).text.toString())
        intent.putStringArrayListExtra("firmDetail", firmDetail)
        Log.d("Mylog", "$firmDetail")
        startActivity((intent))
    }

    //    private var editLauncher: ActivityResultLauncher<Intent>? = null
    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            adapter.addFirm(it.data?.getSerializableExtra("firm") as Firm)
        }
    }

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