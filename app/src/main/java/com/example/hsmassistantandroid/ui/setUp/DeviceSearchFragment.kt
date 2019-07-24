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
import androidx.core.content.ContextCompat.getSystemService
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.MIHelper
import com.example.hsmassistantandroid.ui.activities.MainActivity
import com.example.hsmassistantandroid.ui.activities.SvmkActivity
import com.example.hsmassistantandroid.ui.mainFragment
import kotlinx.android.synthetic.main.fragment_devices_search.*


private val TAG: String = DeviceSearchFragment::class.java.simpleName

class DeviceSearchFragment : mainFragment() {
    private var base_url: String? = null
    private lateinit var mServiceName: String
    private lateinit var nsdManager: NsdManager
    var devices = arrayOf("pocket_example_1", "pocket_example_2", "pocket_example_3", "pocket_example_4", "pocket_example_5")

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

        registerService(3344)

        doAutoDiscovery()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_devices_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doAutoDiscovery()

        loadingProgressBar.hide()
        reTrydiscoveryButton.visibility = View.VISIBLE

        setUpViews()
    }

    fun setUpViews() {
        reTrydiscoveryButton.setOnClickListener { Log.d(TAG, "CLICK") }
    }

    fun doAutoDiscovery() {
//        loadingProgressBar.show()
//        nsdManager.discoverServices("_pocket._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener)

    }

    fun prepareConnection(address: String) {

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
