package com.example.hsmassistantandroid.ui.setUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.data.MIHelper
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.mainFragment
import kotlinx.android.synthetic.main.fragment_discovery.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val TAG: String = DiscoveryFragment::class.java.simpleName


class DiscoveryFragment : mainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_discovery, container, false)
        view.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hsmWasFound = makeAutoDiscovery()

        if(hsmWasFound) {
            //if(devices.count == 1) {
            //  go to SVK screen
            //}
            //else if (devices.count > 1) {
            //  show all options stacked on recyclerview (height iqual to 3 rows)
            //}
        }
        else {
            //SHow fields
            GlobalScope.launch(context = Dispatchers.Main) {
                delay(2000)

                loadingProgressBar.hide()
                rightView.visibility = View.VISIBLE
                leftView.visibility = View.VISIBLE
                ouTextView.visibility = View.VISIBLE
                secondTitleTExtView.visibility = View.VISIBLE
                reTrydiscoveryButton.visibility = View.VISIBLE
                deviceAddressEditText.visibility = View.VISIBLE
                connectToButton.visibility = View.VISIBLE
            }
        }

        setUpViews()
    }

    fun setUpViews() {
        reTrydiscoveryButton.setOnClickListener { Log.d(TAG, "CLICK") }

        deviceAddressEditText.editText!!.onChange { deviceAddressEditText.error = null }
        connectToButton.setOnClickListener {
            prepareConnection()
        }
    }

    fun prepareConnection() {
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

    fun makeAutoDiscovery(): Boolean {
        loadingProgressBar.show()
        //TODO: make SLP
        return false
    }

    fun onConnectionEstablished() {
        getActivity()?.runOnUiThread {
            Toast.makeText(context!!, "Conectado", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_discoveryFragment_to_svmkFragment)
        }
    }

    fun showInvalidTokenDialog() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.invalidTokenDialog_title))
            .setMessage(getString(R.string.invalidTokenDialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .show()
    }
}
