package com.example.hsmassistantandroid.ui.usuário

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.extensions.alertAboutConnectionError
import com.example.hsmassistantandroid.extensions.goToLoginScreen
import com.example.hsmassistantandroid.extensions.handleAPIError
import com.example.hsmassistantandroid.extensions.removeTokenFromSecureLocation
import com.example.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_user_options.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = HsmOptions::class.java.simpleName

class HsmOptions : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
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
//        hsmOptions.setOnClickListener { didTapHsmOptions() }
//        closeButton.setOnClickListener { didTapcloseButton() }
//        changePwdButton.setOnClickListener { didTapChangePwd() }
    }
}
