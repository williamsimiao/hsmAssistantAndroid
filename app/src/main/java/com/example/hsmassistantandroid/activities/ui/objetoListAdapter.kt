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

class ObjetosListAdapter(private val objetosStringList: Array<String>) : RecyclerView.Adapter<ObjetosListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.item_objetos, parent, false) //2
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindRepo(objetosStringList[position]) //3
    }

    override fun getItemCount(): Int = objetosStringList.size //4

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindRepo(objeto: String) { //5
            with(objeto) {
                itemView.identificador.text = objeto
            }
        }
    }
}
