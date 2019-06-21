package com.example.hsmassistantandroid.extensions

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(getAdapterPosition(), getItemViewType())
    }
    return this
}

val View.ctx: Context
    get() = context