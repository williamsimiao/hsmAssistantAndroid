package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.extensions.fieldsAreValid
import com.example.hsmassistantandroid.extensions.onChange
import com.example.hsmassistantandroid.extensions.validPwd
import com.example.hsmassistantandroid.extensions.validPwdConfirmation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import kotlinx.android.synthetic.main.fragment_new_user.*
import kotlinx.android.synthetic.main.fragment_user_options.*
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = gestaoUsuarioFragment::class.java.simpleName
private val newUserDefaultACL = 80

class NewUserFragment : Fragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }

    fun createUserRequest() {
        val callbackList = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.isSuccessful.let {
                    context!!.toast("Usuário criado com sucesso")
                }
            }
        }
        val newUserName = newUsrEditText.editText!!.text.toString()
        val newPassword = newPwdEditText.editText!!.text.toString()
        networkManager.runCreateUsr(tokenString!!, newUserName, newPassword, newUserDefaultACL, callbackList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun didtapCreateUser() {
        if(fieldsAreValid(context, arrayOf(newUsrEditText, newPwdEditText,
                newRepeatPwdEditText)) == false) {
            return
        }

        if(validPwd(context, newPwdEditText) ==  false) return

        if(validPwdConfirmation(context, newPwdEditText.editText!!.text.toString(),
                newRepeatPwdEditText) == false) {
            return
        }

        createUserRequest()
    }

    fun setUpViews() {
        newUsrEditText.editText!!.onChange { newUsrEditText.error = null }
        newPwdEditText.editText!!.onChange { newPwdEditText.error = null }

        val fieldNewPwd = newPwdEditText.editText
        fieldNewPwd!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus && fieldNewPwd.text.toString().isNotEmpty()) {
                validPwd(context, newPwdEditText)
            }
        }

        val fieldNewPwdConfirmation =  newRepeatPwdEditText.editText
        fieldNewPwdConfirmation!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus && fieldNewPwdConfirmation.text.toString().isNotEmpty()) {
                validPwdConfirmation(context, newPwdEditText.editText!!.text.toString(),
                    newRepeatPwdEditText)
            }
        }
        
        newRepeatPwdEditText.editText!!.onChange { newRepeatPwdEditText.error = null }

        createUserButton.setOnClickListener { didtapCreateUser() }
    }

    companion object {
        fun newInstance(): NewUserFragment = NewUserFragment()
    }
}