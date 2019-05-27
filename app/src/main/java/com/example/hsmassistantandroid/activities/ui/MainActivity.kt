package com.example.hsmassistantandroid.activities.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val networkManager = NetworkManager() // 1
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isNetworkConnected() == false ) {
            Log.d("MainActivity", "Sem NET nao da neh")
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }

        autenticarButton.setOnClickListener {
            val context = baseContext
            val callback = object : Callback<ResponseBody1> {
                override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
                    Log.e("MainActivity", "Problem calling the API", t)
                }

                override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
                    response?.isSuccessful.let {
                        tokenString = "HSM " + response?.body()?.token
                        Log.e("MainActivity", "Deu certo "+tokenString)
                        val intent = Intent(context, SecondActivity::class.java)
                        intent.putExtra("TOKEN", tokenString)
                        startActivity(intent)
                    }
                }
            }
            networkManager.runAuth(usrEditText.text.toString(), pwdEditText.text.toString(), "", callback)
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected //3
    }
}
