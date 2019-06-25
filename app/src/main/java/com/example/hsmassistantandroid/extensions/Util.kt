package com.example.hsmassistantandroid.extensions

import android.app.PendingIntent.getActivity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.hsmassistantandroid.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import org.jetbrains.anko.runOnUiThread

private val minPwdLenght = 8

fun handleNetworkResponse(responseCode: Int?, context: Context): String {
    if(responseCode == null) {
        return "failed null"
    }
    when(responseCode) {
        in 200..299 -> return "sucess"
        in 401..499 -> {
//            context.runOnUiThread {
//                val builder = AlertDialog.Builder(context).setTitle(context.getString(R.string.internal_error_dialog_title))
//                    .setMessage(context.getString(R.string.internal_error_dialog_message))
//                    .setPositiveButton(android.R.string.ok) { _, _ -> }
//                builder.create().show()
//            }


            return "authenticationError"
        }
        500 -> {
            context.runOnUiThread {
                val builder = AlertDialog.Builder(context).setTitle(context.getString(R.string.internal_error_dialog_title))
                    .setMessage(context.getString(R.string.internal_error_dialog_message))
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                builder.create().show()
            }
            return "Internal Server error"
        }
        in 501..599 -> return "badRequest"
        600 -> return "outdated"
        else -> return "failed"
    }
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
