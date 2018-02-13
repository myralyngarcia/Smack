package com.example.myralyn.smack.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myralyn.smack.R
import com.example.myralyn.smack.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginLoginBtnClicked (view: View){
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        AuthService.loginUser(this, email, password){loginSuccess ->
            if(loginSuccess){
                AuthService.findUserByEmail(this){findSuccess ->
                    if (findSuccess){
                        finish()
                    }
                }
            }

        }

    }
    //this is when SIGN UP HERE is clicked, we don't go back and click the button again so we finish
    fun loginCreateUserBtnClicked (view: View){
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()

    }

}
