package com.example.hsmassistantandroid.ui.usuário

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.navigation.fragment.findNavController

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.Gestão.gestaoUsuarioFragment
import com.example.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_change_pwd.*
import kotlinx.android.synthetic.main.fragment_change_pwd.newPwdEditText
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = gestaoUsuarioFragment::class.java.simpleName

class ChangePwdFragment : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }


    fun changePwdRequest() {
        val callback = object : Callback<ResponseBody0> {
            override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }

            override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                if(response!!.isSuccessful) {
                    hideSoftKeyboard(requireActivity())
                    context!!.toast(getString(R.string.pwdAlteredWithSuccess))
                    findNavController().navigate(R.id.action_changePwdFragment_to_userOptions)
                }
                else {
                    val message = handleAPIError(this@ChangePwdFragment, response.errorBody())
                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        }
        val newPwd = newPwdEditText.editText!!.text.toString()
        networkManager.runChangePwd(tokenString!!, newPwd, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_change_pwd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        newPwdEditText.editText!!.onChange { newPwdEditText.error = null }
        newPwdConfirmationEditText.editText!!.onChange { newPwdConfirmationEditText.error = null }

        val fieldNewPwd = newPwdEditText.editText
        fieldNewPwd!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                validPwd(context, newPwdEditText)
            }
        }

        val fieldNewPwdConfirmation =  newPwdConfirmationEditText.editText
        fieldNewPwdConfirmation!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                validPwdConfirmation(context, newPwdConfirmationEditText.editText!!.text.toString(),
                    newPwdEditText)
            }
        }

        alterarButton.setOnClickListener { didTapAlterar() }
    }

    fun didTapAlterar() {
        if(fieldsAreValid(context, arrayOf(newPwdEditText,
                newPwdConfirmationEditText)) == false) {
            return
        }

        if(validPwd(context, newPwdEditText) ==  false) return

        if(validPwdConfirmation(context, newPwdEditText.editText!!.text.toString(),
                newPwdConfirmationEditText) == false) {
            return
        }

        changePwdRequest()
    }
}

