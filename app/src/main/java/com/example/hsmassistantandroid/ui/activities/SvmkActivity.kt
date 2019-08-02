package com.example.hsmassistantandroid.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.network.MIHelper
import com.example.hsmassistantandroid.network.NetworkManager
import kotlinx.android.synthetic.main.activity_svmk.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = SvmkActivity::class.java.simpleName

class SvmkActivity : AppCompatActivity() {
    var isFirstBoot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_svmk)
        setUpViews()
        //Eh chamado msm quando vai para tras e volta ?
        val temp_adress = intent.getStringExtra("TEMP_ADDRESS")
        checkFistBoot(temp_adress)
    }

    fun setUpViews() {
        iniciarToButton.setOnClickListener {
            sendAuth()
        }
        svmkEditText.editText!!.onChange { svmkEditText.error = null }
        svmkConfirmationEditText.editText!!.onChange { svmkEditText.error = null }
    }

    fun sendAuth() {
        val key = svmkEditText.editText!!.text.toString()

        val successCallback = {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
            val editor = sharedPreferences.edit()
            editor.putString("INIT_KEY", key)
            editor.apply()

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

        MIHelper.startService(baseContext, successCallback, errorCallback)
    }

    fun onServiceStarted() {
        runOnUiThread {
            Toast.makeText(baseContext, "Servico iniciado", Toast.LENGTH_SHORT).show()
            finishSetUp()
        }
    }

    fun finishSetUp() {
        if(isFirstBoot) {


        }

        val intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        finish()
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

    fun checkFistBoot(temp_adress: String) {
        val caseTrue = {
            isFirstBoot = true
            svmkConfirmationEditText.visibility = View.VISIBLE
        }

        val caseFalse = {
            isFirstBoot = false
            Unit
        }

        MIHelper.isFirstBootProcess(temp_adress, baseContext, caseFalse, caseTrue)
    }

}
