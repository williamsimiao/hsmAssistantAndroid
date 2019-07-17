package com.example.hsmassistantandroid.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.hsmassistantandroid.R

enum class Model private constructor(val titleResId: Int, val layoutResId: Int) {
    FIRST(R.string.setUpFirstTitle, R.layout.fragment_wellcome),
    SECOND(R.string.setupSecondTitle, R.layout.fragment_discovery),
}

class setupPagerAdapter(private val mContext: Context) : PagerAdapter() {
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val modelObject = Model.values()[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(modelObject.layoutResId, collection, false) as ViewGroup
        collection.addView(layout)
        return layout
    }
    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }
    override fun getCount(): Int {
        return Model.values().size
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
    override fun getPageTitle(position: Int): CharSequence {
        val customPagerEnum = Model.values()[position]
        return mContext.getString(customPagerEnum.titleResId)
    }
}
