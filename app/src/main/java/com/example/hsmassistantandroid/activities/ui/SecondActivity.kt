package com.example.hsmassistantandroid.activities.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.hsmassistantandroid.R
import com.example.hsmassistantandroid.api.NetworkManager
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import android.view.Menu


class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        setUpBottomAppBar()

    }

    fun setUpBottomAppBar() {
        setSupportActionBar(bottomAppBar)

        //click event over Bottom bar menu item
        bottomAppBar.replaceMenu(R.menu.bottomappbar_menu)
//        bottomAppBar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
//            override fun onMenuItemClick(item: MenuItem): Boolean {
//                when (item.getItemId()) {
//                    R.id.app_bar_fav -> Log.d("XIXI", "Clicou no search")
//                    else -> Log.d("XIXI", "Outro")
//                }
//                return false
//            }
//        })

        //click event over navigation menu like back arrow or hamburger icon
        bottomAppBar.setNavigationOnClickListener { coisa() }
    }

    fun coisa() {
        Log.d("XIXI", "Navigation Click")
//        val bottomSheetDialogFragment = BottomSheetNavigationFragment()
        val bottomSheetDialogFragment = BottomSheetDialogFragment()
        bottomSheetDialogFragment.show(supportFragmentManager, "Bottom Sheet Dialog Fragment")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottomappbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_fav -> Log.d("XIXI", "Clicou no search 2")
            else -> Log.d("XIXI", "Outro2")
        }
        return super.onOptionsItemSelected(item)
    }
}
