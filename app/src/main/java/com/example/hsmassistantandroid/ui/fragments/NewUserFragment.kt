package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.extensions.*
import com.google.android.material.snackbar.Snackbar
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

class NewUserFragment : mainFragment() {
    private val networkManager = NetworkManager(context)
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
                alertAboutConnectionError(view)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if(response!!.isSuccessful) {
                    hideSoftKeyboard(requireActivity())
                    context!!.toast(getString(R.string.userCreated_toast))
                    findNavController().navigate(R.id.action_newUserFragment_to_gestaoUsuarioFragment2)
                }
                else {
                    val message = handleAPIError(this@NewUserFragment, response.errorBody())
                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        }
        val newUserName = newUsrEditText.editText!!.text.toString()
        val newPassword = newUsrPwdEditText.editText!!.text.toString()
        networkManager.runCreateUsr(tokenString!!, newUserName, newPassword, newUserDefaultACL, callbackList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_new_user, container, false)
        view.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun didtapCreateUser() {
        if(fieldsAreValid(context, arrayOf(newUsrEditText, newUsrPwdEditText,
                newUsrPwdRepeatEditText)) == false) {
            return
        }

        if(validUsr(context, newUsrEditText))

        if(validPwd(context, newUsrPwdEditText) ==  false) return

        if(validPwdConfirmation(context, newUsrPwdEditText.editText!!.text.toString(),
                newUsrPwdRepeatEditText) == false) {
            return
        }
        createUserRequest()
    }

    fun setUpViews() {
        newUsrEditText.editText!!.onChange { newUsrEditText.error = null }
        newUsrPwdEditText.editText!!.onChange { newUsrPwdEditText.error = null }
        newUsrPwdRepeatEditText.editText!!.onChange { newUsrPwdRepeatEditText.error = null }

        val fieldUsr = newUsrEditText.editText
        fieldUsr!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                validUsr(context, newUsrEditText)
            }
        }

        val fieldNewPwd = newUsrPwdEditText.editText
        fieldNewPwd!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                validPwd(context, newUsrPwdEditText)
            }
        }

        val fieldNewPwdConfirmation =  newUsrPwdRepeatEditText.editText
        fieldNewPwdConfirmation!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                validPwdConfirmation(context, newUsrPwdEditText.editText!!.text.toString(),
                    newUsrPwdRepeatEditText)
            }
        }


        createUserButton.setOnClickListener { didtapCreateUser() }
    }
}