package com.example.MP3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.MP3.databinding.ActivityQrBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding
    private lateinit var viewModel: AuthenticationViewModel
    private val auth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(auth.currentUser?.uid, BarcodeFormat.QR_CODE, 400, 400)

        val ivQrCode = findViewById<ImageView>(R.id.iv_qr)

        ivQrCode.setImageBitmap(bitmap)

        binding.btnDone.setOnClickListener {
            MainActivity.launch(this@QrActivity)
            finish()
        }
    }

    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, QrActivity::class.java))
    }

}