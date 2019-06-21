package com.example.hsmassistantandroid.ui.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody5
import com.example.hsmassistantandroid.ui.adapters.ObjetosListAdapter
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import kotlinx.android.synthetic.main.trust_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = TrustFragment::class.java.simpleName

class TrustFragment: Fragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    private var usrNamesStrings: ArrayList<String> = ArrayList()

    private var listener: gestaoUsuarioFragment.OnFragmentInteractionListener? = null

    var isTrustees: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
        listTrustRequest()

    }

    fun listTrustRequest() {
        val callbackList = object : Callback<ResponseBody5> {
            override fun onFailure(call: Call<ResponseBody5>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody5>?, response: Response<ResponseBody5>?) {
                response?.isSuccessful.let {
                    val result = response?.body()?.trust!!
                    for(item in result) {
                        usrNamesStrings.add(item.usr)
                    }
                    trustList.layoutManager = LinearLayoutManager(context)
                    getActivity()?.runOnUiThread {
                        trustList.adapter =
                            ObjetosListAdapter(usrNamesStrings.toTypedArray())
                    }
                }
            }
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userName = sharedPreferences.getString("USER", null)
        val op = if(isTrustees!!) 2 else 1
        networkManager.runListUsrsTrust(tokenString!!, op, userName, callbackList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.trust_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        val itemDecor = DividerItemDecoration(context, ConstraintWidget.VERTICAL)
        trustList.addItemDecoration(itemDecor)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(isTrustees!!) {
            inflater.inflate(R.menu.add_user_and_reload, menu)
        }
        else {
            inflater.inflate(R.menu.reload, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_add_user -> onOptionAddUserClick()

            R.id.options_reload -> onOptionReloadClick()

            else -> Log.d(TAG, "Estranho isso, não existe outro")
        }
        return super.onOptionsItemSelected(item)
    }

    fun onOptionReloadClick() {

    }

    fun onOptionAddUserClick() {

    }

}