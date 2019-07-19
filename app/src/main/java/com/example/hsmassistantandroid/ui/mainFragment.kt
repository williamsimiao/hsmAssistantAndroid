package com.example.hsmassistantandroid.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

open class mainFragment: Fragment() {
    var alreadyLoaded: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //To invalidate the previusly set Options menu
        ActivityCompat.invalidateOptionsMenu(activity)

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}