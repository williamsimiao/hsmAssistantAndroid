package com.dinamonetworks.hsmassistantandroid.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dinamonetworks.hsmassistantandroid.R

private val TAG: String = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
