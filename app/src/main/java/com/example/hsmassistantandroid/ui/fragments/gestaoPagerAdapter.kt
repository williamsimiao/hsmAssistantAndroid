package com.example.hsmassistantandroid.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class gestaoPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                val trusteesFragment = TrustFragment()
                trusteesFragment.isTrustees = true
                return trusteesFragment
            }

            else -> {
                val trusterFragment = TrustFragment()
                trusterFragment.isTrustees = false
                return trusterFragment
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Trustees"
            else -> {
                return "Trusters"
            }
        }
    }
}