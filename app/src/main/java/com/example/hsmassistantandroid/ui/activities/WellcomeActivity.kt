package com.example.hsmassistantandroid.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.example.hsmassistantandroid.R
import kotlinx.android.synthetic.main.activity_wellcome.*

private val TAG: String = WellcomeActivity::class.java.simpleName

class WellcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val base_url = sharedPreferences.getString("BASE_URL", null)
        val isFirstTime = sharedPreferences.getBoolean("FIRST_TIME", true)

        if(base_url != null || !isFirstTime) {
            val intent = Intent(baseContext, SetupActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_wellcome)
        setUpViews()
    }


    fun setUpViews() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean("FIRST_TIME", false)
        editor.apply()

        nextButton.setOnClickListener {
            val intent = Intent(baseContext, SetupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

