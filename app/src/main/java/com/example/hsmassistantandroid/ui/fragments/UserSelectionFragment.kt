package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.net.Uri
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
import com.example.hsmassistantandroid.data.ResponseBody4
import com.example.hsmassistantandroid.ui.adapters.ObjetosListAdapter
import kotlinx.android.synthetic.main.fragment_user_selection.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = UserSelectionFragment::class.java.simpleName

class UserSelectionFragment : Fragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    private lateinit var usrNamesStrings: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
        listUsrsRequest()

    }

    fun listUsrsRequest() {
        val callbackList = object : Callback<ResponseBody4> {
            override fun onFailure(call: Call<ResponseBody4>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody4>?, response: Response<ResponseBody4>?) {
                response?.isSuccessful.let {
                    usrNamesStrings = response?.body()?.usr!!.toTypedArray()
                    userSelectionList.layoutManager = LinearLayoutManager(context)
                    getActivity()?.runOnUiThread {
                        userSelectionList.adapter =
                            ObjetosListAdapter(usrNamesStrings)
                    }
                }
            }
        }
        networkManager.runListUsrs(tokenString!!, callbackList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        val itemDecor = DividerItemDecoration(context, ConstraintWidget.VERTICAL)
        userSelectionList.addItemDecoration(itemDecor)
    }

    companion object {
        fun newInstance(): UserSelectionFragment = UserSelectionFragment()
    }
}
