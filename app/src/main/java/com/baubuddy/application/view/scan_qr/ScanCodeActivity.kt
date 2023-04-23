
package com.baubuddy.application.view.scan_qr

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.baubuddy.application.view.main.MainActivity
import com.baubuddy.application.view.main.MainActivity.Companion.RESULT
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanCodeActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)

        setPermission()
    }

    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }
    override fun onStop() {
        super.onStop()

        scannerView?.stopCamera()
        onBackPressed()
    }
    override fun handleResult(p0: Result?) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(RESULT, p0.toString())
        startActivity(intent)
    }
    private fun setPermission(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1 -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need Camera permission!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
