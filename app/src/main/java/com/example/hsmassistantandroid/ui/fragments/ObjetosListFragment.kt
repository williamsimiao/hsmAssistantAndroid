package com.example.hsmassistantandroid.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.io.InputStream
import javax.security.cert.X509Certificate



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ObjetosListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ObjetosListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
        objetosRequest()
    }

    fun expoRequest(objId: String) {
        val callbackList = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem EXPO", t)
//                Log.e("CALL", call.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val codeMeaning = handleNetworkResponse(response?.code())
                Log.e("CODIGO", codeMeaning)
                response?.isSuccessful.let {
                    val certificateData = response?.body()
//                    val cert = X509Certificate.getInstance(certificateData)
//                    val certificateName = cert.subjectDN.name
//                    certificateNameArray.add(certificateName)
                    exportedCertificateCounter += 1
                }
                if(certificateCounter == exportedCertificateCounter) {
                    objetosList.layoutManager = LinearLayoutManager(context)
                    getActivity()?.runOnUiThread {
                        objetosList.adapter = ObjetosListAdapter(certificateNameArray.toTypedArray())
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

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ObjetosListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ObjetosListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
