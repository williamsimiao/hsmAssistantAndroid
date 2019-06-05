package com.example.hsmassistantandroid.activities.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.data.ResponseBody2
import kotlinx.android.synthetic.main.activity_objetos_list.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ObjetosListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var objetosStrings: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objetos_list)

    }

    fun objetosRequest() {
        val callbackList = object : Callback<ResponseBody2> {
            override fun onFailure(call: Call<ResponseBody2>?, t: Throwable?) {
                Log.e("SecondsActivity", "Problem calling the API", t)
            }

            override fun onResponse(call: Call<ResponseBody2>?, response: Response<ResponseBody2>?) {
                response?.isSuccessful.let {
                    Log.e("SecondActivity", "Deu certo ")
                    objetosStrings = intent.getStringArrayExtra("LIST")
                    viewManager = LinearLayoutManager(baseContext)
                    objetosList.layoutManager = LinearLayoutManager(baseContext)
                    runOnUiThread {
                        objetosList.adapter = ObjetosListAdapter(objetosStrings)
                    }
                }
            }
        }
//        networkManager.runListObjetcs(tokenString!!, callbackList)
    }


}
