package com.example.anywhere

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anywhere.R
import com.example.anywhere.firebaseConnect
import kotlinx.android.synthetic.main.email_sign_up.*



class emailsignupActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_sign_up)

        val fbsconnect = firebaseConnect()
        fbsconnect.firbaseInit()


        signupButton.setOnClickListener {
            //trim() 앞뒤 공백 제거
            var email = inputnewEmail.text.toString().trim()
            var password = inputPW.text.toString().trim()


            var task = fbsconnect.fb_email_signUp(email, password)

            task.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.v(TAG+" 이메일 가입 성공", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "회원가입 성공",Toast.LENGTH_SHORT).show()
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.v(TAG+" 이메일 가입 실패", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_LONG).show()
                    finish()

                }
            }
            task.addOnFailureListener {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
            }

        }
    }
}
