package com.example.hsmassistantandroid.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.MIHelper
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody3
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.activities.MainActivity
import com.example.hsmassistantandroid.ui.activities.SecondActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_discovery.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.net.ssl.*
import javax.security.cert.CertificateException
import java.io.PrintWriter
import java.util.*




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
        Toast.makeText(context!!, "Conectado", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.action_discoveryFragment_to_svmkFragment)
    }

    fun showInvalidTokenDialog() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.invalidTokenDialog_title))
            .setMessage(getString(R.string.invalidTokenDialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .show()
    }
}
