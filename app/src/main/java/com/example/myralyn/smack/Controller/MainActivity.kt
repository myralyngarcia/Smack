package com.example.myralyn.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.myralyn.smack.R
import com.example.myralyn.smack.Services.AuthService
import com.example.myralyn.smack.Services.UserDataService
import com.example.myralyn.smack.Services.UserDataService.name
import com.example.myralyn.smack.Utilities.BROADCAST_DATA_CHANGED
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //registerReceiver needs a receiver and IntentFilter so that it will not receive all intent
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_DATA_CHANGED))

    }
    //create a broadcastreceiver object to be passed as receiver to the broadcastManager
    private val userDataChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            //update navHeader ui
            if (AuthService.isLoggedIn){
                loginBtnNavHeader.text ="logout"
                usernameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userimageNavHeader.setImageResource(resourceId)
                userimageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    fun loginBtnNavClicked(view: View){
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }
    fun addChannelClicked (view: View){

    }

    fun sendMsgBtnClicked (view: View){

    }

}
