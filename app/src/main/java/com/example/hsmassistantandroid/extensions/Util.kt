package com.example.hsmassistantandroid.extensions

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.data.errorBody
import com.example.hsmassistantandroid.ui.activities.MainActivity
import com.example.hsmassistantandroid.ui.fragments.gestaoUsuarioFragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import okhttp3.Response
import okhttp3.ResponseBody
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.http.Body

private val minPwdLenght = 8
private val TAG: String = "Util"

fun handleAPIError(activity: Activity, mErrorBody: ResponseBody?): String? {
    val message: String

    val errorStream = mErrorBody?.byteStream().toString()
    val rc = errorStream.substringAfter("\"rc\": ").substringBefore(",")
    val rd = errorStream.substringAfter("\"rd\":  \"").substringBefore("\"")
    val errorBody = errorBody(rc.toLong(), rd)

    when(errorBody.rd) {
        "ERR_ACCESS_DENIED" -> {
            message = activity.getString(R.string.ERR_ACCESS_DENIED_message)
            if(activity !is MainActivity) {
                goToLoginScreen(activity, true)
            }
        }
        "ERR_USR_NOT_FOUND" -> message = activity.getString(R.string.ERR_USR_NOT_FOUND_message)
        else -> {
            message = activity.getString(R.string.ERR_DESCONHECIDO_message)
            Log.d(TAG, errorBody.rd)
        }
    }
    return message
}

fun alertAboutConnectionError(context: Context?) : Boolean {
    val isConnected = isNetworkConnected(context)

    val title: String?
    val message: String?
    if (isConnected == false ) {
        title = context?.getString(R.string.noInternetDialog_message)
        message = context?.getString(R.string.noInternetDialog_message)
    }
    else {
        title = "Erro"
        message = context!!.getString(R.string.ERR_DESCONHECIDO_message)
    }
    AlertDialog.Builder(context!!).setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { _, _ -> }
        .show()
    return isConnected
}

fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { cb(s.toString()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun validPwdConfirmation(context: Context?, pwd: String, pwdConfirmationField: TextInputLayout): Boolean {
    val input = pwdConfirmationField.editText!!.text.toString()
    if(input == pwd) {
        return true
    }
    else {
        pwdConfirmationField.error = context!!.getString(R.string.wrong_confirmation)
        return false
    }
}

fun validPwd(context: Context?, pwdField: TextInputLayout): Boolean {
    val input = pwdField.editText!!.text.toString()
    if(input.length >= minPwdLenght) {
        return true
    }
    else {
        pwdField.error = context!!.getString(R.string.pwd_too_short)
        return false
    }
}

fun fieldsAreValid(context: Context?, mTextInputLayoutArray: Array<TextInputLayout>): Boolean {
    var isValid = true

    for(mTextInputlayout: TextInputLayout in mTextInputLayoutArray) {
        val input = mTextInputlayout.editText!!.text.toString()
        if(input == "") {
            mTextInputlayout.error = context!!.getString(R.string.required_field)
            isValid = false
        }
    }
    return isValid
}

fun goToLoginScreen(activity: Activity) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    val editor = sharedPreferences.edit()
    editor.remove("TOKEN")
    editor.commit()

    val intent = Intent(activity, MainActivity::class.java)
    activity.startActivity(intent)
    activity.finish()
}

fun isNetworkConnected(context: Context?): Boolean {
    val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected //3
}
