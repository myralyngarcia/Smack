package com.example.myralyn.smack.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.example.myralyn.smack.R
import com.example.myralyn.smack.Services.AuthService
import com.example.myralyn.smack.Services.UserDataService
import com.example.myralyn.smack.Utilities.BROADCAST_DATA_CHANGED
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
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

    fun createUserClicked(view: View){
        spinnerEnable(true)
        val username = createUserNameText.text.toString()
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()

        if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            AuthService.registerUser(this, email, password) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(this, email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(this, username, email, userAvatar, avatarColor){createUserSuccess ->
                                if(createUserSuccess){

                                    val userDataChange = Intent(BROADCAST_DATA_CHANGED)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    spinnerEnable(false)
                                    //dismiss activity since we are done with this activity
                                    finish()
                                }else{errorToast()}
                            }
                        }else{errorToast()}
                    }
                }else {errorToast()}
            }
        }else{
            Toast.makeText(this, "make sure username, password, email are filled in", Toast.LENGTH_LONG).show()
            spinnerEnable(false)
        }


    }

    fun errorToast(){
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        spinnerEnable(false)
    }

    fun spinnerEnable (enable: Boolean){
        if (enable){
            createSpinner.visibility = View.VISIBLE
            createUserBtn.isEnabled = false
            createAvatarImageView.isEnabled = false
            generateAvatarBackgroundColorBtn.isEnabled = false
        }else{
            createSpinner.visibility = View.INVISIBLE
            createUserBtn.isEnabled = true
            createAvatarImageView.isEnabled = true
            generateAvatarBackgroundColorBtn.isEnabled = true
        }



    }

}
