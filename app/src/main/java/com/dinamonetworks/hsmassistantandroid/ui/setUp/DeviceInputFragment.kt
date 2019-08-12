package com.dinamonetworks.hsmassistantandroid.ui.setUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dinamonetworks.hsmassistantandroid.R
import com.dinamonetworks.hsmassistantandroid.extensions.*
import com.dinamonetworks.hsmassistantandroid.ui.activities.DeviceSelectionActivity
import com.dinamonetworks.hsmassistantandroid.ui.mainFragment
import kotlinx.android.synthetic.main.fragment_device_input.*


private val TAG: String = DeviceInputFragment::class.java.simpleName


class DeviceInputFragment : mainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_device_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        secondTitleTExtView.visibility = View.VISIBLE
        rightView.visibility = View.VISIBLE
        leftView.visibility = View.VISIBLE
        ouTextView.visibility = View.VISIBLE
        deviceAddressEditText.visibility = View.VISIBLE
        connectToButton.visibility = View.VISIBLE

        setUpViews()
    }

    fun setUpViews() {

        deviceAddressEditText.editText!!.onChange { deviceAddressEditText.error = null }
        connectToButton.setOnClickListener {
            val address = deviceAddressEditText.editText!!.text.toString()
            (requireActivity() as DeviceSelectionActivity).prepareConnection(address)
        }
    }
}
