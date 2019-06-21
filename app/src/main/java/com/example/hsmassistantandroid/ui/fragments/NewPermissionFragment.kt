package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_new_permission.*
import kotlinx.android.synthetic.main.fragment_user_options.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = NewPermissionFragment::class.java.simpleName

class NewPermissionFragment : Fragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        savePermissionButton.setOnClickListener { didTapSave() }
    }

    fun didTapSave() {
        val newAcl = getIntFromSwitches()
        updateAclRequest(newAcl)
        //TODO: voltar para tela de relacoes
        //TODO: e apresentar toast com o resultado da API
    }

    fun setUpSwitches(aclInteger: Int) {

    }

    fun getIntFromSwitches(): Int {
        return 0
    }

    fun switchChanged() {

    }



    fun updateAclRequest(newAcl: Int) {
        val callbackUpdate = object : Callback<ResponseBody0> {
            override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                Log.e(TAG, "Problem calling the API", t)
            }
            override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                response?.isSuccessful.let {
                    context!!.toast(getString(R.string.permissionSavedToastText))
                }
            }
        }
        networkManager.runUpdateAcl(tokenString!!, userId, newAcl, callbackUpdate)
    }
}
