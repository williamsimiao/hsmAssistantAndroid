package com.example.hsmassistantandroid.ui.Relações

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.constraintlayout.solver.widgets.ConstraintWidget
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody5
import com.example.hsmassistantandroid.extensions.alertAboutConnectionError
import com.example.hsmassistantandroid.extensions.ctx
import com.example.hsmassistantandroid.extensions.handleAPIError
import com.example.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_objetos.view.*
import kotlinx.android.synthetic.main.trust_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = TrustFragment::class.java.simpleName

class TrustFragment: mainFragment() {
    private val networkManager = NetworkManager(context)
    private var tokenString: String? = null
    private var usrNameAndAclArray: ArrayList<Pair<String, Int>> = ArrayList()
    var isTrustees: Boolean? = null
    private lateinit var viewAdapter: RecyclerView.Adapter<*>


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
                alertAboutConnectionError(view)
            }

            override fun onResponse(call: Call<ResponseBody5>?, response: Response<ResponseBody5>?) {
                if(response?.isSuccessful!!) {
                    val result = response?.body()?.trust!!
                    usrNameAndAclArray.clear()
                    for(item in result) {
                        usrNameAndAclArray.add(Pair(item.usr, item.acl))
                    }

                    if(usrNameAndAclArray.count() == 0) {
                        noContentLabel_trust.visibility = View.VISIBLE
                        if(isTrustees!!) { noContentLabel_trust.text = getString(R.string.noContentText_trustees) }
                        else { noContentLabel_trust.text = getString(R.string.noContentText_trusters) }
                    }
                    else { noContentLabel_trust.visibility = View.GONE }


                    trustList.layoutManager = LinearLayoutManager(context)
                    viewAdapter = TrustListAdapter(usrNameAndAclArray)
                    trustList.adapter = viewAdapter
                    alreadyLoaded = true
                }
                else {
                    val message = handleAPIError(this@TrustFragment, response.errorBody())
                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()

                }
            }
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userName = sharedPreferences.getString("USER", null)
        val op = if(isTrustees!!) 2 else 1
        networkManager.runListUsrsTrust(tokenString!!, op, userName, callbackList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.trust_fragment, container, false)

        viewAdapter = TrustListAdapter(usrNameAndAclArray)
        val recyclerView = view.findViewById<RecyclerView>(R.id.trustList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = viewAdapter

        if(!alreadyLoaded) listTrustRequest()

        return view
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
        listTrustRequest()
    }

    fun onOptionAddUserClick() {

        findNavController().navigate(R.id.action_relacaoFragment_to_userSelectionFragment)
    }

}

class TrustListAdapter(private val itensArrayList: ArrayList<Pair<String, Int>>) : RecyclerView.Adapter<TrustListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_objetos, parent, false) //2
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val bundle = bundleOf(
                    USERNAME_KEY to itensArrayList[position].first,
                    ACL_KEY to itensArrayList[position].second)

                holder.itemView.findNavController().navigate(
                    R.id.action_relacaoFragment_to_newPermissionFragment,
                    bundle)
            }
        })

        holder.bindUsuario(itensArrayList[position].first)
    }

    override fun getItemCount(): Int = itensArrayList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindUsuario(objeto: String) {
            with(objeto) {
                itemView.title_label.text = objeto
            }
        }
    }

    companion object {
        const val USERNAME_KEY = "userName"
        const val ACL_KEY = "userAcl"
    }
}