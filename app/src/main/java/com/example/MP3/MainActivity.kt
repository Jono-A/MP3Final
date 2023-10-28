package com.example.MP3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.MP3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = AuthenticationViewModel()
        viewModel.getStates().observe(this@MainActivity){
        handleState(it)

        }

        viewModel.getUserProfile()

        with(binding){
            btnLogout.setOnClickListener {
                viewModel.logOut()
            }
            btnQrScanner.setOnClickListener{
                QrScannerActivity.launch(this@MainActivity)
                finish()
            }
            btnQr.setOnClickListener{
                QrActivity.launch(this@MainActivity)
                finish()
            }
            bttnAddValue.setOnClickListener{
                AddValueActivity.launch(this@MainActivity)
                finish()
            }
        }

    }

    private fun handleState(state : AuthenticationStates) {
        when(state) {
            is AuthenticationStates.Default -> {
                binding.tvUsername.text = "${state.user?.name}"
                binding.tvCurrentBalance.text = "${state.user?.balance}"
            }
            AuthenticationStates.Error -> {}
            AuthenticationStates.LogOut -> {
                LoginActivity.launch(this@MainActivity)
                finish()
            }
            AuthenticationStates.UserDeleted -> {
                LoginActivity.launch(this@MainActivity)
                finish()
            }
            AuthenticationStates.VerificationEmailSent -> {}
            else -> {}
        }
    }
    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, MainActivity::class.java))
    }
}