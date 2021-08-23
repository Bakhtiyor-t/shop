package tairov.baxti.shop.firms

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import tairov.baxti.shop.R
import tairov.baxti.shop.databinding.ActivityFirmsBinding

class Firms : AppCompatActivity() {
    private lateinit var binding: ActivityFirmsBinding
    var data = arrayListOf(
        Firm(
            "Blihhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhss8",
            1234567.561,
            1234567.561,
        ),
    )


    private var adapter = FirmAdapter(object: ClickFirm {
        override fun onClick(item: View) {
            goToFirmDetail(item)
        }
    })

    @SuppressLint("CutPasteId")
    private fun goToFirmDetail(item: View) {
        val intent = Intent(this, FirmDetail::class.java)
        val firmTitle = item.findViewById<TextView>(R.id.tv_title).text
        val firmId = item.findViewById<TextView>(R.id.tv_title).text
        intent.putExtra("title","$firmId : $firmTitle")
        startActivity((intent))
    }

    //    private var editLauncher: ActivityResultLauncher<Intent>? = null
    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            adapter.addFirm(it.data?.getSerializableExtra("firm") as Firm)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            firmsList.layoutManager = LinearLayoutManager(this@Firms)
            firmsList.adapter = adapter
            adapter.firmList.addAll(data)
            addFirm.setOnClickListener {
                editLauncher.launch(Intent(this@Firms, AddFirm::class.java))
            }
        }
    }
}