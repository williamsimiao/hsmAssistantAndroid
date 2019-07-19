package com.example.hsmassistantandroid.ui.Relações

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.ui.adapters.myPagerAdapter
import kotlinx.android.synthetic.main.relacao_fragment.*

private val TAG: String = relacaoFragment::class.java.simpleName

class relacaoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.relacao_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewPager()
    }

    private fun setupViewPager() {

        val adapter = myPagerAdapter(childFragmentManager)

        var trustees = TrustFragment()
        trustees.isTrustees = true

        var trusters = TrustFragment()
        trusters.isTrustees = false

        adapter.addFragment(trustees, "Trustees")
        adapter.addFragment(trusters, "Trusters")

        relacaoViewpager.adapter = adapter

        relacaoTabs.setupWithViewPager(relacaoViewpager)
    }




    companion object {
        fun newInstance(): relacaoFragment =
            relacaoFragment()
    }
}