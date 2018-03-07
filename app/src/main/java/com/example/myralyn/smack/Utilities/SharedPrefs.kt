package com.example.myralyn.smack.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

/**
 * Created by myralyn on 02/03/18.
 */
class SharedPrefs(context: Context) {

    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0) //MODE =0 content is private
    //we are going to save the isLoggedIn, userEmail, authToken from object AuthService to the device (will update Authservice later)

    //our constant key which will be used to retrieve the value (as we work on key,value pair)
    val IS_LOGGED_IN = "isLoggedIn"
    val AUTH_TOKEN = "authToken"
    val USER_EMAIL = "userEmail"

    //now we create variables that we are going to receive and retrieve from
    //then we need to create custom getter and setter
    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()
    var authToken: String
        get() = prefs.getString(AUTH_TOKEN,"")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()
    var userEmail: String
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    //create one requestqueue for the entire app then refactor the multiple request queue in AuthService, MessageService
    val requestQueue = Volley.newRequestQueue(context)

}