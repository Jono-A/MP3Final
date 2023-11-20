package com.example.MP3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.MP3.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = AuthenticationViewModel()
        viewModel.getStates().observe(this@SignupActivity){
            handleState(it)
        }
        binding.bttnSigup.setOnClickListener {
            viewModel.signUp(binding.etEmail.text.toString(), binding.etPassword.text.toString(),)
            /*val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)*/
        }

    }



    private fun handleState(state : AuthenticationStates) {
        when(state) {
            is AuthenticationStates.SignedUp -> {
                Toast.makeText( this@SignupActivity, "Signed In!", Toast.LENGTH_SHORT).show()
                viewModel.createUserRecord(
                    binding.etName.text.toString(),
                    binding.etEmail.text.toString(),
                    0.00,
                )
            }
            is AuthenticationStates.ProfileUpdated -> {
                MainActivity.launch(this@SignupActivity)
                finish()
            }
            else -> {}
        }
    }
    companion object{
        fun launch(activity: Activity) = activity.startActivity(Intent(activity, SignupActivity::class.java))
    }
}