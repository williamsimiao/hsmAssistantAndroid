package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody3
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.activities.SecondActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_change_pwd.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.contentView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.widget.Toast
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.content.BroadcastReceiver
import org.jetbrains.anko.toast


private val TAG: String = LoginFragment::class.java.simpleName


class LoginFragment : mainFragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    private var submitedUser: String? = null
    val wm:WifiManager= context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager

    companion object {

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    fun connectToWPAWiFi(ssid:String, pass:String?){
        if(isConnectedTo(ssid)){ //see if we are already connected to the given ssid
            context?.toast("Connected to"+ssid)
            return
        }
        var wifiConfig=getWiFiConfig(ssid)
        if(wifiConfig==null){//if the given ssid is not present in the WiFiConfig, create a config for it
            createWPAProfile(ssid, pass)
            wifiConfig=getWiFiConfig(ssid)
        }
        wm.disconnect()
        wm.enableNetwork(wifiConfig!!.networkId,true)
        wm.reconnect()
        Log.d(TAG,"intiated connection to SSID"+ssid);
    }
    fun isConnectedTo(ssid: String):Boolean{
        val wm:WifiManager= context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if(wm.connectionInfo.ssid == ssid){
            return true
        }
        return false
    }
    fun getWiFiConfig(ssid: String): WifiConfiguration? {
        val wm:WifiManager= context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList=wm.configuredNetworks
        for (item in wifiList){
            if(item.SSID != null && item.SSID.equals(ssid)){
                return item
            }
        }
        return null
    }
    fun createWPAProfile(ssid: String,pass: String?){
        Log.d(TAG,"Saving SSID :"+ssid)
        val conf = WifiConfiguration()
        conf.SSID = ssid
        if(pass != null) {
            conf.preSharedKey = pass
        }
        val wm:WifiManager= context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wm.addNetwork(conf)
        Log.d(TAG,"saved SSID to WiFiManger")
    }

    class WiFiChngBrdRcr : BroadcastReceiver(){ // shows a toast message to the user when device is connected to a AP
        private val TAG = "WiFiChngBrdRcr"
        override fun onReceive(context: Context, intent: Intent) {
            val networkInfo=intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if(networkInfo.state == NetworkInfo.State.CONNECTED){
                val bssid=intent.getStringExtra(WifiManager.EXTRA_BSSID)
                Log.d(TAG, "Connected to BSSID:"+bssid)
                val ssid=intent.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO).ssid
                val log="Connected to SSID:"+ssid
                Log.d(TAG,"Connected to SSID:"+ssid)
                Toast.makeText(context, log, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }
        return view
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

    fun showInvalidTokenDialog() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.invalidTokenDialog_title))
            .setMessage(getString(R.string.invalidTokenDialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .show()
    }

    fun didTapAutenticar() {
        if (!wm.isWifiEnabled()) {
            context?.toast("WiFi is disabled ... We need to enable it")
            wm.setWifiEnabled(true);
        }

        val ssid = "pocket"
        context?.toast("Changing wifi to "+ssid)
        connectToWPAWiFi(ssid, null)
//        if(fieldsAreValid(context, arrayOf(usrEditText, pwdEditText)) == false) return
//
//        if(validPwd(context, pwdEditText) ==  false) return
//
//        val callback = object : Callback<ResponseBody1> {
//            override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
//                alertAboutConnectionError(view)
//            }
//
//            override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
//                if(response?.isSuccessful!!) {
//                    tokenString = "HSM " + response?.body()?.token
//                    Log.e("MainActivity", "Autenticado "+tokenString)
//                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//                    val editor = sharedPreferences.edit()
//                    editor.putString("TOKEN", tokenString)
//                    editor.putString("USER", submitedUser)
//                    editor.apply()
//
//                    hideSoftKeyboard(requireActivity())
//                    val intent = Intent(context, SecondActivity::class.java)
//                    startActivity(intent)
//                    requireActivity().finish()
//                }
//                else {
//                    val message = handleAPIError(this@LoginFragment, response.errorBody())
//                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()
//                }
//            }
//        }
//
//        submitedUser = usrEditText.editText!!.text.toString()
//        networkManager.runAuth(submitedUser!!, pwdEditText.editText!!.text.toString(), "", callback)
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
                    val message = handleAPIError(this@LoginFragment, response.errorBody())
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
