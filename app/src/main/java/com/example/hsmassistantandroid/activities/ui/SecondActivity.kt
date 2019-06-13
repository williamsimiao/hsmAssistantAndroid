package com.example.hsmassistantandroid.activities.ui

import android.content.Intent
import android.net.Uri
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
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_bottomsheet.*


class SecondActivity : AppCompatActivity(), ObjetosListFragment.OnFragmentInteractionListener,
                       PainelFragment.OnFragmentInteractionListener ,
                       userSettingsFragment.OnFragmentInteractionListener,
                       gestaoUsuarioFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        Log.e("AQUI", "Houve interacao ")
    }

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
            R.id.app_bar_user -> {
                Log.d("XIXI", "Clicou no user")
            }

            else -> Log.d("XIXI", "Estranho isso, não existe outro")
        }
        return super.onOptionsItemSelected(item)
    }

    //ACTIONS
    override fun onSupportNavigateUp() =
        findNavController(this, R.id.navHostFragment).navigateUp()

    fun didTapNavigationButton() {
        val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
        bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
    }
}
