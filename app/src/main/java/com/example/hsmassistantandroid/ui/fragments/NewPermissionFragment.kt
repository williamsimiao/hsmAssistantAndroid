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
import com.example.hsmassistantandroid.data.aclStruct
import com.example.hsmassistantandroid.extensions.handleNetworkResponse
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
    var userName: String? = null
    var userAcl: Int? = null

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
        if(userAcl == null) {
            //TODO: colocar 0
            userAcl = 15
        }
        //TODO: Apagar
        if(userName == null) {
            userName = "queiroz"
        }
        setUpSwitches(userAcl!!)
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
        val mAclStruct = aclStruct(aclInteger)
        if(mAclStruct.rawValue and mAclStruct.obj_read == mAclStruct.obj_read) {
            Lerswitch.isChecked = true
        }
        if(mAclStruct.rawValue and mAclStruct.obj_create == mAclStruct.obj_create) {
            Criarswitch.isChecked = true
        }
        if(mAclStruct.rawValue and mAclStruct.obj_del == mAclStruct.obj_del) {
            Deleteswitch.isChecked = true
        }
        if(mAclStruct.rawValue and mAclStruct.obj_update == mAclStruct.obj_update) {
            Atualizarswitch.isChecked = true
        }
    }

    fun getIntFromSwitches(): Int {
        //Just an aux to access the masks
        val aux = aclStruct(0)
        var unionOfBits = 0
        if(Lerswitch.isChecked) {
            unionOfBits = unionOfBits or aux.obj_read
        }
        if(Criarswitch.isChecked) {
            unionOfBits = unionOfBits or aux.obj_create
        }
        if(Deleteswitch.isChecked) {
            unionOfBits = unionOfBits or aux.obj_del
        }
        if(Atualizarswitch.isChecked) {
            unionOfBits = unionOfBits or aux.obj_update
        }
        return unionOfBits
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

                }
            }
        }
        if(userName == null) return
        networkManager.runUpdateAcl(tokenString!!, userName!!, newAcl, callbackUpdate)
    }
}
