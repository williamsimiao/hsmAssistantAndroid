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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hsmassistantandroid.data.ResponseBody3
import com.example.hsmassistantandroid.extensions.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.contentView
import org.jetbrains.anko.toast

private val TAG: String = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    private var submitedUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingProgressBar.hide()
        contentView!!.setOnClickListener {
            hideSoftKeyboard(this)
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        tokenString = sharedPreferences.getString("TOKEN", null)
        autenticarButton.setOnClickListener { didTapAutenticar() }

        usrEditText.editText!!.onChange { usrEditText.error = null }
        pwdEditText.editText!!.onChange { pwdEditText.error = null }

        if(tokenString != null) {
            //Então fez logout logo não deve mostrar que o token expirou
            hideLoginFields()
            probeRequest()
        }

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
                alertAboutConnectionError(contentView)
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

                    hideSoftKeyboard(this@MainActivity)
                    val intent = Intent(applicationContext, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    val message = handleAPIError(this@MainActivity, response.errorBody())
                }
            }
        }

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
                alertAboutConnectionError(contentView)
                showLoginFields()
            }

            override fun onResponse(call: Call<ResponseBody3>?, response: Response<ResponseBody3>?) {
                if(response?.isSuccessful!!) {
                    val intent = Intent(baseContext, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    val message = handleAPIError(this@MainActivity, response.errorBody())
                    if(message == getString(R.string.ERR_INVALID_KEY_message) || message == getString(R.string.ERR_ACCESS_DENIED_message)) {
                        showInvalidTokenDialog()
                    }
                }
                showLoginFields()
            }
        }
        networkManager.runProbe(tokenString!!, callback)
    }
}
