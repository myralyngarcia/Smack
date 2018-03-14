package com.example.myralyn.smack.Services

import android.app.DownloadManager
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethod
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.myralyn.smack.Controller.App
import com.example.myralyn.smack.Model.Channel
import com.example.myralyn.smack.Utilities.URL_GET_CHANNELS
import org.json.JSONException

/**
 * Created by myralyn on 17/02/18.
 */
object MessageService {
    //this object is going to download the channel messages, etc
    val channels = ArrayList<Channel>()  //initialize as empty arrayList of type Channel

    fun getChannels (complete: (Boolean)->Unit){
        val channelRequest = object: JsonArrayRequest (Method.GET, URL_GET_CHANNELS, null, Response.Listener { reponse ->
            try{
                for (x in 0 until reponse.length()){
                    val channel = reponse.getJSONObject(x)
                    val name = channel.getString("name")
                    val channelDesc = channel.getString("description")
                    val channelId = channel.getString("_id")
                    val newChannel = Channel(name, channelDesc, channelId)
                    this.channels.add(newChannel)
                }
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON", "EXC"+ e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error->
            Log.d("ERROR", "Could not retrieve channels")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String >()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(channelRequest)
    }

}