package com.example.hsmassistantandroid.activities.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.preference.PreferenceManager
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

        bottomAppBar.replaceMenu(R.menu.bottomappbar_menu)

        //click event over navigation menu like back arrow or hamburger icon
        bottomAppBar.setNavigationOnClickListener { didTapNavigationButton() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottomappbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_user -> Log.d("XIXI", "Clicou no search 2")

            else -> Log.d("XIXI", "Estranho isso, não existe outro")
        }
        return super.onOptionsItemSelected(item)
    }

    //ACTIONS
    fun didTapNavigationButton() {
        val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
        bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
    }
}
