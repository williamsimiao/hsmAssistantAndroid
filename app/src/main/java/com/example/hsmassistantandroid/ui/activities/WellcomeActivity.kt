package com.example.hsmassistantandroid.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import com.example.hsmassistantandroid.R
import kotlinx.android.synthetic.main.activity_wellcome.*

private val TAG: String = WellcomeActivity::class.java.simpleName

class WellcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val base_url = sharedPreferences.getString("BASE_URL", null)
        if(base_url != null) {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_wellcome)
        setUpViews()
    }

    fun setUpViews() {

        nextButton.setOnClickListener {
            val intent = Intent(baseContext, SetupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



}

