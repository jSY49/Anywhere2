package com.example.anywhere.User

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anywhere.R
import com.example.anywhere.Connect.firebaseConnect
import kotlinx.android.synthetic.main.activityuser.*


class userActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activityuser)

        val fbsconnect = firebaseConnect()
        fbsconnect.firbaseInit()

        val user = fbsconnect.fb_user()

        profileNm.setText(user?.email)

        //로그아웃
        logoutBtn?.setOnClickListener {
            Log.v("로그아웃","로그아웃상태")
            fbsconnect.firbaseInit().signOut()

            if(user!=null){
                Toast.makeText(applicationContext, "로그아웃 성공.", Toast.LENGTH_LONG).show()
                finish()


            }
            else {
                Toast.makeText(applicationContext, "로그아웃 실패.", Toast.LENGTH_LONG).show()
            }
        }
    }
}