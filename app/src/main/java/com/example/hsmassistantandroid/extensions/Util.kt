package com.example.hsmassistantandroid.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.hsmassistantandroid.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*

fun handleNetworkResponse(responseCode: Int?): String {
    if(responseCode == null) {
        return "failed null"
    }
    when(responseCode) {
        in 200..299 -> return "sucess"
        in 401..500 -> return "authenticationError"
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

fun fieldsAreValid(contex: Context, mTextInputLayoutArray: Array<TextInputLayout>): Boolean {
    var isValid = true

    for(mTextInputlayout: TextInputLayout in mTextInputLayoutArray) {
        val input = mTextInputlayout.editText!!.text.toString()
        if(input == "") {
            mTextInputlayout.error = contex.getString(R.string.required_field)
            isValid = false
        }
    }
    return isValid
}
