package com.example.hsmassistantandroid.ui.usuário

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.hsmassistantandroid.ui.activities.DeviceSelectionActivity
import com.example.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_user_options.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = UserOptions::class.java.simpleName

class UserOptions : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }

    fun didTapcloseButton() {
        val callbackClose = object : Callback<ResponseBody0> {
            override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }
            override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                if(response?.isSuccessful!!) {
                    removeTokenFromSecureLocation(requireActivity())
                    goToLoginScreen(this@UserOptions)
                }
                else {
                    val message = handleAPIError(this@UserOptions, response.errorBody())
                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        AlertDialog.Builder(requireContext()).setTitle("Encerrar sessão")
            .setMessage("Deseja mesmo encerrar a sessão ?")
            .setNegativeButton(android.R.string.cancel){dialogInterface, i -> }
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, i ->
                networkManager.runClose(tokenString!!, callbackClose)
            }
            .show()
    }

    fun didTapChangePwd() {
        findNavController().navigate(R.id.action_userOptions_to_changePwdFragment)
    }

    fun didTapHsmOptions() {
        findNavController().navigate(R.id.action_userOptions_to_hsmOptions)
    }

    @SuppressLint("ApplySharedPref")
    fun didTapConnectNewHSM() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove("BASE_URL")
        editor.commit()

        val intent = Intent(context, DeviceSelectionActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        connectNewHSM.setOnClickListener { didTapConnectNewHSM() }
        hsmOptions.setOnClickListener { didTapHsmOptions() }
        closeButton.setOnClickListener { didTapcloseButton() }
        changePwdButton.setOnClickListener { didTapChangePwd() }
    }
}
