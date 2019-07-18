package com.example.hsmassistantandroid.data

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import org.jetbrains.anko.doAsync
import java.io.PrintWriter
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

private val TAG: String = MIHelper::class.java.simpleName


open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

class MIHelper private constructor(ipAdress: String) {
    private val MI_PORT = 3344

    lateinit var myAddress: String
    init {
        myAddress = ipAdress
    }

    fun connectToAddress(address: String = myAddress, context: Context, successCallback: () -> Unit, errorCallback: (response: String) -> Unit?) {
        //TODO validar address

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

        val sslContext = SSLContext.getInstance("SSL")

        doAsync {
            try {
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())

                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                val sslsocket: SSLSocket = sslSocketFactory.createSocket(address, MI_PORT) as SSLSocket
                val input = Scanner(sslsocket.inputStream)
                val output = PrintWriter(sslsocket.outputStream, true)

//                val bundle = bundleOf("input" to input, "output" to output)

                output.println("MI_HELLO")
                val response = input.nextLine()
                Log.d(TAG, response)
                if(response == "MI_ACK 00000000 ") {
                    Log.d(TAG, "conectado")
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val editor = sharedPreferences.edit()
                    editor.putString("IP", address)
                    successCallback()
                }
                else {
                    Log.d(TAG, "falha ao connectar")
                    errorCallback(response)
                }

            } catch (e: Exception) {
                Log.d(TAG, "outra falha: $e")
            }

        }
    }

    companion object : SingletonHolder<MIHelper, String>(::MIHelper)
}