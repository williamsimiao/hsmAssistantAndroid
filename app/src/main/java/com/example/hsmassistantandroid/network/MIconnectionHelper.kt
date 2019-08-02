package com.example.hsmassistantandroid.network

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import org.jetbrains.anko.doAsync
import java.io.PrintWriter
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

private val TAG: String = MIHelper::class.java.simpleName

object  MIHelper {
    private const val MI_PORT = 3344
    lateinit var myAddress: String
    lateinit var input: Scanner
    lateinit var output: PrintWriter

    fun serviceStartProcess(key: String ,context: Context, serviceStartSuccess: () -> Unit, serviceStartErrorCallback: (response: String) -> Unit?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val address = sharedPreferences.getString("BASE_URL", null)

        //AUTENTICATE
        val successCallback2 = {
            Log.d(TAG, "Autenticou-se")
            startService(context, serviceStartSuccess, serviceStartErrorCallback)
            Unit
        }
        val errorCallback2 = { errorMessage: String ->
            Log.d(TAG, "autentication $errorMessage")
            Unit
        }

        //CONNECT
        val successCallback = {
            Log.d(TAG, "Conectou-se")
            autenticateWithKey(key, successCallback2, errorCallback2)
        }
        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }

        connectToAddress(address, context, successCallback, errorCallback)
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

                val sslsocket: SSLSocket = sslSocketFactory.createSocket(address,
                    MI_PORT
                ) as SSLSocket
                input = Scanner(sslsocket.inputStream)
                output = PrintWriter(sslsocket.outputStream, true)

                output.println("MI_HELLO")
                val response = input.nextLine()
                Log.d(TAG, response)
                if(response == "MI_ACK 00000000 ") {
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val editor = sharedPreferences.edit()
                    editor.putString("BASE_URL", address)
                    editor.apply()

                    successCallback()
                }
                else {
                    Log.d(TAG, "falha ao connectar")
                    errorCallback(response)
                }

            } catch (e: Exception) {
                Log.d(TAG, "outra falha1: $e")
            }
        }
    }

    fun autenticateWithKey(key: String, successCallback: () -> Unit, errorCallback: (response: String) -> Unit?) {
        doAsync {
            try {
                output.println("MI_MINI_AUTH $key")
                val response = input.nextLine()
                if(response == "MI_ACK 00000000 ") {
                    successCallback()
                }
                else {
                    errorCallback(response)
                }
            } catch (e: Exception) {
                Log.d(TAG, "falha2: $e")
            }
        }
    }

    fun isServiceStarted(context: Context , caseFalse: () -> Unit, caseTrue: () -> Unit) {
        doAsync {
            try {
                output.println("MI_SVC_STATUS")
                val response = input.nextLine()
                when(response) {
                    "MI_ACK 00000000 " -> caseFalse()
                    "MI_ACK 00000001 " -> caseTrue()
                }

            } catch (e: Exception) {
                Log.d(TAG, "falha8: $e")
            }
        }
    }

    fun isFirstBoot(caseFalse: () -> Unit, caseTrue: () -> Unit) {
        doAsync {
            try {
                output.println("MI_FIRST_BOOT")
                val response = input.nextLine()
                when(response) {
                    "MI_ACK 00000000 " -> caseFalse()
                    "MI_ACK 00000001 " -> caseTrue()
                }

            } catch (e: Exception) {
                Log.d(TAG, "falha8: $e")
            }
        }
    }

    fun isFirstBootProcess(address: String, context: Context, caseFalse: () -> Unit, caseTrue: () -> Unit) {
        //CONNECT
        val successCallback = {
            Log.d(TAG, "Conectou-se")
            isFirstBoot(caseFalse, caseTrue)
        }
        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }

        connectToAddress(address, context, successCallback, errorCallback)
    }

    fun startService(context: Context , successCallback: () -> Unit, errorCallback: (response: String) -> Unit?) {
        doAsync {
            try {
                output.println("MI_SVC_STATUS")
                val response = input.nextLine()
                when(response) {
                    "MI_ACK 00000000 " -> {
                        output.println("MI_SVC_START")
                        val response = input.nextLine()

                        if(response == "MI_ACK 00000000 ") {
                            successCallback()
                        }
                        else {
                            errorCallback(response)
                        }
                    }
                    "MI_ACK 00000001 " -> successCallback()
                }

            } catch (e: Exception) {
                Log.d(TAG, "falha3: $e")
            }
        }
    }

    fun stopService(successCallback: () -> Unit, errorCallback: (response: String) -> Unit?) {
        doAsync {
            try {
                output.println("MI_SVC_STATUS")
                val response = input.nextLine()
                when(response) {
                    "MI_ACK 00000001 " -> {
                        output.println("MI_SVC_STOP")
                        val response = input.nextLine()

                        if(response == "MI_ACK 00000000 ") {
                            successCallback()
                        }
                        else {
                            errorCallback(response)
                        }
                    }
                    "MI_ACK 00000000 " -> successCallback()
                }

            } catch (e: Exception) {
                Log.d(TAG, "falha9: $e")
            }
        }
    }

    fun disconnect(successCallback: () -> Unit, errorCallback: (response: String) -> Unit?) {
        doAsync {
            try {
                output.println("MI_CLOSE")
                val response = input.nextLine()
                if(response == "MI_ACK 00000000 ") {
                    successCallback()
                }
                else {
                    errorCallback(response)
                }
            } catch (e: Exception) {
                Log.d(TAG, "falha: $e")
            }
        }
    }
}