package com.example.myralyn.smack.Adapter

import android.content.Context
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.view.menu.ListMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.myralyn.smack.Model.Message
import com.example.myralyn.smack.R
import com.example.myralyn.smack.Services.UserDataService
import java.util.ArrayList

/**
 * Created by myralyn on 15/03/18.
 */
class MessageAdapter(val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        //lets create our views, we pass the context that we use to contruct our adapter
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        //now we return the Viewholder and pass in the view that we just created
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        //we tell listview how many messages to expect
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        //we bind the message
       holder?.bindMessage(context, messages[position])
    }

    inner class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView){
        //we need to get handles of all the ui elements for our message_list_view
        val userImage = itemView?.findViewById<ImageView>(R.id.messageUserImage)
        val timeStamp = itemView?.findViewById<TextView>(R.id.timeStampLbl)
        val userName = itemView?.findViewById<TextView>(R.id.messageUserNameLbl)
        val messageBody = itemView?.findViewById<TextView>(R.id.messageBodyLbl)

        //now we need our bindMessage fun
        fun bindMessage(context: Context, message: Message){
            //this is where we bind our data to our view
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timeStamp?.text = message.timeStamp
            messageBody?.text = message.message
        }
    }
}