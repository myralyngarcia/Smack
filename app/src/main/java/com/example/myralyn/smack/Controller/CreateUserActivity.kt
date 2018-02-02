package com.example.myralyn.smack.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myralyn.smack.R
import com.example.myralyn.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvatar(view: View) {
        var random = Random()
        var color = random.nextInt(2)
        var avatar = random.nextInt(28)
        if (color == 1) {
            userAvatar = "light$avatar"}
        else{

            userAvatar = "dark$avatar"
        }
        val resourceId = resources.getIdentifier(userAvatar,"drawable", packageName)
        createAvatarImageView.setImageResource(resourceId)
    }
    fun createUserClicked(view: View){
        AuthService.registerUser(this, "myra3@gmail.com", "12345") {complete ->
            if (complete){
                println("user Registration successful")
            }
            else{println("failed to register user") }
        }

    }
    fun generateAvatarBackgroundColorClicked(view: View){
        var random = Random()
        var r = random.nextInt(255)
        var g = random.nextInt(255)
        var b = random.nextInt(255)
        createAvatarImageView.setBackgroundColor(Color.rgb(r,g,b))
        var savedR = r.toDouble()/255
        var savedG = g.toDouble()/255
        var savedB = b.toDouble()/255

        avatarColor="[savedR, savedG, savedG, 1]"

    }
}
