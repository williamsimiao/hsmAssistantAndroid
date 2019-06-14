package com.example.hsmassistantandroid.ui.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.hsmassistantandroid.R
import kotlinx.android.synthetic.main.activity_second.*
import android.view.Menu
import androidx.navigation.Navigation.findNavController
import com.example.hsmassistantandroid.ui.fragments.*

private val TAG: String = SecondActivity::class.java.simpleName


class SecondActivity : AppCompatActivity(),
    ObjetosListFragment.OnFragmentInteractionListener,
    PainelFragment.OnFragmentInteractionListener,
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

                val userOptionsFragment = UserOptionsBottomSheetFragment()
                userOptionsFragment.show(supportFragmentManager, userOptionsFragment.tag)

                Log.d(TAG, "Click on user")
            }

            else -> Log.d(TAG, "Estranho isso, não existe outro")
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
