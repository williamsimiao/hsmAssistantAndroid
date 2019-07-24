package com.example.hsmassistantandroid.ui.setUp

import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.MIHelper
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.activities.SvmkActivity
import com.example.hsmassistantandroid.ui.mainFragment
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
            prepareConnection()
        }
    }

    fun prepareConnection() {
        //TODO: validate address
        val address = deviceAddressEditText.editText!!.text.toString()

        val successCallback = {
            onConnectionEstablished()
        }

        val errorCallback = { errorMessage: String ->
            getActivity()?.runOnUiThread {
                Toast.makeText(context!!, "Erro ao conectar-se", Toast.LENGTH_SHORT).show()
                Log.d(TAG, errorMessage)
            }
        }

        MIHelper.connectToAddress(address, requireContext(), successCallback, errorCallback)
    }

    fun onConnectionEstablished() {
        getActivity()?.runOnUiThread {
            Toast.makeText(context!!, "Conectado", Toast.LENGTH_SHORT).show()
            goToSVMKactivity()
        }
    }

    fun goToSVMKactivity() {
        val intent = Intent(context, SvmkActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
