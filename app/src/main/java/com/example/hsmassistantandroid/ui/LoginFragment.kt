package com.example.hsmassistantandroid.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody3
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.activities.SecondActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



private val TAG: String = LoginFragment::class.java.simpleName


class LoginFragment : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null
    private var submitedUser: String? = null
    private var userPwd: String? = null
    // Instantiate a new DiscoveryListener

//    fun slp() {
//        // get Locator instance
//        val locator = ServiceLocationManager.getLocator(Locale("en"))
//
//        // find all services of type "test" that have attribute "cool=yes"
//        val sle = locator.findServices(ServiceType("service:pocket"), null, "(cool=yes)")
//
//        // iterate over the results
//        while (sle.hasMoreElements()) {
//            val foundService = sle.nextElement() as ServiceURL
//            Log.d(TAG, foundService)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(tokenString != null) {
            //Então fez logout logo não deve mostrar que o token expirou
            hideLoginFields()
            probeRequest()
        }

        setUpViews()
    }

    fun setUpViews() {
        usrEditText.editText!!.onChange { usrEditText.error = null }
        pwdEditText.editText!!.onChange { pwdEditText.error = null }

        loadingProgressBar.hide()

        autenticarButton.setOnClickListener { didTapAutenticar() }
    }

    fun didTapAutenticar() {
        if(fieldsAreValid(context, arrayOf(usrEditText, pwdEditText)) == false) return

        if(validPwd(context, pwdEditText) ==  false) return

        val callback = object : Callback<ResponseBody1> {
            override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }

            override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
                if(response?.isSuccessful!!) {
                    tokenString = "HSM " + response?.body()?.token
                    Log.e("MainActivity", "Autenticado " + tokenString)
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val editor = sharedPreferences.edit()
                    editor.putString("TOKEN", tokenString)
                    editor.putString("USER", submitedUser)
                    editor.putString("PWD", userPwd)
                    editor.apply()

                    hideSoftKeyboard(requireActivity())
                    val intent = Intent(context, SecondActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                else {
                    handleAPIError(this@LoginFragment, response.errorBody()) {}
                }
            }
        }

        userPwd = pwdEditText.editText!!.text.toString()
        submitedUser = usrEditText.editText!!.text.toString()
        networkManager.runAuth(submitedUser!!, pwdEditText.editText!!.text.toString(), "", callback)
    }

    fun hideLoginFields() {
        loadingProgressBar.show()
        usrEditText.visibility = View.INVISIBLE
        pwdEditText.visibility = View.INVISIBLE
        otpEditText.visibility = View.INVISIBLE
        autenticarButton.visibility = View.INVISIBLE
    }

    fun showLoginFields() {
        loadingProgressBar.hide()

        usrEditText.visibility = View.VISIBLE
        pwdEditText.visibility = View.VISIBLE
        otpEditText.visibility = View.VISIBLE
        autenticarButton.visibility = View.VISIBLE
    }

    fun probeRequest() {

        val callback = object : Callback<ResponseBody3> {
            override fun onFailure(call: Call<ResponseBody3>?, t: Throwable?) {
                alertAboutConnectionError(view)
                showLoginFields()
            }

            override fun onResponse(call: Call<ResponseBody3>?, response: Response<ResponseBody3>?) {
                if(response?.isSuccessful!!) {
                    val intent = Intent(context, SecondActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                else {
                    val callback = { message: String ->
                        if(message == getString(R.string.ERR_INVALID_KEY_message) || message == getString(R.string.ERR_ACCESS_DENIED_message)) {
                            AlertDialog.Builder(requireContext()).setTitle(getString(R.string.invalidTokenDialog_title))
                                .setMessage(getString(R.string.invalidTokenDialog_message))
                                .setPositiveButton(android.R.string.ok) { _, _ -> }
                                .show()
                        }

                    }
                    handleAPIError(this@LoginFragment, response.errorBody(), callback)

                }
                showLoginFields()
            }
        }
        networkManager.runProbe(tokenString!!, callback)
    }
}
