package com.example.hsmassistantandroid.ui.Gestão

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.navigation.fragment.findNavController
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.extensions.*
import com.example.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_new_user.*
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = gestaoUsuarioFragment::class.java.simpleName
private val newUserDefaultACL = 80

class NewUserFragment : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)

    }

    fun createUserRequest() {
        val callbackList = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if(response!!.isSuccessful) {
                    hideSoftKeyboard(requireActivity())
                    context!!.toast(getString(R.string.userCreated_toast))
                    findNavController().navigate(R.id.action_newUserFragment_to_gestaoUsuarioFragment2)
                }
                else {
                    val message = handleAPIError(this@NewUserFragment, response.errorBody())
                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        }
        val newUserName = newUsrEditText.editText!!.text.toString()
        val newPassword = newUsrPwdEditText.editText!!.text.toString()
        networkManager.runCreateUsr(tokenString!!, newUserName, newPassword,
            newUserDefaultACL, callbackList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_new_user, container, false)
        view.setOnClickListener {
            hideSoftKeyboard(requireActivity())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun didtapCreateUser() {
        if(fieldsAreValid(context, arrayOf(newUsrEditText, newUsrPwdEditText,
                newUsrPwdRepeatEditText)) == false) {
            return
        }

        if(validUsr(context, newUsrEditText))

        if(validPwd(context, newUsrPwdEditText) ==  false) return

        if(validPwdConfirmation(context, newUsrPwdEditText.editText!!.text.toString(),
                newUsrPwdRepeatEditText) == false) {
            return
        }
        createUserRequest()
    }

    fun setUpViews() {
        newUsrEditText.editText!!.onChange { newUsrEditText.error = null }
        newUsrPwdEditText.editText!!.onChange { newUsrPwdEditText.error = null }
        newUsrPwdRepeatEditText.editText!!.onChange { newUsrPwdRepeatEditText.error = null }

        val fieldUsr = newUsrEditText.editText
        fieldUsr!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                validUsr(context, newUsrEditText)
            }
        }

        val fieldNewPwd = newUsrPwdEditText.editText
        fieldNewPwd!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                validPwd(context, newUsrPwdEditText)
            }
        }

        val fieldNewPwdConfirmation =  newUsrPwdRepeatEditText.editText
        fieldNewPwdConfirmation!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                validPwdConfirmation(context, newUsrPwdEditText.editText!!.text.toString(),
                    newUsrPwdRepeatEditText)
            }
        }


        createUserButton.setOnClickListener { didtapCreateUser() }
    }
}