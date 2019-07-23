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
import com.example.hsmassistantandroid.ui.activities.MainActivity
import com.example.hsmassistantandroid.ui.mainFragment
import kotlinx.android.synthetic.main.fragment_discovery.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val TAG: String = DiscoveryFragment::class.java.simpleName


class DiscoveryFragment : mainFragment() {
    private var base_url: String? = null
    private lateinit var mServiceName: String
    private lateinit var nsdManager: NsdManager

    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
            // Save the service name. Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used.
            mServiceName = NsdServiceInfo.serviceName
        }

        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Registration failed! Put debugging code here to determine why.
        }

        override fun onServiceUnregistered(arg0: NsdServiceInfo) {
            // Service has been unregistered. This only happens when you call
            // NsdManager.unregisterService() and pass in this listener.
        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Unregistration failed. Put debugging code here to determine why.
        }
    }

//     Instantiate a new DiscoveryListener
    private val discoveryListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Service discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            Log.d(TAG, "Service discovery success $service")
            Log.d(TAG, service.serviceName)
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost: $service")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }

    fun registerService(port: Int) {
        // Create the NsdServiceInfo object, and populate it.
        val serviceInfo = NsdServiceInfo().apply {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceName = "NsdChat"
            serviceType = "_nsdchat._tcp"
            setPort(port)
        }

        nsdManager = (getSystemService(requireContext(), NsdManager::class.java) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        base_url = sharedPreferences.getString("BASE_URL", null)
        if(base_url != null) {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        registerService(3344)

        nsdManager.discoverServices("_pocket._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener)


//        nsdManager = getSystemService(requireContext(), NsdManager::class.java) as NsdManager
//
//        nsdManager.apply {
//            nsdManager.discoverServices("TEST", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hsmWasFound = makeAutoDiscovery()

//        if(hsmWasFound) {
//            //if(devices.count == 1) {
//            //  go to SVK screen
//            //}
//            //else if (devices.count > 1) {
//            //  show all options stacked on recyclerview (height iqual to 3 rows)
//            //}
//        }
//        else {
//            //SHow fields
//            GlobalScope.launch(context = Dispatchers.Main) {
//                delay(2000)
//
//
//            }
//        }

        loadingProgressBar.hide()
        rightView.visibility = View.VISIBLE
        leftView.visibility = View.VISIBLE
        ouTextView.visibility = View.VISIBLE
        secondTitleTExtView.visibility = View.VISIBLE
        reTrydiscoveryButton.visibility = View.VISIBLE
        deviceAddressEditText.visibility = View.VISIBLE
        connectToButton.visibility = View.VISIBLE

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
