package com.example.hsmassistantandroid.activities.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.example.hsmassistantandroid.R
import kotlinx.android.synthetic.main.activity_objetos_list.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ObjetosListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var objetosStrings: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objetos_list)
        objetosStrings = intent.getStringArrayExtra("LIST")
        viewManager = LinearLayoutManager(this)
        objetosList.layoutManager = LinearLayoutManager(this)
        runOnUiThread {
            objetosList.adapter = ObjetosListAdapter(objetosStrings)
        }


//
//        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
//            // use this setting to improve performance if you know that changes
//            // in content do not change the layout size of the RecyclerView
//            setHasFixedSize(true)
//
//            // use a linear layout manager
//            layoutManager = viewManager
//
//            // specify an viewAdapter (see also next example)
//            adapter = viewAdapter
//
//        }

    }


}
