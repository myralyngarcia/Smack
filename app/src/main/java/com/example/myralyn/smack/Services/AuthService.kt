package com.example.myralyn.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myralyn.smack.Services.UserDataService.id
import com.example.myralyn.smack.Utilities.URL_CREATE_USER
import com.example.myralyn.smack.Utilities.URL_LOGIN
import com.example.myralyn.smack.Utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject




/**
 * Created by myralyn on 28/01/18.
 */
object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    //we need to pass context coz Volley requires it. also we need completion handler that returns boolean
    fun registerUser(context: Context, email: String, password: String, complete: (Boolean)-> Unit){
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
        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser (context: Context, email: String, password: String, complete: (Boolean) -> Unit){
        val url = URL_LOGIN
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val stringBody = jsonBody.toString()
        //use JsonObjectRequest coz we are expecting json response
        val loginRequest = object: JsonObjectRequest(Request.Method.POST, url, null, Response.Listener { response ->

            try {//getString returns a jsonObject exception so we need to do try/catch
                //parse json response
                authToken = response.getString("token")
                userEmail = response.getString("user")
                isLoggedIn = true
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
        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser (context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit){
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
                headers.put("Authorization","Bearer $authToken")
                return headers
            }

            override fun getBody(): ByteArray {
                return stringBody.toByteArray()
            }
        }
        Volley.newRequestQueue(context).add(createRequest)
    }

}