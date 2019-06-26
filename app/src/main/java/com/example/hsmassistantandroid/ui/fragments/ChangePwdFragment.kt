package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.data.ResponseBody4
import com.example.hsmassistantandroid.extensions.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_change_pwd.*
import kotlinx.android.synthetic.main.fragment_change_pwd.newPwdEditText
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import kotlinx.android.synthetic.main.fragment_new_user.*
import kotlinx.android.synthetic.main.item_objetos.view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = gestaoUsuarioFragment::class.java.simpleName

class ChangePwdFragment : mainFragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }

    fun changePwdRequest() {
        val callback = object : Callback<ResponseBody0> {
            override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                alertAboutConnectionError(context)
            }

            override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                if(response!!.isSuccessful) {
                    context!!.toast(getString(R.string.pwdAlteredWithSuccess))
                    findNavController().navigate(R.id.action_changePwdFragment_to_userOptions)
                }
                else {
                    handleAPIError(context, response.errorBody())
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

        val view = inflater.inflate(R.layout.fragment_change_pwd, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        newPwdEditText.editText!!.onChange { newPwdEditText.error = null }
        newPwdConfirmationEditText.editText!!.onChange { newPwdConfirmationEditText.error = null }
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

