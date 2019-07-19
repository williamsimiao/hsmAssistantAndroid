package com.example.hsmassistantandroid.ui.setUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.mainFragment
import kotlinx.android.synthetic.main.fragment_wellcome.*


private val TAG: String = DiscoveryFragment::class.java.simpleName


class WellcomeFragment : mainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_wellcome, container, false)
        view.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {

        nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_wellcomeFragment_to_discoveryFragment)
            Log.d(TAG, "CLICK")
        }
//
//        deviceAddressEditText.editText!!.onChange { deviceAddressEditText.error = null }

    }
}
