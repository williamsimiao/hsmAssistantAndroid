package com.example.hsmassistantandroid.network

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.data.*
import com.example.hsmassistantandroid.extensions.alertAboutConnectionError
import com.example.hsmassistantandroid.extensions.handleAPIError
import com.example.hsmassistantandroid.extensions.handleAPIErrorForRequest
import com.example.hsmassistantandroid.ui.activities.SecondActivity
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

private val TAG: String = NetworkManager::class.java.simpleName

class UnsafeOkHttpClient {
    companion object {
        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }

                return builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}


class NetworkManager {
    private val sessaoRouter: sessaoEndPoint
    private val objetosRouter: objetosEndPoint
    private val usuarioRouter: usuarioEndPoint

    companion object {
        lateinit var BASE_URL: String
    }

    constructor(context: Context?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val url = sharedPreferences.getString("BASE_URL", null)
        val completeUrl = "https://$url/api/"
        BASE_URL = completeUrl
        val unsafeClient = UnsafeOkHttpClient.getUnsafeOkHttpClient().build()

        // setLenient : to acpect malformed JSONs
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(unsafeClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        sessaoRouter = retrofit.create(sessaoEndPoint::class.java)
        objetosRouter = retrofit.create(objetosEndPoint::class.java)
        usuarioRouter = retrofit.create(usuarioEndPoint::class.java)
    }

    //USUARIO

    fun runGetAcl(token: String, usr: String, callback: Callback<ResponseBody6>) {
        val json = JSONObject()
        json.put("usr", usr)

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = usuarioRouter.getAcl(requestBody, token)
        call.enqueue(callback)
    }

    fun runUpdateAcl(token: String, usr: String, acl: Int, callback: Callback<ResponseBody0>) {
        val json = JSONObject()
        json.put("usr", usr)
        json.put("acl", acl)

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = usuarioRouter.updateAcl(requestBody, token)
        call.enqueue(callback)
    }

    fun runListUsrsTrust(token: String, op: Int, usr: String, callback: Callback<ResponseBody5>) {
        val json = JSONObject()
        json.put("op", op)
        json.put("usr", usr)

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = usuarioRouter.listUsersTrust(requestBody, token)
        call.enqueue(callback)
    }

    fun runListUsrs(fragment: Fragment, token: String, callback: Callback<ResponseBody4>) {
        val probeCallback = object : Callback<ResponseBody3> {
            override fun onFailure(call: Call<ResponseBody3>?, t: Throwable?) {
                Log.d(TAG, "Probe Fail 1")
            }

            override fun onResponse(call: Call<ResponseBody3>?, response: Response<ResponseBody3>?) {
                if(response?.isSuccessful!!) {
                    val call = usuarioRouter.listUsrs(token)
                    call.enqueue(callback)
                }
                else {
                    Log.d(TAG, "handleAPIErrorForRequest YES")

                    val primaryCall = { updatedToken: String ->
                        Log.d(TAG, "Vai chamar o callback primario: token $updatedToken")

                        val primaryCall = usuarioRouter.listUsrs(updatedToken)
                        primaryCall.enqueue(callback)
                    }
                    handleAPIErrorForRequest(fragment, response.errorBody(), primaryCall)
                }
            }
        }
        runProbe(token, probeCallback)
    }

    fun runCreateUsr(token: String, usr: String, pwd: String, acl: Int, callback: Callback<ResponseBody>) {
        val json = JSONObject()
        json.put("usr", usr)
        json.put("pwd", pwd)
        json.put("acl", acl)

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = usuarioRouter.createUsr(requestBody, token)
        call.enqueue(callback)
    }

    fun runChangePwd(token: String, newPwd: String, callback: Callback<ResponseBody0>) {
        val json = JSONObject()
        json.put("pwd", newPwd)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = usuarioRouter.changePwd(requestBody, token)
        call.enqueue(callback)
    }

    //OBJETOS
    fun runObjExp(objId: String, token: String, callback: Callback<ResponseBody>) {
        val json = JSONObject()
        json.put("obj", objId)

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = objetosRouter.objExp(requestBody, token)
        call.enqueue(callback)
    }

    fun runGetObjInfo(objId: String, token: String, callback: Callback<ResponseBody7>) {
        val json = JSONObject()
        json.put("obj", objId)

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = objetosRouter.getObjInfo(requestBody, token)
        call.enqueue(callback)
    }

    fun runListObjects(token: String, callback: Callback<ResponseBody2>) {
        val call = objetosRouter?.listObjs(token)
        call.enqueue(callback)
//        val probeCallback = object : Callback<ResponseBody3> {
//            override fun onFailure(call: Call<ResponseBody3>?, t: Throwable?) {
//                Log.d(TAG, "Probe Fail 1")
//            }
//
//            override fun onResponse(call: Call<ResponseBody3>?, response: Response<ResponseBody3>?) {
//                if(response?.isSuccessful!!) {
//                    Log.d(TAG, "Beleza")
//                    val mCall = objetosRouter.listObjs(token)
//                    mCall.enqueue(callback)
//                }
//                else {
//                    Log.d(TAG, "Probe Fail 2")
//
//                    val myCall = {
//                        val mCall = objetosRouter.listObjs(token)
//                        mCall.enqueue(callback)
//                    }
//
//                    handleAPIErrorForRequest(response.errorBody(), myCall)
//                }
//            }
//        }
//        runProbe(token, probeCallback)
    }

    //SESSAO
    fun runClose(token: String, callback: Callback<ResponseBody0>) {
        val json = JSONObject()
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        val call = sessaoRouter.close(requestBody, token)
        call.enqueue(callback)
    }

    fun runProbe(token: String, callback: Callback<ResponseBody3>) {
        val call = sessaoRouter.probe(token)
        call.enqueue(callback)
    }

    fun runAuth(usr: String, pwd: String, otp: String, callback: Callback<ResponseBody1>) {
        val json = JSONObject()
        json.put("usr", usr)
        json.put("pwd", pwd)
//        json.put("otp", otp)

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = sessaoRouter.auth(requestBody)
        call.enqueue(callback)
    }
}
