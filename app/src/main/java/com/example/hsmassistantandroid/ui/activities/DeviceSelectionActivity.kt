package com.example.hsmassistantandroid.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.example.hsmassistantandroid.R

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
}
