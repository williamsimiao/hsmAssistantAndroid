package com.example.hsmassistantandroid.ui.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.preference.PreferenceManager
import android.text.TextWatcher
import android.view.View
import com.example.hsmassistantandroid.data.ResponseBody3
import com.example.hsmassistantandroid.extensions.fieldsAreValid
import com.example.hsmassistantandroid.extensions.handleNetworkResponse
import com.example.hsmassistantandroid.extensions.onChange
import com.google.android.material.textfield.TextInputLayout

private val TAG: String = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    private var submitedUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingProgressBar.hide()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        tokenString = sharedPreferences.getString("TOKEN", null)
        autenticarButton.setOnClickListener { didTapAutenticar() }

        usrEditText.editText!!.onChange { usrEditText.error = null }
        pwdEditText.editText!!.onChange { pwdEditText.error = null }

        val shouldShowDialog = intent.getBooleanExtra("shouldShowInvalidTokenDialog", false)
        if(shouldShowDialog) {
            showInvalidTokenDialog()
        }
        else {
            showLoading()
            probeRequest()
        }
    }

    fun showLoading() {
        loadingProgressBar.show()
        usrEditText.visibility = View.INVISIBLE
        pwdEditText.visibility = View.INVISIBLE
        otpEditText.visibility = View.INVISIBLE
        autenticarButton.visibility = View.INVISIBLE
    }

    fun showInvalidTokenDialog() {
        AlertDialog.Builder(this).setTitle(getString(R.string.invalidTokenDialog_title))
            .setMessage(getString(R.string.invalidTokenDialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .show()
    }

    fun didTapAutenticar() {
        if(fieldsAreValid(baseContext, arrayOf(usrEditText, pwdEditText)) == false) {
            return
        }

        val callback = object : Callback<ResponseBody1> {
            override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
                Log.e("MainActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
                if(response?.isSuccessful!!) {
                    tokenString = "HSM " + response?.body()?.token
                    Log.e("MainActivity", "Autenticado "+tokenString)
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    val editor = sharedPreferences.edit()
                    editor.putString("TOKEN", tokenString)
                    editor.putString("USER", submitedUser)
                    editor.apply()

                    val intent = Intent(applicationContext, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Log.d(TAG, response.errorBody().toString())
                }
            }
        }

        submitedUser = usrEditText.editText!!.text.toString()
        networkManager.runAuth(submitedUser!!, pwdEditText.editText!!.text.toString(), "", callback)
    }

    fun hideLoading() {
        loadingProgressBar.hide()

        usrEditText.visibility = View.VISIBLE
        pwdEditText.visibility = View.VISIBLE
        otpEditText.visibility = View.VISIBLE
        autenticarButton.visibility = View.VISIBLE
    }

    fun probeRequest() {
        if (isNetworkConnected() == false ) {
            AlertDialog.Builder(this).setTitle(getString(R.string.noInternetDialog_title))
                .setMessage(getString(R.string.noInternetDialog_message))
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
            return
        }

        val callback = object : Callback<ResponseBody3> {
            override fun onFailure(call: Call<ResponseBody3>?, t: Throwable?) {
                Log.e("Probe", "Problem calling the API", t)
                hideLoading()
            }

            override fun onResponse(call: Call<ResponseBody3>?, response: Response<ResponseBody3>?) {
                if(response?.isSuccessful!!) {
                    val intent = Intent(baseContext, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    showInvalidTokenDialog()
                    Log.d(TAG, response.errorBody().toString())
                }
                hideLoading()
            }

        }

        if(tokenString == null) {
            hideLoading()
            return
        }
        networkManager.runProbe(tokenString!!, callback)
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected //3
    }
}
