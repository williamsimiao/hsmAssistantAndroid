package com.example.hsmassistantandroid.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.ui.adapters.myPagerAdapter
import com.example.hsmassistantandroid.ui.adapters.setupPagerAdapter
import com.example.hsmassistantandroid.ui.fragments.DiscoveryFragment
import com.example.hsmassistantandroid.ui.fragments.WellcomeFragment

private val TAG: String = SetupActivity::class.java.simpleName

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        val viewPager = findViewById<View>(R.id.setupViewpager) as ViewPager

        val adapter = myPagerAdapter(supportFragmentManager)

        val wellcomeFragment = WellcomeFragment()
        val setupFragment = DiscoveryFragment()
        adapter.addFragment(wellcomeFragment, "HSM Pocket")
        adapter.addFragment(setupFragment, "Configuração")

        viewPager.adapter = adapter
    }



}

