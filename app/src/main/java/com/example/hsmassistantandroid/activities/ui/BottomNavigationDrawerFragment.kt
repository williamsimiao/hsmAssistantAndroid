package com.example.hsmassistantandroid.activities.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody2
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
                    context!!.toast("DOIDERA 1")
                    didTaplistObjsButton()
                }
                R.id.drawer_item_relacao -> context!!.toast("DOIDERA 2")
                R.id.drawer_item_gestao -> context!!.toast("DOIDERA 3")

            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            true
        }

    }

    fun didTaplistObjsButton() {
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

    fun didTapcloseButton() {
        val callbackClose = object : Callback<ResponseBody1> {
            override fun onFailure(call: Call<ResponseBody1>?, t: Throwable?) {
                Log.e("SecondActivity", "Problem calling the API", t)
            }
            override fun onResponse(call: Call<ResponseBody1>?, response: Response<ResponseBody1>?) {
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