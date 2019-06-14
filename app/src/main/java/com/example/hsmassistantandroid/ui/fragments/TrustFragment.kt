package com.example.hsmassistantandroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hsmassistantandroid.R
import kotlinx.android.synthetic.main.trust_fragment.*

class TrustFragment: Fragment() {
    var isTrustees: Boolean? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.trust_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isTrustees == true) {
            myTextView.text = "Em quem eu confio"
        }
        else {
            myTextView.text = "Quem confia em mim"
        }
    }
}