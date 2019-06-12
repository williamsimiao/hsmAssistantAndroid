package com.example.hsmassistantandroid.activities.ui

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
import android.view.View
import com.example.hsmassistantandroid.data.ResponseBody3
import com.example.hsmassistantandroid.extensions.handleNetworkResponse

class MainActivity : AppCompatActivity() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        tokenString = sharedPreferences.getString("TOKEN", null)
        autenticarButton.setOnClickListener { didTapAutenticar() }

        loadingProgressBar.show()
        usrEditText.visibility = View.INVISIBLE
        pwdEditText.visibility = View.INVISIBLE
        otpEditText.visibility = View.INVISIBLE
        autenticarButton.visibility = View.INVISIBLE

        probeRequest()
    }

    fun didTapAutenticar() {
        val context = baseContext
        val callback = object : Callback<ResponseBody1> {
            override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
                Log.e("MainActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
                response?.isSuccessful.let {
                    tokenString = "HSM " + response?.body()?.token
                    Log.e("MainActivity", "Deu certo "+tokenString)
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    val editor = sharedPreferences.edit()
                    editor.putString("TOKEN", tokenString)
                    editor.apply()

                    val intent = Intent(context, SecondActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        networkManager.runAuth(usrEditText.editText!!.text.toString(), pwdEditText.editText!!.text.toString(), "", callback)
    }

    fun showLoginFields() {
        loadingProgressBar.hide()

        usrEditText.visibility = View.VISIBLE
        pwdEditText.visibility = View.VISIBLE
        otpEditText.visibility = View.VISIBLE
        autenticarButton.visibility = View.VISIBLE
    }

    fun probeRequest() {
        if (isNetworkConnected() == false ) {
            Log.d("MainActivity", "Sem NET nao da neh")
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
            return
        }

        val callback = object : Callback<ResponseBody3> {
            override fun onFailure(call: Call<ResponseBody3>?, t: Throwable?) {
                Log.e("Probe", "Problem calling the API", t)
                showLoginFields()
            }

            override fun onResponse(call: Call<ResponseBody3>?, response: Response<ResponseBody3>?) {
                val codeMeaning = handleNetworkResponse(response?.code())
                if(codeMeaning == "sucess") {
                    val intent = Intent(baseContext, SecondActivity::class.java)
                    startActivity(intent)
                }
                showLoginFields()
                Log.d("probe", codeMeaning)
            }
        }

        if(tokenString == null) {
            showLoginFields()
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
