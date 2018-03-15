package com.example.myralyn.smack.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.myralyn.smack.Adapter.MessageAdapter
import com.example.myralyn.smack.Model.Channel
import com.example.myralyn.smack.Model.Message
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
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){
    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageAdapter
    var selectedChannel: Channel? = null

    private fun setupAdapters(){
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
        messageAdapter = MessageAdapter(this, MessageService.messages)
        messageListView.adapter = messageAdapter

        //dont forget your layout manager bec with recycler view you need to have the layour manager
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //we moved from onResume coz these 2 line of code is printed 2x
        //we only want to be called once so we put it here in onCreate
        socket.connect()
        socket.on("channelCreated", onNewChannel)//we listen of event, ChannelCreated and use onNewChannel to extract the info
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapters()
        //registerReceiver needs a receiver and IntentFilter so that it will not receive all intent
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGED))

        channel_list.setOnItemClickListener { _, _, _, i ->
            //so we are going to change the name of the selectedChannel to the one we selected which is id
            selectedChannel = MessageService.channels[i.toInt()]
            //then we close the Nav drawer, START to start closing
            drawer_layout.closeDrawer(GravityCompat.START)
            //and we are just going to update with channel to the newly selected channel
            updateWithChannel()
        }
        //when we start the app we need to check if user is logged-in
        //if so we finduseremail and no need to login
        if (App.prefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }
    }

//    override fun onResume() {
//        //registerReceiver needs a receiver and IntentFilter so that it will not receive all intent
//        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
//                IntentFilter(BROADCAST_USER_DATA_CHANGED))
//        super.onResume()
//    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }
    //create a broadcastreceiver object to be passed as receiver to the broadcastManager
    private val userDataChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            //update navHeader ui
            if (App.prefs.isLoggedIn){
                usernameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable",
                        packageName)
                userimageNavHeader.setImageResource(resourceId)
                userimageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text ="Logout"

                MessageService.getChannels(){ complete ->
                    if (complete){
                        if (MessageService.channels.count()>0){
                            selectedChannel = MessageService.channels[0]
                            //then set dataHas changed, if there are no channel at all we don’t need to notifyData has changed
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }

                }
            }
        }
    }

    fun updateWithChannel(){
        mainChannelName.text = "#${selectedChannel?.name}"
        //this is where we are going to download the messages
        if (selectedChannel != null){
            //we do double exclamation coz we already know that selectChannel is not null hence id exists
            //also we do our lambda expression complete
            MessageService.getMessages(selectedChannel!!.id){complete ->
                //if there are message of complete
                if (complete){
                    messageAdapter.notifyDataSetChanged()
                    //now we want to scroll to the end of the message, not on top
                    if (messageAdapter.itemCount > 0){
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount-1)
                    }
                }
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
        if(App.prefs.isLoggedIn){
            //we want to logout and clear out the UserData variable and AuthService
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            usernameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userimageNavHeader.setImageResource(R.drawable.profiledefault)
            userimageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"
            mainChannelName.text ="Please login"

        }else{
            //we want to login coz here we are logged-in
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
    fun addChannelClicked (view: View){
        //do a check: allow user to create channel only if they are logged-in
        if(App.prefs.isLoggedIn){
            //builder for alert dialog
            val builder = AlertDialog.Builder(this)
            //create dialog view from custom layout
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                    .setPositiveButton("Add"){_, _ ->
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
                    .setNegativeButton ("Cancel"){_, _ ->
                        //cancel and close the dialog
                    }.show()
        }

    }

    //this emitter listener will run in worker thread not on main thread to avoid
    //the MainActivity hanging
    // on arg we are on worker or background thread
    //but on runOnUiThread we are back in the ui thread
    private val onNewChannel = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn){
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
                channelAdapter.notifyDataSetChanged()//new channel added will be reloaded in the display list
            }
        }

    }

    //we receive the args that we are able to access
    private val onNewMessage = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn){
            //we are going to runOnUiThread bec. the callback of this is on diff background or worker thread
            //and so we need to get back on the ui thread so we can make changes to the ui
            runOnUiThread {
                val channelId = args[2] as String //notice we skip 1 coz we are not using userId
                if (channelId == selectedChannel?.id){
                    //we are going to extract from args the values that we need
                    val msgBody = args[0] as String

                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    //now that we have all these values let go create a message object
                    val newMessage = Message(msgBody, channelId, userName, userAvatar,
                            userAvatarColor, id, timeStamp)
                    MessageService.messages.add(newMessage)
                    //when we receive a new message as well we need to update the messages view
                    messageAdapter.notifyDataSetChanged()
                    //we also want to go to the last message
                    messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)

                }
            }
        }
    }

    fun sendMsgBtnClicked (view: View){
        //make sure user isLoggedIn and messageTextField is not empty and there is selectedChannel
        if (App.prefs.isLoggedIn && messageTxtField.text.isNotEmpty() && selectedChannel != null){
            //create some variable for the userId and channelId
            var userId = UserDataService.id
            var channelId = selectedChannel!!.id //safe call is one ! but we already did check is not null hence we use !!
            socket.emit("newMessage", messageTxtField.text.toString(), userId, channelId,
                    UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            messageTxtField.text.clear() //clear the text field once message is emitted
            //then we hide the keyboard
            hideKeyboard()
        }

    }

    fun hideKeyboard (){
        var inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }


}
