package com.example.myralyn.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myralyn.smack.Utilities.URL_REGISTER
import org.json.JSONObject




/**
 * Created by myralyn on 28/01/18.
 */
object AuthService {

    //we need to pass context coz Volley requires it. also we need completion handler that returns boolean
    fun registerUser(context: Context, email: String, password: String, complete: (Boolean)-> Unit){
        val url = URL_REGISTER
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        //convert request body to string so we can convert to byteArray for Volley
        val requestBody = jsonBody.toString()

        //now create the registerRequest object
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
//
        }
        //put reqister request in the queue in this context
        Volley.newRequestQueue(context).add(registerRequest)
    }
}