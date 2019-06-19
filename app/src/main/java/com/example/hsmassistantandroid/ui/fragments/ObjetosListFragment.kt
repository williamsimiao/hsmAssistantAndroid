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
import com.example.hsmassistantandroid.ui.adapters.ObjetosListAdapter
import kotlinx.android.synthetic.main.fragment_objetos_list.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.security.cert.X509Certificate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.constraintlayout.solver.widgets.ConstraintWidget.HORIZONTAL
import androidx.constraintlayout.solver.widgets.ConstraintWidget.VERTICAL

private val TAG: String = ObjetosListFragment::class.java.simpleName

class ObjetosListFragment : Fragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    private lateinit var objetosStrings: Array<String>
    private var certificateCounter: Int = 0
    private var exportedCertificateCounter: Int = 0
    private val certificateTypeInteger = 13
    private var certificateNameArray = arrayListOf<String>()
    private var listener: OnFragmentInteractionListener? = null

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
                val codeMeaning = handleNetworkResponse(response?.code())
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_objetos_list, container, false)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(): ObjetosListFragment = ObjetosListFragment()
    }
}
