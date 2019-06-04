package com.example.hsmassistantandroid.activities.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog

import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody2
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.fragment_painel.*
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
 * [PainelFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PainelFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PainelFragment : Fragment() {
    private val networkManager = NetworkManager() // 1
    private var tokenString: String? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)

        tokenTextView.text = tokenString

        bottomAppBar.replaceMenu(R.menu.bottomappbar_menu)
        bottomAppBar.setNavigationOnClickListener {
            // do something interesting on navigation click
        }

        listObjsButton.setOnClickListener {
            val context = context
            val callbackList = object : Callback<ResponseBody2> {
                override fun onFailure(call: Call<ResponseBody2>?, t: Throwable?) {
                    Log.e("SecondsActivity", "Problem calling the API", t)
                }

                override fun onResponse(call: Call<ResponseBody2>?, response: Response<ResponseBody2>?) {
                    response?.isSuccessful.let {
                        Log.e("SecondActivity", "Deu certo "+tokenString)
                        val intent = Intent(context, ObjetosListActivity::class.java)
                        val objetosStringList = response?.body()?.obj
                        intent.putExtra("LIST", objetosStringList?.toTypedArray())
                        startActivity(intent)
                    }
                }
            }
            networkManager.runListObjetcs(tokenString!!, callbackList)
        }
        closeButton.setOnClickListener {
            val callbackClose = object : Callback<ResponseBody1> {
                override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
                    Log.e("SecondActivity", "Problem calling the API", t)
                }

                override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
                    response?.isSuccessful.let {

                        AlertDialog.Builder(context!!).setTitle("Sessão encerrada")
                            .setMessage("Sessão encerrada com sucesso")
                            .setPositiveButton(android.R.string.ok) { dialogInterface, i ->

                            }

                        Log.e("SecondActivity", "Deu certo ")
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            networkManager.runClose(tokenString!!, callbackClose)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.hsmassistantandroid.R.layout.fragment_painel, container, false)
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
         * @return A new instance of fragment PainelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PainelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
