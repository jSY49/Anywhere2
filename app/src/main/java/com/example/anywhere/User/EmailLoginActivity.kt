package com.example.anywhere.User

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anywhere.R
import com.example.anywhere.Connect.firebaseConnect
import kotlinx.android.synthetic.main.email_sign_in.*



class emaillogin : AppCompatActivity()  {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_sign_in)



        //이메일 로그인
        loginButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                var email = edtID.text.toString().trim()
                var password = edtPW.text.toString().trim()

                loginuser(email,password)


            }
        })


        //이메일 가입하기
        emailmakebtn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val intent = Intent(applicationContext, emailsignupActivity::class.java)
                startActivity(intent)

            }
        })

    }


    private fun loginuser(email: String, password: String) {
        val fbsconnect = firebaseConnect()
        fbsconnect.firbaseInit()
        var task = fbsconnect.fb_email_login(email, password)


        task.addOnCompleteListener(this) {task->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "LoginUserWithEmail:success")
                    //val user = auth.currentUser
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                   // ActivityCompat.finishAffinity(this);  //모든 activity 종료 (앱이 완전 종료됨)
                    finish()


                } else {
                    Log.w(ContentValues.TAG, "LoginUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                }
            }
            task.addOnFailureListener {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
            }
    }
}