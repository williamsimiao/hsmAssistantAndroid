package com.example.hsmassistantandroid.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.extensions.ctx
import kotlinx.android.synthetic.main.item_objetos.view.*
import android.util.Log


class ObjetosListAdapter(private val itensStringList: Array<String>) : RecyclerView.Adapter<ObjetosListAdapter.ViewHolder>() {
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

        holder.bindRepo(itensStringList[position])
    }

    override fun getItemCount(): Int = itensStringList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindRepo(objeto: String) {
            with(objeto) {
                itemView.title_label.text = objeto
            }
        }
    }
}
