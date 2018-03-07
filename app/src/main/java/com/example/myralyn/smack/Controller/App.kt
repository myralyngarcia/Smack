package com.example.myralyn.smack.Controller

import android.app.Application
import com.example.myralyn.smack.Utilities.SharedPrefs

/**
 * Created by myralyn on 02/03/18.
 */
class App : Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}