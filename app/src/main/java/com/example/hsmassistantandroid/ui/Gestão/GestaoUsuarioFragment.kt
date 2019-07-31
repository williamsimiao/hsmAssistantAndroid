package com.example.hsmassistantandroid.ui.Gestão

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
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody4
import com.example.hsmassistantandroid.extensions.alertAboutConnectionError
import com.example.hsmassistantandroid.extensions.ctx
import com.example.hsmassistantandroid.extensions.handleAPIError
import com.example.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import kotlinx.android.synthetic.main.item_objetos.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = gestaoUsuarioFragment::class.java.simpleName

class gestaoUsuarioFragment : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var usrNamesStrings = ArrayList<String>()
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(true)
    }

    fun listUsrsRequest() {
        val callbackList = object : Callback<ResponseBody4> {
            override fun onFailure(call: Call<ResponseBody4>?, t: Throwable?) {
                alertAboutConnectionError(view)
                refresh_layout.isRefreshing = false

            }

            override fun onResponse(call: Call<ResponseBody4>?, response: Response<ResponseBody4>?) {
                if(response!!.isSuccessful) {
                    usrNamesStrings = ArrayList(response.body()!!.usr)
                    //TODO: tirar essa linha
                    usrNamesStrings.addAll(arrayListOf<String>("pocket_example_1", "pocket_example_2", "pocket_example_3", "pocket_example_4", "pocket_example_5", "pocket_example_6", "pocket_example_7", "pocket_example_7", "pocket_example_7", "pocket_example_7", "pocket_example_7", "pocket_example_7" ))
                    gestaousuarioList.layoutManager = LinearLayoutManager(context)
                    viewAdapter = GestaoListAdapter(usrNamesStrings)
                    gestaousuarioList.adapter = viewAdapter
                    alreadyLoaded = true
                }
                else {
                    val message = handleAPIError(this@gestaoUsuarioFragment, response.errorBody())
                    Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG).show()
                }
                refresh_layout.isRefreshing = false

            }
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val tokenString = sharedPreferences.getString("TOKEN", null)
        networkManager.runListUsrs(tokenString!!, callbackList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_gestao_usuario_list, container, false)

        viewAdapter = GestaoListAdapter(usrNamesStrings)
        val recyclerView = view.findViewById<RecyclerView>(R.id.gestaousuarioList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = viewAdapter

        if(!alreadyLoaded) listUsrsRequest()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        refresh_layout.setOnRefreshListener {
            onOptionReloadClick()
        }
        val itemDecor = DividerItemDecoration(context, ConstraintWidget.VERTICAL)
        gestaousuarioList.addItemDecoration(itemDecor)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_user_and_reload, menu)

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
        listUsrsRequest()
    }

    fun onOptionAddUserClick() {
        findNavController().navigate(R.id.action_gestaoUsuarioFragment2_to_newUserFragment)
    }
}

class GestaoListAdapter(private val itensStringList: ArrayList<String>) : RecyclerView.Adapter<GestaoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_objetos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.itemView.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View) {
//                Log.d(TAG, "name:" + itensStringList[position])
//            }
//        })
        holder.bindUsuario(itensStringList[position])
    }

    override fun getItemCount(): Int = itensStringList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindUsuario(objeto: String) {
            itemView.title_label.text = objeto
        }
    }
}

