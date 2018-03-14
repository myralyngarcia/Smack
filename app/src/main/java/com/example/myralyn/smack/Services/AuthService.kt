package com.example.myralyn.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myralyn.smack.Controller.App
import com.example.myralyn.smack.Services.UserDataService.id
import com.example.myralyn.smack.Utilities.*
import org.json.JSONException
import org.json.JSONObject




/**
 * Created by myralyn on 28/01/18.
 */
object AuthService {

    //we need to pass context coz Volley requires it. also we need completion handler that returns boolean
    fun registerUser(email: String, password: String, complete: (Boolean)-> Unit){
        val url = URL_REGISTER
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        //convert request body to string so we can convert to byteArray for Volley
        val requestBody = jsonBody.toString()

        //use StringRequest coz we are expecting a String for response
        val registerRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            println(response)
            complete(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "could not register user: $error")
            complete(false)

        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"

            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        //put reqister request in the queue in this context
        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser (email: String, password: String, complete: (Boolean) -> Unit){
        val url = URL_LOGIN
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val stringBody = jsonBody.toString()
        //use JsonObjectRequest coz we are expecting json response
        val loginRequest = object: JsonObjectRequest(Request.Method.POST, url, null, Response.Listener { response ->

            try {//getString returns a jsonObject exception so we need to do try/catch
                //parse json response
                App.prefs.authToken = response.getString("token")
                App.prefs.userEmail = response.getString("user")
                App.prefs.isLoggedIn = true
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON", "EXEC: " + e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "login failed $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"

            }

            override fun getBody(): ByteArray {
                return stringBody.toByteArray()
            }

        }
        App.prefs.requestQueue.add(loginRequest)
    }

    fun createUser (name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit){
        val jsonBody = JSONObject()
        jsonBody.put("name",name)
        jsonBody.put("email",email)
        jsonBody.put("avatarName",avatarName)
        jsonBody.put("avatarColor",avatarColor)
        val stringBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest (Request.Method.POST,URL_CREATE_USER,null,Response.Listener { response->
            try {
                UserDataService.id = response.getString("_id")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.email = response.getString("email")
                UserDataService.name = response.getString("name")
                complete(true)

            }catch (e: JSONException){
                Log.d("JSON" , "EXEC " + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not add user $error")
            complete(false)

        } ){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization","Bearer ${App.prefs.authToken}")
                return headers
            }

            override fun getBody(): ByteArray {
                return stringBody.toByteArray()
            }
        }
        App.prefs.requestQueue.add(createRequest)
    }

    //for this we only need context coz its a get request we are not passing any body
    //and everything we need to send is part of the url

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit){
        var findUserRequest = object: JsonObjectRequest(Request.Method.GET, "$URL_GET_USER${App.prefs.userEmail}", null,
                Response.Listener{response ->
                    try{
                        UserDataService.id = response.getString("_id")
                        UserDataService.avatarColor = response.getString("avatarColor")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.email = response.getString("email")
                        UserDataService.name = response.getString("name")

                        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGED)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                        complete(true)
                    }catch (e: JSONException){
                        Log.d("JSON", "EXEC:"+ e.localizedMessage)
                        complete(false)
                    }


                },
                Response.ErrorListener { error ->
                Log.d("ERROR", "failed to get user by email")
                complete(false)}
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization","Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(findUserRequest)
    }

}