package com.sahilkalra.bookhub.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.sahilkalra.bookhub.*
import com.sahilkalra.bookhub.fragment.AboutAppFragment
import com.sahilkalra.bookhub.fragment.DashboardFragment
import com.sahilkalra.bookhub.fragment.FavouritesFragment
import com.sahilkalra.bookhub.fragment.ProfileFragment


class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout:DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView:NavigationView
    var previousMenuItem: MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=findViewById(R.id.drawerLayout)
        toolbar=findViewById(R.id.toolbar)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        frameLayout=findViewById(R.id.frames)

        navigationView=findViewById(R.id.navigationView)
        setUpToolbar()
        openDashboard()
        val actionBarDrawerToggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem!=null) {
                previousMenuItem?.isChecked = false
            }
                it.isCheckable=true
                it.isChecked=true
                previousMenuItem=it

            when(it.itemId){
                R.id.dashboard ->{
                   openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frames, FavouritesFragment())
                        .commit()
                    supportActionBar?.title="Favourites"
                    drawerLayout.closeDrawers()
                         }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frames, ProfileFragment())

                        .commit()
                    supportActionBar?.title="Profile"
                    drawerLayout.closeDrawers()
                               }
                R.id.aboutUs ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frames, AboutAppFragment())

                        .commit()
                    supportActionBar?.title="About App"
                    drawerLayout.closeDrawers()
                            }
                R.id.Exit->{
                    exitApp()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }
    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if (id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openDashboard(){
        val fragment= DashboardFragment()
        val trancation=supportFragmentManager.beginTransaction()
        trancation.replace(R.id.frames,fragment)
        trancation.commit()
        supportActionBar?.title="Dashboard"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frames)
        when(frag){
            !is DashboardFragment -> openDashboard()
            is DashboardFragment -> exitApp()
            else-> super.onBackPressed()
        }
    }

fun exitApp(){
    val alertDialog= AlertDialog.Builder(this@MainActivity)
    alertDialog.setTitle("Exit")
    alertDialog.setMessage("Do you want to exit the app ?")
    alertDialog.setPositiveButton("Exit"){text,Listener->
        ActivityCompat.finishAffinity(this@MainActivity)
    }
    alertDialog.setNegativeButton("Cancel"){
            text,Listener->
    }
    alertDialog.create()
    alertDialog.show()
}
}