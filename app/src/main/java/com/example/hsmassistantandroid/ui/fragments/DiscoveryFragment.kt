package com.example.hsmassistantandroid.ui.fragments

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
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody3
import com.example.hsmassistantandroid.extensions.*
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
    private val MI_PORT = 3344

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
            Log.d(TAG, "Indo para SVK screen")

            val address = deviceAddressEditText.editText!!.text.toString()
            //TODO validar address

            connectToAddress(address)
        }
    }

    fun makeAutoDiscovery(): Boolean {
        loadingProgressBar.show()
        //TODO: make SLP
        return false
    }

    fun connectToAddress(address: String): Boolean {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("SSL")
        try {
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val sslsocket: SSLSocket = sslSocketFactory.createSocket(address, MI_PORT) as SSLSocket
            val input = Scanner(sslsocket.inputStream)
            val output = PrintWriter(sslsocket.outputStream, true)

            output.println("MI_HELLO")
            val response = input.nextLine()
            Log.d(TAG, response)

            if(response == "MI_ACK 00000000") {
                Toast.makeText(context, "Conectado", Toast.LENGTH_SHORT)
                Log.d(TAG, "conectado")
                return true
            }
            else {
                Toast.makeText(context, "Falha ao conectar", Toast.LENGTH_SHORT)
                Log.d(TAG, "falha ao connectar")
                return false
            }

//                output.println("MI_MINI_AUTH 12345678")
//                Log.d(TAG, input.nextLine())
//                output.println("MI_SVC_STOP")
//                Log.d(TAG, input.nextLine())

        } catch (e: Exception) {
            Log.d(TAG, "outra falha")
        }

        return false
    }

    fun sendMessage(message: String) {

    }



    fun showInvalidTokenDialog() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.invalidTokenDialog_title))
            .setMessage(getString(R.string.invalidTokenDialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .show()
    }
}
