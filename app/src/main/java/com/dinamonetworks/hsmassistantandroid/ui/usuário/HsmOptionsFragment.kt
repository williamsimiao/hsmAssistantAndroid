package com.dinamonetworks.hsmassistantandroid.ui.usuário

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*

import com.dinamonetworks.hsmassistantandroid.R
import com.dinamonetworks.hsmassistantandroid.network.NetworkManager
import com.dinamonetworks.hsmassistantandroid.network.MIHelper
import com.dinamonetworks.hsmassistantandroid.ui.activities.MainActivity
import com.dinamonetworks.hsmassistantandroid.ui.mainFragment
import kotlinx.android.synthetic.main.fragment_hsm_options.*

private val TAG: String = HsmOptions::class.java.simpleName

enum class ServiceStatus {
    Stoped,
    Started
}

class HsmOptions : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null
    private var myServiceStatus = ServiceStatus.Stoped

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
        checkServiceStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hsm_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        serviceButton.setOnClickListener { didTapService() }
        restartButton.setOnClickListener { didTapRestart() }
        powerButton.setOnClickListener { didTapPower() }
    }

    override fun onStop() {
        super.onStop()

        val successCallback = {
            Log.d(TAG, "Disconnected")
            Unit
        }

        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }

        MIHelper.disconnect(successCallback, errorCallback)
    }

    private fun didTapPower() {
        //Power
    }

    private fun didTapRestart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setButtonWithServiceStatus() {

        if(myServiceStatus == ServiceStatus.Stoped) {
            serviceButton.text = "Carregar"
        }
        else {
            serviceButton.text = "Parar"
        }
    }

    private fun checkServiceStatus() {
        //Check
        val caseTrue = {
            myServiceStatus = ServiceStatus.Started
        }

        val caseFalse = {
            myServiceStatus = ServiceStatus.Stoped
            setButtonWithServiceStatus()
        }

        MIHelper.isServiceStarted(requireContext() ,caseTrue, caseFalse)
    }

    private fun didTapService() {

        //Start
        //Stop
        val successCallback = {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }
    }
}
