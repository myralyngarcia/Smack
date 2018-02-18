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
import com.example.myralyn.smack.Model.Channel
import com.example.myralyn.smack.R
import com.example.myralyn.smack.Services.AuthService
import com.example.myralyn.smack.Services.MessageService
import com.example.myralyn.smack.Services.UserDataService
import com.example.myralyn.smack.Utilities.BROADCAST_USER_DATA_CHANGED
import com.example.myralyn.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
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

        //we moved from onResume coz these 2 line of code is printed 2x
        //we only want to be called once so we put it here in onCreate
        socket.connect()
        socket.on("channelCreated", onNewChannel)//we listen of event, ChannelCreated and use onNewChannel to extract the info

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onResume() {
        //registerReceiver needs a receiver and IntentFilter so that it will not receive all intent
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGED))
        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
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

    //this emitter listener will run in worker thread not on main thread to avoid
    //the MainActivity hanging
    // on arg we are on worker or background thread
    //but on runOnUiThread we are back in the ui thread
    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
                //here we can update the list view, add stuff to the channel
            //lets extract the information that is comming from the emitter from the args
            //this info is based on the ui code when we look at atom
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String
            val newChannel = Channel(channelName, channelDescription, channelId)
            //now add to the channel Array
            MessageService.channels.add(newChannel)
            //print below to see if it is working
            println(newChannel.name)
            println(newChannel.description)
            println(newChannel.id)
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
