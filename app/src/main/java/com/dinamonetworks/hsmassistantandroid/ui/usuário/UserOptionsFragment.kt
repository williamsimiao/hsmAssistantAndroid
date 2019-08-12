package com.dinamonetworks.hsmassistantandroid.ui.usuário

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dinamonetworks.hsmassistantandroid.R
import com.dinamonetworks.hsmassistantandroid.network.NetworkManager
import com.dinamonetworks.hsmassistantandroid.data.ResponseBody0
import com.dinamonetworks.hsmassistantandroid.extensions.*
import com.dinamonetworks.hsmassistantandroid.network.MIHelper
import com.dinamonetworks.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_user_options.*
import kotlinx.android.synthetic.main.settings_card.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = UserOptions::class.java.simpleName

class UserOptions : mainFragment(), RecyclerViewClickListener {

    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

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
        val view = inflater.inflate(R.layout.fragment_user_options, container, false)

        setUpRecyclerView(view)

        return view
    }

    fun setUpRecyclerView(view: View) {

        val settings = arrayListOf<String>()
        settings.add(getString(R.string.usr_options_change_hsm))
        settings.add(getString(R.string.usr_options_hsm_menu))
        settings.add(getString(R.string.change_pwd))
        settings.add(getString(R.string.fechar_sessao))

        viewAdapter = SettingsListAdapter(this, settings)
        val recyclerView = view.findViewById<RecyclerView>(R.id.settingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = viewAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        val buttonText = view.item_title.text
        when(buttonText) {
            view.context.getString(R.string.usr_options_change_hsm) -> goToSetUpScreen(requireActivity())
            view.context.getString(R.string.usr_options_hsm_menu) -> didTapHsmOptions()
            view.context.getString(R.string.fechar_sessao) -> didTapcloseButton()
            view.context.getString(R.string.change_pwd) -> findNavController().navigate(R.id.action_userOptions_to_changePwdFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val usrName = sharedPreferences.getString("USER", null)
        useNameLabel.text = usrName

        super.onViewCreated(view, savedInstanceState)
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

//    fun showAutenticationDialog() {
//        val dialog = Dialog(activity)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(true)
//        dialog.setContentView(R.layout.dialog_svmk_input)
//
//        val title = dialog.findViewById(R.id.tvTitle) as TextView
//        title.text = "Um titulo muito legal"
//
//        val textLayout = dialog.findViewById(R.id.tvTitle) as TextView
//
//        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
//        yesBtn.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//
//        try {
//        val alert = AlertDialog.Builder(requireContext())
//        var editTextAge: EditText? = null
//
//        // Builder
//        with (alert) {
//            setTitle("Informe a chave de ativação do HSM")
////            setMessage("Enter your Age Here!!")
//
//            // Add any  input field here
//            editTextAge = EditText(context)
//            editTextAge!!.hint="12345678"
//            editTextAge!!.inputType = InputType.TYPE_NUMBER_VARIATION_PASSWORD
//            editTextAge!!.highlightColor = ResourcesCompat.getColor(getResources(), R.color.black, null)
//
//            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//            param.setMargins(20, 8, 20, 8)
//            editTextAge!!.layoutParams = param
//
//            setPositiveButton("OK") {
//                    dialog, whichButton ->
//                val hsm_key = editTextAge!!.text.toString()
//
//                if(validPwd(context, editTextAge!!)) {
//
//                    val successCallback = {
//                        requireActivity().runOnUiThread {
//                            onAuthenticationCompleted()
//                        }
//                    }
//
//                    val errorCallback = { errorMessage: String ->
//                        Log.d(TAG, errorMessage)
//                        Unit
//                    }
//
//                    MIHelper.autenticateWithKey(hsm_key, successCallback, errorCallback)
//                    dialog.dismiss()
//                }
//            }
//
//            setNegativeButton("Cancelar") {
//                    dialog, whichButton ->
//                dialog.dismiss()
//            }
//        }
//
//        // Dialog
//        val dialog = alert.create()
//        dialog.setView(editTextAge)
//        dialog.show()
//    }

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


class SettingsListAdapter(private val listener: RecyclerViewClickListener, private val settingsArrayList: ArrayList<String>) : RecyclerView.Adapter<SettingsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.settings_card, parent, false) //2
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                listener.onItemClick(v, position)
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

interface RecyclerViewClickListener {
    fun onItemClick(view: View, position: Int)
}