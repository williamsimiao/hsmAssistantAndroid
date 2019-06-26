package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody4
import com.example.hsmassistantandroid.extensions.ctx
import com.example.hsmassistantandroid.extensions.handleAPIError
import kotlinx.android.synthetic.main.fragment_user_selection.*
import kotlinx.android.synthetic.main.item_objetos.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = UserSelectionFragment::class.java.simpleName

class UserSelectionFragment : mainFragment() {
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
                if(response?.isSuccessful!!) {                    usrNamesStrings = response?.body()?.usr!!.toTypedArray()
                    userSelectionList.layoutManager = LinearLayoutManager(context)
                    getActivity()?.runOnUiThread {
                        userSelectionList.adapter =
                            SelectionListAdapter(usrNamesStrings)
                    }
                }
                else {
                    handleAPIError(context, response.errorBody())
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
}

class SelectionListAdapter(private val itensStringList: Array<String>) : RecyclerView.Adapter<SelectionListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_objetos, parent, false) //2
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val bundle = bundleOf(TrustListAdapter.USERNAME_KEY to itensStringList[position])
                holder.itemView.findNavController().navigate(R.id.action_userSelectionFragment_to_newPermissionFragment2, bundle)
            }
        })

        holder.bindUsuario(itensStringList[position])
    }

    override fun getItemCount(): Int = itensStringList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindUsuario(objeto: String) {
            with(objeto) {
                itemView.title_label.text = objeto
            }
        }
    }
}