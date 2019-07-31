package com.example.hsmassistantandroid.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.data.errorBody
import com.example.hsmassistantandroid.ui.activities.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.network.MIHelper
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.ui.activities.DeviceSelectionActivity
import com.example.hsmassistantandroid.ui.activities.SecondActivity
import com.example.hsmassistantandroid.ui.usuário.ServiceStatus
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val minPwdLenght = 8
private val TAG: String = "Util"

fun handleAPIError(fragment: Fragment, error: ResponseBody?, callback: (response: String) -> Unit? = {}) {
    val message: String

    val errorStream = error?.byteStream().toString()
    val rc = errorStream.substringAfter("\"rc\": ").substringBefore(",")
    val rd = errorStream.substringAfter("\"rd\":  \"").substringBefore("\"")
    val mErrorBody = errorBody(rc.toLong(), rd)

    val activity = fragment.requireActivity()

    when(mErrorBody.rd) {
        "ERR_ACCESS_DENIED" -> {
            message = activity.getString(R.string.ERR_ACCESS_DENIED_message)
            if(activity !is MainActivity) {
                loginWithPreviusCredentials(fragment)
//                goToLoginScreen(fragment)
            }
        }
        "ERR_INVALID_KEY" -> {
            message = activity.getString(R.string.ERR_INVALID_KEY_message)
            if(activity !is MainActivity) {
                goToLoginScreen(fragment)
            }
        }
        "ERR_INVALID_PAYLOAD" -> message = activity.getString(R.string.ERR_INVALID_PAYLOAD_message)
        "ERR_USR_NOT_FOUND" -> message = activity.getString(R.string.ERR_USR_NOT_FOUND_message)
        "ERR_USR_ALREADY_EXISTS" -> {
            message = activity.getString(R.string.ERR_USR_ALREADY_EXISTS_message)
        }
        else -> {
            message = activity.getString(R.string.ERR_DESCONHECIDO_message)
            Log.d(TAG, mErrorBody.rd)
        }
    }
    callback(message)
}

fun handleAPIErrorForRequest(error: ResponseBody?, callback: () -> Unit? = {}) {
    val message: String

    val errorStream = error?.byteStream().toString()
    val rc = errorStream.substringAfter("\"rc\": ").substringBefore(",")
    val rd = errorStream.substringAfter("\"rd\":  \"").substringBefore("\"")
    val mErrorBody = errorBody(rc.toLong(), rd)

    callback()

//    when(mErrorBody.rd) {
//        "ERR_ACCESS_DENIED" -> {
//            callback()
//        }
//        "ERR_INVALID_KEY" -> {
//            callback()
//        }
//        else -> {
//            val erro = mErrorBody.rd
//            Log.d(TAG, "Outro erro: $erro")
//        }
//    }
}

fun loginWithPreviusCredentials(fragment: Fragment) {
    val callback = object : Callback<ResponseBody1> {
        override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
//            alertAboutConnectionError(fragment.view)
        }

        override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
            if(response?.isSuccessful!!) {
                Log.d(TAG, "reLogin is Successful")
                val tokenString = "HSM " + response?.body()?.token
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.context)
                val editor = sharedPreferences.edit()
                editor.putString("TOKEN", tokenString)
                //TODO talvez mudar para commit
                editor.apply()
            }
            else {
                Log.d(TAG, "MERSA")
//                handleAPIError(fragment, response.errorBody())
            }
        }
    }

    val networkManager = NetworkManager(fragment.context)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.context)
    val user = sharedPreferences.getString("USER", null)
    val pwd = sharedPreferences.getString("PWD", null)

    networkManager.runAuth(user, pwd, "", callback)
}

fun alertAboutConnectionError(view: View?) : Boolean {
    if(view == null) {
        return false
    }

    val isConnected = isNetworkConnected(view.context)
    if (isConnected == false ) {
        val message = view.context?.getString(R.string.noInternet_message)
        Snackbar.make(view, message!!, Snackbar.LENGTH_LONG).show()
    }

    else {

        Log.d(TAG, "Maybe Service not started")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        val initKey = sharedPreferences.getString("INIT_KEY", null)

        //Start by it self
        if(initKey != null) {
            val successCallback = {
                Snackbar.make(view, "Serviço iniciado", Snackbar.LENGTH_LONG).show()
            }

            val errorCallback = { errorMessage: String ->
                Snackbar.make(view, "Não foi possivel iniciar o serviço", Snackbar.LENGTH_LONG).show()
                Log.d(TAG, errorMessage)
                Unit
            }
            MIHelper.serviceStartProcess("12345678", view.context, successCallback, errorCallback)
        }

        //Needs user input
        else {
            val title = view.context?.getString(R.string.ERR_SERVICE_NOT_STARTED_title)
            val message = view.context?.getString(R.string.ERR_SERVICE_NOT_STARTED_message)

            val dialog = AlertDialog.Builder(view.context)
                .setTitle(title)
                .setMessage(message)

            dialog.setPositiveButton("Configurar serviço do HSM") { dialogInterface, i ->
                Log.d(TAG, "Vai para tela adequada")
//                    goToSetUpScreen()
            }
        }


//        val caseIsStarted = {
//            Log.d(TAG, "Service is started")
//            val message = view.context?.getString(R.string.ERR_DESCONHECIDO_message)
//            Snackbar.make(view, message!!, Snackbar.LENGTH_LONG).show()
//        }
//
//        val caseNotStarted = {
//            Log.d(TAG, "Service not started")
//
//            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
//            val initKey = sharedPreferences.getString("INIT_KEY", null)
//
//            //Start by it self
//            if(initKey != null) {
//                val successCallback = {
//                    Snackbar.make(view, "Serviço iniciado", Snackbar.LENGTH_LONG).show()
//                }
//
//                val errorCallback = { errorMessage: String ->
//                    Snackbar.make(view, "Não foi possivel iniciar o serviço", Snackbar.LENGTH_LONG).show()
//                    Log.d(TAG, errorMessage)
//                    Unit
//                }
//
//                MIHelper.serviceStartProcess(view.context, successCallback, errorCallback)
//            }
//
//            //Needs user input
//            else {
//                val title = view.context?.getString(R.string.ERR_SERVICE_NOT_STARTED_title)
//                val message = view.context?.getString(R.string.ERR_SERVICE_NOT_STARTED_message)
//
//                val dialog = AlertDialog.Builder(view.context)
//                    .setTitle(title)
//                    .setMessage(message)
//
//                dialog.setPositiveButton("Configurar serviço do HSM") { dialogInterface, i ->
//                    Log.d(TAG, "Vai para tela adequada")
////                    goToSetUpScreen()
//                }
//            }
//
//            Unit
//        }
//
//        MIHelper.isServiceStarted(view.context ,caseIsStarted, caseNotStarted)
    }

    return isConnected
}



fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { cb(s.toString()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

// MARK: - INPUT ERROR HANDLING

fun validUsr(context: Context?, usrField: TextInputLayout): Boolean {
    val input = usrField.editText!!.text.toString()
    if(input.isEmpty()) {
        return false
    }
    val isAlplhanumeric = input.matches("[A-Za-z0-9]*".toRegex())
    if(isAlplhanumeric == false) {
        usrField.error = context!!.getString(R.string.ERR_INPUT_notAlphanumeric)
    }
    return isAlplhanumeric
}

fun validPwd(context: Context?, pwdField: EditText): Boolean {
    val input = pwdField.text.toString()
    if(input.isEmpty()) {
        return false
    }

    if(input.length >= minPwdLenght) {
        return true
    }
    else {
        pwdField.error = context!!.getString(R.string.pwd_too_short)
        return false
    }
}

fun validPwd(context: Context?, pwdField: TextInputLayout): Boolean {
    val input = pwdField.editText!!.text.toString()
    if(input.isEmpty()) {
        return false
    }

    if(input.length >= minPwdLenght) {
        return true
    }
    else {
        pwdField.error = context!!.getString(R.string.pwd_too_short)
        return false
    }
}

fun validPwdConfirmation(context: Context?, pwd: String, pwdConfirmationField: TextInputLayout): Boolean {
    val input = pwdConfirmationField.editText!!.text.toString()
    if(input.isEmpty()) {
        return false
    }
    if(input == pwd) {
        return true
    }
    else {
        pwdConfirmationField.error = context!!.getString(R.string.wrong_confirmation)
        return false
    }
}

fun fieldsAreValid(context: Context?, mTextInputLayoutArray: Array<TextInputLayout>): Boolean {
    var isValid = true

    for(mTextInputlayout: TextInputLayout in mTextInputLayoutArray) {
        val input = mTextInputlayout.editText!!.text.toString()
        if(input.isEmpty()) {
            mTextInputlayout.error = context!!.getString(com.example.hsmassistantandroid.R.string.required_field)
            isValid = false
        }
    }
    return isValid
}

fun removeTokenFromSecureLocation(activity: Activity) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    val editor = sharedPreferences.edit()
    editor.remove("TOKEN")
    editor.commit()
}

fun goToLoginScreen(fragment: Fragment) {
    fragment.findNavController().navigate(R.id.goto_Login)
    fragment.requireActivity().finish()
}

@SuppressLint("ApplySharedPref")
fun goToSetUpScreen(activity: Activity) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    val editor = sharedPreferences.edit()
    editor.remove("BASE_URL")
    editor.commit()

    val intent = Intent(activity, DeviceSelectionActivity::class.java)
    activity.startActivity(intent)
    activity.finish()
}

fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocus = activity.currentFocus ?: return
    inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
}

fun isNetworkConnected(context: Context?): Boolean {
    val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}
