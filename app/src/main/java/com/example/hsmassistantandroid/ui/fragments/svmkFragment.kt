package com.example.hsmassistantandroid.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.data.MIHelper
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.activities.MainActivity
import com.example.hsmassistantandroid.ui.activities.SecondActivity
import kotlinx.android.synthetic.main.fragment_discovery.*
import kotlinx.android.synthetic.main.fragment_svmk.*
import java.io.PrintWriter
import java.util.*

private val TAG: String = svmkFragment::class.java.simpleName

class svmkFragment : mainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_svmk, container, false)
        view.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        iniciarToButton.setOnClickListener {
            sendAuth()
        }
        svmkEditText.editText!!.onChange { svmkEditText.error = null }
    }

    fun sendAuth() {
        val key = svmkEditText.editText!!.text.toString()

        val successCallback = {
            onAuthenticationCompleted()
        }

        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }

        if(validPwd(context, svmkEditText)) {
            MIHelper.autenticateWithKey(key, successCallback, errorCallback)
        }
    }

    fun onAuthenticationCompleted() {
        getActivity()?.runOnUiThread {
            Toast.makeText(context!!, "Autenticado", Toast.LENGTH_SHORT).show()
        }

        val successCallback = {
            onServiceStarted()
        }

        val errorCallback = { errorMessage: String ->
            Log.d(TAG, errorMessage)
            Unit
        }

        MIHelper.startService(successCallback, errorCallback)
    }

    fun onServiceStarted() {
        getActivity()?.runOnUiThread {
            Toast.makeText(context!!, "Servico iniciado", Toast.LENGTH_SHORT).show()
        }
        val intent = Intent(context, SecondActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    fun showInvalidTokenDialog() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.invalidTokenDialog_title))
            .setMessage(getString(R.string.invalidTokenDialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .show()
    }
}
