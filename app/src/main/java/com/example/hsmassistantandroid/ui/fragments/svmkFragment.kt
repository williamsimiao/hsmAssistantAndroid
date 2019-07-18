package com.example.hsmassistantandroid.ui.fragments

import android.content.Intent
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.activities.MainActivity
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
        svmkEditText.editText!!.onChange { deviceAddressEditText.error = null }
    }

    fun sendAuth() {
        val key = svmkEditText.editText!!.text.toString()
        val input = arguments?.get("input") as Scanner
        val output = arguments?.get("output") as PrintWriter

        if(validPwd(context, svmkEditText)) {
            output.println("MI_MINI_AUTH $key")
            var response = input.nextLine()
            if(response == "MI_ACK 00000000") {
                output.println("MI_SVC_START")
                response = input.nextLine()
                if(response == "MI_ACK 00000000") {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    fun showInvalidTokenDialog() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.invalidTokenDialog_title))
            .setMessage(getString(R.string.invalidTokenDialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .show()
    }
}
