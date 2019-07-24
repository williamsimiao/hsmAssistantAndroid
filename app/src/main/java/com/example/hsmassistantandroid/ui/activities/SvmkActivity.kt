package com.example.hsmassistantandroid.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.extensions.onChange
import com.example.hsmassistantandroid.extensions.validPwd
import com.example.hsmassistantandroid.network.MIHelper
import kotlinx.android.synthetic.main.activity_svmk.*

private val TAG: String = SvmkActivity::class.java.simpleName

class SvmkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_svmk)
        setUpViews()
    }

    fun setUpViews() {
        iniciarToButton.setOnClickListener {
            sendAuth()
        }
        svmkEditText.editText!!.onChange { svmkEditText.error = null }
    }

    fun sendAuth() {
        val key = svmkEditText.editText!!.text.toString()

        val successCallback = {
            onAuthenticationCompleted()
        }

        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }

        if(validPwd(baseContext, svmkEditText)) {
            MIHelper.autenticateWithKey(key, successCallback, errorCallback)
        }
    }

    fun onAuthenticationCompleted() {
        runOnUiThread {
            Toast.makeText(baseContext, "Autenticado", Toast.LENGTH_SHORT).show()
        }

        val successCallback = {
            onServiceStarted()
        }

        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }

        MIHelper.startService(successCallback, errorCallback)
    }

    fun onServiceStarted() {
        runOnUiThread {
            Toast.makeText(baseContext, "Servico iniciado", Toast.LENGTH_SHORT).show()
        }

        val successCallback = {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }

        MIHelper.disconnect(successCallback, errorCallback)
    }
}
