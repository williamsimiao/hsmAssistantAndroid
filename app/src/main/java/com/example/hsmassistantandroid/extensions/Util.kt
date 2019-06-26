package com.example.hsmassistantandroid.extensions

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
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

fun handleAPIError(context: Context?, mErrorBody: ResponseBody?) {
    val message: String
    if(context == null) {
        return
    }
    val teste = mErrorBody?.byteStream().toString()
    val rc = teste.substringAfter("\"rc\": ").substringBefore(",")
    val rd = teste.substringAfter("\"rd\":  \"").substringBefore("\"")
    val errorBody = errorBody(rc.toLong(), rd)

    when(errorBody.rd) {
        "ERR_ACCESS_DENIED" -> message = context.getString(R.string.ERR_ACCESS_DENIED_message)
        else -> {
            message = context.getString(R.string.ERR_DESCONHECIDO_message)
            Log.d(TAG, errorBody.rd)
        }
    }
    context.toast(message)

}

//fun handleNetworkResponse(responseCode: Int?, context: Context): String {
//    if(responseCode == null) {
//        return "failed null"
//    }
//    when(responseCode) {
//        in 200..299 -> return "sucess"
//        in 401..499 -> {
////            context.runOnUiThread {
////                val builder = AlertDialog.Builder(context).setTitle(context.getString(R.string.internal_error_dialog_title))
////                    .setMessage(context.getString(R.string.internal_error_dialog_message))
////                    .setPositiveButton(android.R.string.ok) { _, _ -> }
////                builder.create().show()
////            }
//
//
//            return "authenticationError"
//        }
//        500 -> {
//            context.runOnUiThread {
//                val builder = AlertDialog.Builder(context).setTitle(context.getString(R.string.internal_error_dialog_title))
//                    .setMessage(context.getString(R.string.internal_error_dialog_message))
//                    .setPositiveButton(android.R.string.ok) { _, _ -> }
//                builder.create().show()
//            }
//            return "Internal Server error"
//        }
//        in 501..599 -> return "badRequest"
//        600 -> return "outdated"
//        else -> return "failed"
//    }
//}

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

fun goToLoginScreen(fragment: Fragment, shouldShowInvalidTokenDialog: Boolean) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.context)
    val editor = sharedPreferences.edit()
    editor.remove("TOKEN")
    editor.commit()

    val intent = Intent(fragment.context, MainActivity::class.java)
    intent.putExtra("shouldShowInvalidTokenDialog", shouldShowInvalidTokenDialog)
    fragment.startActivity(intent)
    fragment.requireActivity().finish()
}
