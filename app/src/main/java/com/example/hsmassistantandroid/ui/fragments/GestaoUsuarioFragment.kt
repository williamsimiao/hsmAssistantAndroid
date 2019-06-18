//package com.example.hsmassistantandroid.activities.ui
//
//class gestaoUsuarioFragment {
//}

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
import com.example.hsmassistantandroid.ui.activities.SecondActivity
import com.example.hsmassistantandroid.ui.adapters.ObjetosListAdapter
import kotlinx.android.synthetic.main.fragment_gestao_usuario_list.*
import kotlinx.android.synthetic.main.fragment_objetos_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG: String = gestaoUsuarioFragment::class.java.simpleName

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ObjetosListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ObjetosListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class gestaoUsuarioFragment : Fragment() {
    private val networkManager = NetworkManager()
    private var tokenString: String? = null
    private lateinit var usrNamesStrings: Array<String>

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)
        listUsrsRequest()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    fun listUsrsRequest() {
        val callbackList = object : Callback<ResponseBody4> {
            override fun onFailure(call: Call<ResponseBody4>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody4>?, response: Response<ResponseBody4>?) {
                response?.isSuccessful.let {
                    usrNamesStrings = response?.body()?.usr!!.toTypedArray()
                    gestaousuarioList.layoutManager = LinearLayoutManager(context)
                    getActivity()?.runOnUiThread {
                        gestaousuarioList.adapter =
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
        return inflater.inflate(R.layout.fragment_gestao_usuario_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    fun setUpViews() {
        val itemDecor = DividerItemDecoration(context, ConstraintWidget.VERTICAL)
        gestaousuarioList.addItemDecoration(itemDecor)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gestao, menu)

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

    }

    fun onOptionAddUserClick() {

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
        fun newInstance(): gestaoUsuarioFragment = gestaoUsuarioFragment()
    }
}
