package com.example.hsmassistantandroid.ui.Objetos

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.network.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody2
import com.example.hsmassistantandroid.data.ResponseBody7
import kotlinx.android.synthetic.main.fragment_objetos_list.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.security.cert.X509Certificate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.constraintlayout.solver.widgets.ConstraintWidget.VERTICAL
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.data.certificate
import com.example.hsmassistantandroid.extensions.alertAboutConnectionError
import com.example.hsmassistantandroid.extensions.ctx
import com.example.hsmassistantandroid.extensions.handleAPIError
import com.example.hsmassistantandroid.ui.mainFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_certificado.view.*
import java.text.SimpleDateFormat


private val TAG: String = ObjetosListFragment::class.java.simpleName

class ObjetosListFragment : mainFragment() {
    private lateinit var networkManager: NetworkManager
    private var tokenString: String? = null
    private lateinit var objetosStrings: Array<String>
    private var certificateCounter: Int = 0
    private var exportedCertificateCounter: Int = 0
    private var objectCounter: Int = 0
    private val certificateTypeInteger = 13
    private var certificateNameArray = ArrayList<certificate>()
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkManager = NetworkManager(context)
        setHasOptionsMenu(true)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
//        Log.d(TAG, BuildConfig.FINGER_PRINT)
    }

    fun expoRequest(objId: String) {
        val callbackList = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if(response?.isSuccessful!!) {
                    noContentLabel_objetos.visibility = View.GONE

                    val certificateData =
                        response?.body()?.bytes()
                    val cert = X509Certificate.getInstance(certificateData)

                    val certificateName = cert.subjectDN.name
                    val subjectName = certificateName.substringAfter("CN=").substringBefore(',')

                    val issuer = cert.issuerDN.name
                    val issuerName = issuer.substringAfter("CN=").substringBefore(',')

                    val beginDate = cert.notBefore
                    val finalDate = cert.notAfter

                    val myCert = certificate(subjectName, issuerName, beginDate, finalDate)
                    certificateNameArray.add(myCert)
                    exportedCertificateCounter += 1
                }
                else {
                    handleAPIError(this@ObjetosListFragment, response.errorBody())
                }

                if(certificateCounter == exportedCertificateCounter) {
                    if(exportedCertificateCounter == 0) { noContentLabel_objetos.visibility = View.VISIBLE }
                    else { noContentLabel_objetos.visibility = View.GONE }

                    objetosList.layoutManager = LinearLayoutManager(context)
                    viewAdapter = ObjetosListAdapter(certificateNameArray)
                    objetosList.adapter = viewAdapter
                    alreadyLoaded = true
                }
            }
        }
        networkManager.runObjExp(this@ObjetosListFragment, objId, tokenString!!, callbackList)

    }

    fun detailsRequest(objId: String) {
        val callbackList = object : Callback<ResponseBody7> {
            override fun onFailure(call: Call<ResponseBody7>?, t: Throwable?) {
                alertAboutConnectionError(view)
            }

            override fun onResponse(call: Call<ResponseBody7>?, response: Response<ResponseBody7>?) {
                if(response?.isSuccessful!!) {
                    if(response?.body()?.type == certificateTypeInteger) {
                        certificateCounter += 1
                        expoRequest(objId)
                    }
                }
                else {
                    handleAPIError(this@ObjetosListFragment, response.errorBody())
                }
            }
        }
        networkManager.runGetObjInfo(this@ObjetosListFragment, objId, tokenString!!, callbackList)

    }

    fun objetosRequest() {
        certificateCounter = 0
        exportedCertificateCounter = 0
        certificateNameArray = arrayListOf<certificate>()
        val callbackList = object : Callback<ResponseBody2> {
            override fun onFailure(call: Call<ResponseBody2>?, t: Throwable?) {
                alertAboutConnectionError(view)
                refresh_layout.isRefreshing = false
            }

            override fun onResponse(call: Call<ResponseBody2>?, response: Response<ResponseBody2>?) {
                if(response?.isSuccessful!!) {
                    objetosStrings = response?.body()?.obj!!.toTypedArray()
                    for(objId: String in objetosStrings) {
                        detailsRequest(objId)
                    }
                }
                else {
                    handleAPIError(this@ObjetosListFragment, response.errorBody())
                }
                refresh_layout.isRefreshing = false
            }
        }
        networkManager.runListObjects(this@ObjetosListFragment, tokenString!!, callbackList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_objetos_list, container, false)

        viewAdapter = ObjetosListAdapter(certificateNameArray)
        val recyclerView = view.findViewById<RecyclerView>(R.id.objetosList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = viewAdapter

        if(!alreadyLoaded) objetosRequest()

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

        val itemDecor = DividerItemDecoration(context, VERTICAL)
        objetosList.addItemDecoration(itemDecor)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reload, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_reload -> {
                //TODO: remover essas linhas
                val callbackClose = object : Callback<ResponseBody0> {
                    override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                        alertAboutConnectionError(view)
                        Snackbar.make(view!!, "sessao nao pode ser fechada", Snackbar.LENGTH_LONG).show()
                    }
                    override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                        if(response?.isSuccessful!!) {
//                            removeTokenFromSecureLocation(requireActivity())
                            Log.d(TAG, "sessao fechada")
                        }
                        else {
                            handleAPIError(this@ObjetosListFragment, response.errorBody())
                        }
                    }
                }
                networkManager.runClose(tokenString!!, callbackClose)
            }

            else -> Log.d(TAG, "Estranho isso, não existe outro")
        }
        return super.onOptionsItemSelected(item)
    }

    fun onOptionReloadClick() {
        objetosRequest()
    }
}

class ObjetosListAdapter(private val certArrayList: ArrayList<certificate>) : RecyclerView.Adapter<ObjetosListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_certificado, parent, false) //2
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                findNavController(v).navigate(R.id.action_objetosListFragment_to_objetoDetailFragment)
            }
        })

        holder.bindObjetos(certArrayList[position])
    }

    override fun getItemCount(): Int = certArrayList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindObjetos(myCert: certificate) {
            with(myCert) {
                itemView.certificateName.text = myCert.name
                itemView.emissor.text =  myCert.issuer

                val format = SimpleDateFormat("dd/MM/yyyy")
                itemView.fromDate.text = format.format(myCert.notBefore)
                itemView.toDate.text = format.format(myCert.notAfter)
                itemView.toLabel.text = "To:"
                itemView.fromLabel.text = "From:"
            }
        }
    }
}
