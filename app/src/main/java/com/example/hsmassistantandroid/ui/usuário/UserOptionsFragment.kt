package com.example.hsmassistantandroid.ui.usuário

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.data.certificate
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.network.MIHelper
import com.example.hsmassistantandroid.ui.activities.DeviceSelectionActivity
import com.example.hsmassistantandroid.ui.activities.SvmkActivity
import com.example.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_svmk.*
import kotlinx.android.synthetic.main.fragment_user_options.*
import kotlinx.android.synthetic.main.item_certificado.view.*
import kotlinx.android.synthetic.main.settings_card.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

private val TAG: String = UserOptions::class.java.simpleName

class UserOptions : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
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
        connectNewHSM.setOnClickListener { didTapConnectNewHSM() }
        hsmOptions.setOnClickListener { didTapHsmOptions() }
        closeButton.setOnClickListener { didTapcloseButton() }
        changePwdButton.setOnClickListener {
            findNavController().navigate(R.id.action_userOptions_to_changePwdFragment)
        }
    }

    fun didTapcloseButton() {
        val callbackClose = object : Callback<ResponseBody0> {
            override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }
            override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                if(response?.isSuccessful!!) {
                    removeTokenFromSecureLocation(requireActivity())
                    goToLoginScreen(this@UserOptions)
                }
                else {
                    val message = handleAPIError(this@UserOptions, response.errorBody())
                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()
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

    fun didTapHsmOptions() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val url = sharedPreferences.getString("BASE_URL", null)
        prepareConnection(url)
    }

    @SuppressLint("ApplySharedPref")
    fun didTapConnectNewHSM() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreferences.edit()
        editor.remove("BASE_URL")
        editor.commit()

        val intent = Intent(context, DeviceSelectionActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    fun showAutenticationDialog() {
        val alert = AlertDialog.Builder(requireContext())
        var editTextAge:EditText? = null

        // Builder
        with (alert) {
            setTitle("Informe a chave de ativação do HSM")
//            setMessage("Enter your Age Here!!")

            // Add any  input field here
            editTextAge = EditText(context)
            editTextAge!!.hint="12345678"
            editTextAge!!.inputType = InputType.TYPE_CLASS_NUMBER

            setPositiveButton("OK") {
                    dialog, whichButton ->
                val hsm_key = editTextAge!!.text.toString()

                if(validPwd(context, editTextAge!!)) {

                    val successCallback = {
                        requireActivity().runOnUiThread {
                            onAuthenticationCompleted()
                        }
                    }

                    val errorCallback = { errorMessage: String ->
                        Log.d(TAG, errorMessage)
                        Unit
                    }

                    MIHelper.autenticateWithKey(hsm_key, successCallback, errorCallback)
                    dialog.dismiss()
                }


            }

            setNegativeButton("Cancelar") {
                    dialog, whichButton ->
                dialog.dismiss()
            }
        }

        // Dialog
        val dialog = alert.create()
        dialog.setView(editTextAge)
        dialog.show()
    }

    fun onAuthenticationCompleted() {
        findNavController().navigate(R.id.action_userOptions_to_hsmOptions)
    }

    fun prepareConnection(address: String) {
        //TODO: validate address

        val successCallback = {
            requireActivity().runOnUiThread {
                showAutenticationDialog()
            }
        }

        val errorCallback = { errorMessage: String ->
            requireActivity().runOnUiThread {
                Toast.makeText(context, "Erro ao conectar-se", Toast.LENGTH_SHORT).show()
                Log.d(TAG, errorMessage)
            }
        }

        MIHelper.connectToAddress(address, requireContext(), successCallback, errorCallback)
    }
}

class SettingsListAdapter(private val settingsArrayList: ArrayList<String>) : RecyclerView.Adapter<SettingsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.settings_card, parent, false) //2
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Navigation.findNavController(v).navigate(R.id.action_objetosListFragment_to_objetoDetailFragment)
            }
        })

        holder.bindObjetos(settingsArrayList[position])
    }

    override fun getItemCount(): Int = settingsArrayList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindObjetos(settingTitle: String) {
            itemView.item_title.text = settingTitle
        }
    }
}

