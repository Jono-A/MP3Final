package com.example.MP3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.MP3.databinding.ActivityAddValueBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AddValueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddValueBinding
    private lateinit var  viewModel: TransferViewModel
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddValueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = TransferViewModel()

        binding.bttnAddValue.setOnClickListener {
            val enteredAmount = binding.etAmount.text.toString()
            val amount = enteredAmount.toDouble()
            viewModel.addValue(auth.currentUser?.uid, amount)
        }
        viewModel.getStates().observe(this@AddValueActivity){
            state ->
            handleState(state)
        }

    }
    private fun handleState(state: TransferState){
        when(state){
            is TransferState.ValueAdded -> {
                MainActivity.launch(this@AddValueActivity)
                finish()
            }
            else -> {
            }
        }
    }
    companion object {
        fun launch(activity: Activity) = activity.startActivity(Intent(activity, AddValueActivity::class.java))
    }
}