package com.example.MP3

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.MP3.databinding.ActivityQrScannerBinding
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ViewfinderView
import java.util.Random

class QrScannerActivity : AppCompatActivity(){

    private lateinit var binding: ActivityQrScannerBinding
    private lateinit var capture : CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var viewfinderView: ViewfinderView

//    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
//        if (result.contents == null) {
//            Toast.makeText(this@QrScannerActivity, "Cancelled", Toast.LENGTH_LONG).show()
//            MainActivity.launch(this@QrScannerActivity)
//            finish()
//        } else {
//            Toast.makeText(
//                this@QrScannerActivity,
//                "Scanned: ${result.contents}",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScannerBinding.inflate(layoutInflater)

        setContentView(binding.root)


        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)


        viewfinderView = findViewById(R.id.zxing_viewfinder_view)


        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent,savedInstanceState)
        capture.setShowMissingCameraPermissionDialog(false)

        barcodeScannerView.decodeSingle { result ->
            val barcodeResult = result.text
            val intent = Intent(this@QrScannerActivity, TransferActivity::class.java)
            intent.putExtra("barcode_result", barcodeResult)
            startActivity(intent)
        }

        changeMaskColor(null)
        changeLaserVisibility(true)

        binding.btnCancel.setOnClickListener {
            Toast.makeText(this@QrScannerActivity, "Cancelled", Toast.LENGTH_LONG).show()
            MainActivity.launch(this@QrScannerActivity)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }


    fun changeMaskColor(view: View?) {
        val rnd = Random()
        val color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        viewfinderView.setMaskColor(color)
    }

    fun changeLaserVisibility(visible: Boolean) {
        viewfinderView.setLaserVisibility(visible)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, QrScannerActivity::class.java))
    }

}