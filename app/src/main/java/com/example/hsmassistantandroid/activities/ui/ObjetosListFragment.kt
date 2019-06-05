package com.example.hsmassistantandroid.activities.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody2
import kotlinx.android.synthetic.main.activity_objetos_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        objetosRequest()


    }

    fun objetosRequest() {
        val callbackList = object : Callback<ResponseBody2> {
            override fun onFailure(call: Call<ResponseBody2>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody2>?, response: Response<ResponseBody2>?) {
                response?.isSuccessful.let {
                    Log.e("SecondActivity", "Deu certo ")
                    objetosStrings = response?.body()?.obj
                    val viewManager = LinearLayoutManager(context)
                    objetosList.layoutManager = LinearLayoutManager(context)
                    getActivity()?.runOnUiThread {
                        objetosList.adapter = ObjetosListAdapter(objetosStrings)
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
