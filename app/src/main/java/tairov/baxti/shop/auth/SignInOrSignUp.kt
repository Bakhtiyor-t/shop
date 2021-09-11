package tairov.baxti.shop.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.intellij.lang.annotations.Language
import tairov.baxti.shop.MainActivity
import tairov.baxti.shop.R
import tairov.baxti.shop.R.*
import tairov.baxti.shop.databinding.ActivitySignInOrSignUpBinding

class SignInOrSignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignInOrSignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInOrSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
//        binding.maiLayout.startAnimation(animation)
//        animation.setAnimationListener(object : Animation.AnimationListener{
//            override fun onAnimationStart(p0: Animation?) {
//
//            }
//
//            override fun onAnimationEnd(p0: Animation?) {
//                startActivity(Intent(this@SignInOrSignUp, Language::class.java))
//            }
//
//            override fun onAnimationRepeat(p0: Animation?) {
//
//            }
//        })

        auth = FirebaseAuth.getInstance()
//        binding.maiLayout.visibility = View.GONE
//        binding.maiLayout.setBackgroundColor(ContextCompat.getColor(this, color.black))
        binding.signInOrSignUp.setOnClickListener {
            singInOrSingUpSelect()
        }

        binding.signInOrSignUpBtn.setOnClickListener {
            if(!isFieldEmpty() && binding.confirmPassword.visibility == View.GONE){
                signIn(binding.email.text.toString(), binding.password.text.toString())
            }else if(!isFieldEmpty()){
                Toast.makeText(this, "Регистрация пока не доступно", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI()
        }
//        else{
//            binding.maiLayout.visibility = View.VISIBLE
//        }
    }

    private fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    updateUI()
                }else{
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(){
        binding.email.text = null
        binding.password.text = null
        binding.confirmPassword.text = null
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun singInOrSingUpSelect(){
        if (binding.confirmPassword.visibility != View.GONE){
            binding.signInOrSignUpTitle.text = getString(string.sing_in)
            binding.signInOrSignUp.text = getString(string.singUp)
            binding.signInOrSignUpBtn.text = getString(string.sing_in)
            binding.confirmPassword.text = null
            binding.confirmPassword.visibility = View.GONE
        } else{
            binding.signInOrSignUpTitle.text = getString(string.sing_up)
            binding.signInOrSignUp.text = getString(string.singIn)
            binding.signInOrSignUpBtn.text = getString(string.sing_up)
            binding.confirmPassword.visibility = View.VISIBLE
        }
    }

    private fun isFieldEmpty(): Boolean{
        binding.apply {
            var passwordConfirm = false

            if(email.text.isNullOrEmpty()) email.error = "Поле должно быть заполнено"
            if(password.text.isNullOrEmpty()) password.error = "Поле должно быть заполнено"

            if(confirmPassword.visibility == View.GONE) passwordConfirm = false
            if(confirmPassword.visibility != View.GONE && confirmPassword.text.isNullOrEmpty()){
                confirmPassword.error = "Поле должно быть заполнено"
                passwordConfirm = true
            }

            return  email.text.isNullOrEmpty() || password.text.isNullOrEmpty() || passwordConfirm
        }
    }
}