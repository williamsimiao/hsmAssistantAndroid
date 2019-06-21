package com.example.hsmassistantandroid.ui.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.hsmassistantandroid.R
import kotlinx.android.synthetic.main.activity_second.*
import android.view.Menu
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.hsmassistantandroid.ui.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

private val TAG: String = SecondActivity::class.java.simpleName

class SecondActivity : AppCompatActivity(),
    ObjetosListFragment.OnFragmentInteractionListener,
    gestaoUsuarioFragment.OnFragmentInteractionListener {

    lateinit var activeFragment: Fragment

    val fragmentUsuario = NewPermissionFragment()
    val fragmentGestao = gestaoUsuarioFragment.newInstance()
    val fragmentRelacao = relacaoFragment.newInstance()
    val fragmentCertificados = ObjetosListFragment.newInstance()

    override fun onFragmentInteraction(uri: Uri) {
        Log.e("AQUI", "Houve interacao ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        setSupportActionBar(toolbar)

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        supportFragmentManager.beginTransaction().add(R.id.container, fragmentUsuario, "4").hide(fragmentUsuario).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, fragmentGestao, "3").hide(fragmentGestao).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, fragmentRelacao, "2").hide(fragmentRelacao).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, fragmentCertificados, "1").commit()
        activeFragment = fragmentCertificados
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_objetos -> {
                toolbar.title = "Certificados"
                supportFragmentManager.beginTransaction().hide(activeFragment).show(fragmentCertificados).commit()
                activeFragment = fragmentCertificados
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_relacao -> {
                toolbar.title = "Relacoes"
                supportFragmentManager.beginTransaction().hide(activeFragment).show(fragmentRelacao).commit()
                activeFragment = fragmentRelacao
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_gestao -> {
                toolbar.title = "Gestão"
                supportFragmentManager.beginTransaction().hide(activeFragment).show(fragmentGestao).commit()
                activeFragment = fragmentGestao
//                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_usuario -> {
                toolbar.title = "Usuário"
                supportFragmentManager.beginTransaction().hide(activeFragment).show(fragmentUsuario).commit()
                activeFragment = fragmentUsuario
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }



//    fun setUpBottomAppBar() {
//        setSupportActionBar(bottomAppBar)
//
//        bottomAppBar.replaceMenu(R.menu.bottomappbar_menu)
//
//        //click event over navigation menu like back arrow or hamburger icon
//        bottomAppBar.setNavigationOnClickListener { didTapNavigationButton() }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.bottomappbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.app_bar_user -> {
//
//                val userOptionsFragment = UserOptionsBottomSheetFragment()
//                userOptionsFragment.show(supportFragmentManager, userOptionsFragment.tag)
//
//                Log.d(TAG, "Click on user")
//            }
//
//            else -> Log.d(TAG, "Estranho isso, não existe outro")
//        }
        return super.onOptionsItemSelected(item)
    }
}
