package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody2
import com.example.hsmassistantandroid.data.ResponseBody7
import com.example.hsmassistantandroid.extensions.handleNetworkResponse
import kotlinx.android.synthetic.main.fragment_objetos_list.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.security.cert.X509Certificate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.constraintlayout.solver.widgets.ConstraintWidget.HORIZONTAL
import androidx.constraintlayout.solver.widgets.ConstraintWidget.VERTICAL
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.hsmassistantandroid.extensions.ctx
import kotlinx.android.synthetic.main.item_objetos.view.*

private val TAG: String = ObjetosListFragment::class.java.simpleName

class ObjetosListFragment : mainFragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    private lateinit var objetosStrings: Array<String>
    private var certificateCounter: Int = 0
    private var exportedCertificateCounter: Int = 0
    private val certificateTypeInteger = 13
    private var certificateNameArray = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
        objetosRequest()

    }

    fun expoRequest(objId: String) {
        val callbackList = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem EXPO", t)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val codeMeaning = handleNetworkResponse(response?.code(), context!!)
                Log.e("CODIGO", codeMeaning)
                response?.isSuccessful.let {
                    val certificateData =
                        response?.body()?.bytes()
                    val cert = X509Certificate.getInstance(certificateData)

                    val certificateName = cert.subjectDN.name
                    val result = certificateName.substringAfter("CN=").substringBefore(',')
                    certificateNameArray.add(result)
                    exportedCertificateCounter += 1
                }
                if(certificateCounter == exportedCertificateCounter) {
                    objetosList.layoutManager = LinearLayoutManager(context)
                    getActivity()?.runOnUiThread {
                        objetosList.adapter =
                            ObjetosListAdapter(certificateNameArray.toTypedArray())
                    }
                }
            }
        }
        networkManager.runObjExp(objId, tokenString!!, callbackList)

    }

    fun detailsRequest(objId: String) {
        val callbackList = object : Callback<ResponseBody7> {
            override fun onFailure(call: Call<ResponseBody7>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem INFO", t)
            }

            override fun onResponse(call: Call<ResponseBody7>?, response: Response<ResponseBody7>?) {
                response?.isSuccessful.let {
                    if(response?.body()?.type == certificateTypeInteger) {
                        certificateCounter += 1
                        expoRequest(objId)
                    }
                }
            }
        }
        networkManager.runGetObjInfo(objId, tokenString!!, callbackList)

    }

    fun objetosRequest() {
        certificateCounter = 0
        exportedCertificateCounter = 0
        certificateNameArray = arrayListOf<String>()
        val callbackList = object : Callback<ResponseBody2> {
            override fun onFailure(call: Call<ResponseBody2>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody2>?, response: Response<ResponseBody2>?) {
                response?.isSuccessful.let {
                    objetosStrings = response?.body()?.obj!!.toTypedArray()
                    for(objId: String in objetosStrings) {
                        detailsRequest(objId)
                    }
                }
            }
        }
        networkManager. runListObjetcs(tokenString!!, callbackList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_objetos_list, container, false)
        getActivity()?.runOnUiThread {
            val recyclerView = view.findViewById<RecyclerView>(R.id.objetosList).apply {
                adapter = ObjetosListAdapter(certificateNameArray.toTypedArray())
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()

    }

    fun setUpViews() {
        val itemDecor = DividerItemDecoration(context, VERTICAL)
        objetosList.addItemDecoration(itemDecor)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reload, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_reload -> onOptionReloadClick()
            else -> Log.d(TAG, "Estranho isso, não existe outro")
        }
        return super.onOptionsItemSelected(item)
    }

    fun onOptionReloadClick() {
        objetosRequest()
    }
}

class ObjetosListAdapter(private val itensStringList: Array<String>) : RecyclerView.Adapter<ObjetosListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_objetos, parent, false) //2
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d(TAG, "name:" + itensStringList[position])
            }
        })

        holder.bindObjetos(itensStringList[position])
    }

    override fun getItemCount(): Int = itensStringList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindObjetos(objeto: String) {
            with(objeto) {
                itemView.title_label.text = objeto
            }
        }
    }
}
