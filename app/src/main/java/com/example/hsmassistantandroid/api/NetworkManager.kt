package com.example.hsmassistantandroid.api

import android.util.Log
import com.example.hsmassistantandroid.data.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import okhttp3.ResponseBody

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
//        const val BASE_URL = "https://10.61.53.210"
        const val BASE_URL = "https://hsmlab63.dinamonetworks.com/api/"
    }

    init {
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

    fun runGetAcl(token: String, usr: String, callback: Callback<ResponseBody6>){
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

    fun runListUsrs(token: String, callback: Callback<ResponseBody4>) {
        val call = usuarioRouter.listUsrs(token)
        call.enqueue(callback)
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

    fun runListObjetcs(token: String, callback: Callback<ResponseBody2>) {
        val call = objetosRouter?.listObjs(token)
        if (call != null) {
            call.enqueue(callback)
        }
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

        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val call = sessaoRouter.auth(requestBody)
        call.enqueue(callback)
    }
}
