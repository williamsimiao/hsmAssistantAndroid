package com.example.hsmassistantandroid.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.ui.adapters.setupPagerAdapter

private val TAG: String = SetupActivity::class.java.simpleName

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        val viewPager = findViewById<View>(R.id.setupViewpager) as ViewPager
        viewPager.adapter = setupPagerAdapter(this)
    }
}

