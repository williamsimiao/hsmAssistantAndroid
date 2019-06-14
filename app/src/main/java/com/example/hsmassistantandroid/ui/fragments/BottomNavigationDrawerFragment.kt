package com.example.hsmassistantandroid.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.ui.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_navigation_drawer.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomNavigationDrawerFragment: BottomSheetDialogFragment() {
    private val networkManager = NetworkManager() // 1
    private var tokenString: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        tokenString = sharedPreferences.getString("TOKEN", null)

        return inflater.inflate(R.layout.fragment_bottomsheet , container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            // Bottom Navigation Drawer menu item clicks
            when (menuItem.itemId) {
                R.id.drawer_item_obejtos -> {
                    findNavController().navigate(R.id.action_painelFragment_to_objetosListFragment)
                }
                R.id.drawer_item_relacao -> {
                    context!!.toast("Relacao")
                }
                R.id.drawer_item_gestao -> {
                    findNavController().navigate(R.id.action_painelFragment_to_gestaoUsuarioFragment)
                }

            }
            true
        }

    }


    fun didTapcloseButton() {
        val callbackClose = object : Callback<ResponseBody0> {
            override fun onFailure(call: Call<ResponseBody0>?, t: Throwable?) {
                Log.e("SecondActivity", "Problem calling the API", t)
            }
            override fun onResponse(call: Call<ResponseBody0>?, response: Response<ResponseBody0>?) {
                response?.isSuccessful.let {

                    AlertDialog.Builder(activity!!.baseContext).setTitle("Sessão encerrada")
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