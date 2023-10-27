package com.example.MP3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.MP3.databinding.ActivityTransferBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding
    private lateinit var viewModel: TransferViewModel
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = TransferViewModel()

        val receivedBarcodeResult = intent.getStringExtra("barcode_result")

        viewModel.getRecipientInfo(receivedBarcodeResult)


        binding.bttnTransfer.setOnClickListener {
            val enteredAmount = binding.etAmount.text.toString()
            val amount = enteredAmount.toDouble()

            viewModel.transferFund(auth.currentUser?.uid, receivedBarcodeResult, amount)
        }

        binding.btnCancel.setOnClickListener {
            MainActivity.launch(this@TransferActivity)
            finish()
        }

        viewModel.getStates().observe(this@TransferActivity) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: TransferState) {
        when (state) {
            is TransferState.ScanSuccess -> {
                binding.tvRecepient.text = "${state.user?.name}"
            }
            is TransferState.TransactionSuccess -> {
            val enteredAmount = binding.etAmount.text.toString()
            val amount = enteredAmount.toDouble()
                viewModel.deductTransferedAmount(auth.currentUser?.uid, amount )

            }
            is TransferState.DeductionSuccess ->{
                MainActivity.launch(this@TransferActivity)
                finish()
            }
            is TransferState.insuficientAmountError ->{
                Toast.makeText(this@TransferActivity, "Balance Insufficient", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    companion object {
        fun launch(activity: Activity) = activity.startActivity(Intent(activity, TransferActivity::class.java))
    }
}