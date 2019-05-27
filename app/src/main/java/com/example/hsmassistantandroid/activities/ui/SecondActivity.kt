package com.example.hsmassistantandroid.activities.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondActivity : AppCompatActivity() {
    private val networkManager = NetworkManager() // 1
    private var tokenString: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        tokenString = intent.getStringExtra("TOKEN")
        tokenTextView.text = tokenString

        listObjsButton.setOnClickListener {
            val context = baseContext
            val callbackList = object : Callback<ResponseBody2> {
                override fun onFailure(call: Call<ResponseBody2>?, t: Throwable?) {
                    Log.e("SecondsActivity", "Problem calling the API", t)
                }

                override fun onResponse(call: Call<ResponseBody2>?, response: Response<ResponseBody2>?) {
                    response?.isSuccessful.let {
                        Log.e("SecondActivity", "Deu certo "+tokenString)
                        val intent = Intent(context, ObjetosListActivity::class.java)
                        val objetosStringList = response?.body()?.obj
                        intent.putExtra("LIST", objetosStringList?.toTypedArray())
                        startActivity(intent)
                    }
                }
            }
            networkManager.runListObjetcs(tokenString!!, callbackList)
        }
        closeButton.setOnClickListener {
            val context = baseContext
            val callbackClose = object : Callback<ResponseBody1> {
                override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
                    Log.e("SecondActivity", "Problem calling the API", t)
                }

                override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
                    response?.isSuccessful.let {

                        AlertDialog.Builder(context).setTitle("Sessão encerrada")
                            .setMessage("Sessão encerrada com sucesso")
                            .setPositiveButton(android.R.string.ok) { dialogInterface, i ->

                            }

                        Log.e("SecondActivity", "Deu certo ")
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            networkManager.runClose(tokenString!!, callbackClose)
        }
    }
}
