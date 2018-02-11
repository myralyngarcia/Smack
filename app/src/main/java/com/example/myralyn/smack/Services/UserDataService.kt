package com.example.myralyn.smack.Services

import android.graphics.Color
import com.example.myralyn.smack.R.id.basic
import com.example.myralyn.smack.R.id.loginBtnNavHeader
import java.util.*

/**
 * Created by myralyn on 04/02/18.
 */
object UserDataService {
    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun logout(){
        id = ""
        avatarName =""
        avatarColor =""
        email = ""
        name = ""
        AuthService.authToken=""
        AuthService.userEmail=""
        AuthService.isLoggedIn=false
    }

    //the component is the RGB and we return Int coz in android colors are actually integers
    fun returnAvatarColor (components: String): Int{
        //this is the string array that we got for the user avatarColor in the database
        //[0.5254901960784314, 0.6235294117647059, 0.6235294117647059, 1]
        println("component: $components")
        val strippedColor = components
                .replace("[", "")
                .replace("]", "")
                .replace(",", "")
        //scanner will start at beginning of string and pick up the first double value, next double value
        var r = 0
        var g = 0
        var b = 0
        //strippedColor is: 0.5254901960784314 0.6235294117647059 0.6235294117647059 1

       var scanner = Scanner(strippedColor)
//        scanner.useLocale(Locale.CANADA)
//        println("locale is: ${scanner.locale()}")
        if (scanner.hasNext()){
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()

        }
        return Color.rgb(r,g,b)//returns an integer for the rgb
    }
}