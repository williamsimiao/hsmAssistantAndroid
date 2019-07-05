package com.example.hsmassistantandroid.extensions

import android.app.Activity
import android.content.Context
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
import androidx.navigation.fragment.findNavController

private val minPwdLenght = 8
private val TAG: String = "Util"

fun handleAPIError(fragment: Fragment, error: ResponseBody?): String? {
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
                goToLoginScreen(fragment)
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
    return message
}

fun alertAboutConnectionError(view: View?) : Boolean {
    if(view == null) {
        return false
    }

    val isConnected = isNetworkConnected(view.context)
    val title: String?
    if (isConnected == false ) {
        title = view.context?.getString(R.string.noInternet_message)
    }
    else {
        title = view.context?.getString(R.string.ERR_DESCONHECIDO_message)
    }
    Snackbar.make(view, title!!, Snackbar.LENGTH_LONG).show()
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
