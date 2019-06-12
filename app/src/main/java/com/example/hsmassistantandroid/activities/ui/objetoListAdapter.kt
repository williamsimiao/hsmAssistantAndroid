package com.example.hsmassistantandroid.activities.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.data.ResponseBody2
import com.example.hsmassistantandroid.extensions.ctx
import kotlinx.android.synthetic.main.item_objetos.view.*
import android.widget.Toast
import android.util.Log
import org.jetbrains.anko.toast


class ObjetosListAdapter(private val objetosStringList: Array<String>) : RecyclerView.Adapter<ObjetosListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_objetos, parent, false) //2
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d("TAG-94", "item x")
            }
        })

        holder.bindRepo(objetosStringList[position])
    }

    override fun getItemCount(): Int = objetosStringList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindRepo(objeto: String) {
            with(objeto) {
                itemView.title_label.text = objeto
            }
        }
    }
}
