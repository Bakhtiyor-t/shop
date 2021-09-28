package tairov.baxti.shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import tairov.baxti.shop.auth.SignInOrSignUp
import tairov.baxti.shop.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser
    }

    override fun onResume(){
        super.onResume()
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        binding.mainLayout.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                if(currentUser != null) {
                    startActivity(Intent(this@StartActivity, MainActivity::class.java))
                }
                else {
                    startActivity(Intent(this@StartActivity, SignInOrSignUp::class.java))
                    finish()
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }
        })
    }
}