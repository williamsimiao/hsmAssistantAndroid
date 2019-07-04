package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.data.ResponseBody4
import com.example.hsmassistantandroid.extensions.alertAboutConnectionError
import com.example.hsmassistantandroid.extensions.goToLoginScreen
import com.example.hsmassistantandroid.extensions.handleAPIError
import com.example.hsmassistantandroid.extensions.removeTokenFromSecureLocation
import com.example.hsmassistantandroid.ui.activities.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import kotlinx.android.synthetic.main.fragment_user_options.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = UserOptions::class.java.simpleName

class UserOptions : mainFragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }

    fun didTapcloseButton() {
        val callbackClose = object : Callback<ResponseBody0> {
            override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }
            override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                if(response?.isSuccessful!!) {
                    removeTokenFromSecureLocation(requireActivity())
                    goToLoginScreen(requireActivity())
                }
                else {
                    handleAPIError(requireActivity(), response.errorBody())
                }
            }
        }

        AlertDialog.Builder(requireContext()).setTitle("Encerrar sessão")
            .setMessage("Deseja mesmo encerrar a sessão ?")
            .setNegativeButton(android.R.string.cancel){dialogInterface, i -> }
            .setPositiveButton(getString(R.string.yes)) { dialogInterface, i ->
                networkManager.runClose(tokenString!!, callbackClose)
            }
            .show()
    }

    fun didTapChangePwd() {
        findNavController().navigate(R.id.action_userOptions_to_changePwdFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        closeButton.setOnClickListener { didTapcloseButton() }
        changePwd.setOnClickListener { didTapChangePwd() }
    }
}
