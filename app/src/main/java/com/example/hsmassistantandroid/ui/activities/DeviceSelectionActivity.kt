package com.example.hsmassistantandroid.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.MIHelper

private val TAG: String = DeviceSelectionActivity::class.java.simpleName

class DeviceSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val base_url = sharedPreferences.getString("BASE_URL", null)
        if(base_url != null) {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_device_selection)
    }

    fun prepareConnection(address: String) {
        //TODO: validate address

        val successCallback = {
            onConnectionEstablished(address)
        }

        val errorCallback = { errorMessage: String ->
            runOnUiThread {
                Toast.makeText(baseContext, "Erro ao conectar-se", Toast.LENGTH_SHORT).show()
                Log.d(TAG, errorMessage)
            }
        }

        MIHelper.connectToAddress(address, baseContext, successCallback, errorCallback)
    }

    fun onConnectionEstablished(address: String) {
        runOnUiThread {
            Toast.makeText(baseContext, "Conectado", Toast.LENGTH_SHORT).show()
            goToSVMKactivity(address)
        }
    }

    fun goToSVMKactivity(address: String) {
        val intent = Intent(baseContext, SvmkActivity::class.java)
        intent.putExtra("TEMP_ADDRESS", address)
        startActivity(intent)
    }
}
