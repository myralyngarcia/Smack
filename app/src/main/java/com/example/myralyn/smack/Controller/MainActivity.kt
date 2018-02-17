package com.example.myralyn.smack.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.myralyn.smack.R
import com.example.myralyn.smack.Services.AuthService
import com.example.myralyn.smack.Services.UserDataService
import com.example.myralyn.smack.Utilities.BROADCAST_USER_DATA_CHANGED
import com.example.myralyn.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){
    val socket = IO.socket(SOCKET_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onResume() {
        //registerReceiver needs a receiver and IntentFilter so that it will not receive all intent
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGED))
        socket.connect()
        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onPause()
    }

    override fun onDestroy() {
        socket.disconnect()
        super.onDestroy()
    }
    //create a broadcastreceiver object to be passed as receiver to the broadcastManager
    private val userDataChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            //update navHeader ui
            if (AuthService.isLoggedIn){
                usernameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable",
                        packageName)
                userimageNavHeader.setImageResource(resourceId)
                userimageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text ="Logout"
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
        if(AuthService.isLoggedIn){
            //we want to logout and clear out the UserData variable and AuthService
            UserDataService.logout()
            usernameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userimageNavHeader.setImageResource(R.drawable.profiledefault)
            userimageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"

        }else{
            //we want to login coz here we are logged-in
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
    fun addChannelClicked (view: View){
        //do a check: allow user to create channel only if they are logged-in
        if(AuthService.isLoggedIn){
            //builder for alert dialog
            val builder = AlertDialog.Builder(this)
            //create dialog view from custom layout
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                    .setPositiveButton("Add"){dialogInterface, i ->
                        //perform some logic when clicked.
                        // Get reference to the UI elements in Dialog views
                        val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                        val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)

                        //now we can get the text via reference
                        val channelName = nameTextField.text.toString()
                        val channelDesc = descTextField.text.toString()

                        // create channel with name and description,
                        socket.emit("newChannel", channelName, channelDesc)

                    }
                    .setNegativeButton ("Cancel"){dialogInterface, i ->
                        //cancel and close the dialog
                    }.show()
        }

    }

    fun sendMsgBtnClicked (view: View){
        hideKeyboard()
    }

    fun hideKeyboard (){
        var inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }


}
