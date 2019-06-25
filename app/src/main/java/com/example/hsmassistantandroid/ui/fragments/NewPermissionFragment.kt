package com.example.hsmassistantandroid.ui.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.data.ResponseBody6
import com.example.hsmassistantandroid.data.aclStruct
import com.example.hsmassistantandroid.extensions.handleNetworkResponse
import kotlinx.android.synthetic.main.fragment_new_permission.*
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
    var systemACL: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TODO: deletar
        if(userName == null) userName = "master"
        //TODO: mudar userACl de acordo com o parametro recebido
        if(userAcl == null) {
            //caso em que clicou em de nova permissao
            userAcl = 0
        }
        getSystemAclRequest()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSwitches(userAcl!!)
        setUpViews()
    }

    fun setUpViews() {
        Lerswitch.setOnClickListener { didTapASwitch(it) }
        Criarswitch.setOnClickListener { didTapASwitch(it) }
        Deleteswitch.setOnClickListener { didTapASwitch(it) }
        Atualizarswitch.setOnClickListener { didTapASwitch(it) }

        savePermissionButton.setOnClickListener { didTapSave() }
    }

    fun didTapASwitch(view: View) {
        when(view) {
            Lerswitch -> {
                if(Lerswitch.isChecked == false) {
                    Criarswitch.isChecked = false
                    Deleteswitch.isChecked = false
                    Atualizarswitch.isChecked = false
                }
            }
            else -> {
                val mSwitch = view as Switch
                if(mSwitch.isChecked) {
                    Lerswitch.isChecked = true
                }
            }
        }
    }

    fun makeSwitchesHideOrNot(shouldHide: Boolean) {
        if(shouldHide) {
            Lerswitch.visibility = View.INVISIBLE
            Criarswitch.visibility = View.INVISIBLE
            Deleteswitch.visibility = View.INVISIBLE
            Atualizarswitch.visibility = View.INVISIBLE
        }
        else {
            Lerswitch.visibility = View.VISIBLE
            Criarswitch.visibility = View.VISIBLE
            Deleteswitch.visibility = View.VISIBLE
            Atualizarswitch.visibility = View.VISIBLE
        }
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

    fun composeFinalAcl(newAcl: Int): Int {
        val onlySystemAcl = systemACL!!.shr(4).shl(4)
        val finalAcl = onlySystemAcl or newAcl
        return finalAcl
    }

    fun getSystemAclRequest() {
        val callback = object : Callback<ResponseBody6> {
            override fun onFailure(call: Call<ResponseBody6>?, t: Throwable?) {
                Log.e(TAG, "Problem calling the API", t)
            }
            override fun onResponse(call: Call<ResponseBody6>?, response: Response<ResponseBody6>?) {
                if(response!!.isSuccessful) {
                    getActivity()?.runOnUiThread {
                        systemACL = response.body()!!.acl
                    }
                }
                else {
                    handleNetworkResponse(response?.code(), context!!)
                }
            }
        }
        if(userName == null) return
        networkManager.runGetAcl(tokenString!!, userName!!, callback)
    }

    fun updateAclRequest(newAcl: Int) {
        val callbackUpdate = object : Callback<ResponseBody0> {
            override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                Log.e(TAG, "Problem calling the API", t)
            }
            override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                response?.isSuccessful.let {
                    val codeMeaning = handleNetworkResponse(response?.code(), context!!)
                    if(codeMeaning == "sucess") {
                        context!!.toast(getString(R.string.permissionSavedToastText))
                    }
                }
            }
        }
        if(userName == null) return

        val finalAcl = composeFinalAcl(newAcl)
        networkManager.runUpdateAcl(tokenString!!, userName!!, finalAcl, callbackUpdate)
    }
}
