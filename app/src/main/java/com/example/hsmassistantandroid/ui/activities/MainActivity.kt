package com.example.hsmassistantandroid.ui.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.preference.PreferenceManager
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hsmassistantandroid.data.ResponseBody3
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.fragments.LoginFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.contentView
import org.jetbrains.anko.toast

private val TAG: String = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
