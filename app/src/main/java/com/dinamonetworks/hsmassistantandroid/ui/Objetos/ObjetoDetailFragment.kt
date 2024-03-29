package com.dinamonetworks.hsmassistantandroid.ui.Objetos

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import com.dinamonetworks.hsmassistantandroid.R
import com.dinamonetworks.hsmassistantandroid.network.NetworkManager
import com.dinamonetworks.hsmassistantandroid.data.ResponseBody4
import com.dinamonetworks.hsmassistantandroid.extensions.alertAboutConnectionError
import com.dinamonetworks.hsmassistantandroid.extensions.handleAPIError
import com.dinamonetworks.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = ObjetoDetailFragment::class.java.simpleName

class ObjetoDetailFragment : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null
    private var usrNamesStrings = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(true)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
    }

    fun listUsrsRequest() {
        val callbackList = object : Callback<ResponseBody4> {
            override fun onFailure(call: Call<ResponseBody4>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }

            override fun onResponse(call: Call<ResponseBody4>?, response: Response<ResponseBody4>?) {
                if(response!!.isSuccessful) {
                    usrNamesStrings = ArrayList(response.body()!!.usr)
                }
                else {
                    val message = handleAPIError(this@ObjetoDetailFragment, response.errorBody())
                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()
                }
            }
        }
        networkManager.runListUsrs(this@ObjetoDetailFragment, tokenString!!, callbackList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_objeto_detail, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {

    }
}
