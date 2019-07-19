package com.example.hsmassistantandroid.ui.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody4
import com.example.hsmassistantandroid.extensions.alertAboutConnectionError
import com.example.hsmassistantandroid.extensions.handleAPIError
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val TAG: String = gestaoUsuarioFragment::class.java.simpleName

class ObjetoDetailFragment : mainFragment() {
    private val networkManager = NetworkManager(context)
    private var tokenString: String? = null
    private var usrNamesStrings = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        networkManager.runListUsrs(tokenString!!, callbackList)
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
