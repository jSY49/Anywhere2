package com.example.anywhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.anywhere.loginMenuActivity
import com.example.anywhere.userActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fbsconnect = firebaseConnect()
        fbsconnect.firbaseInit()


        //유저버튼
        Activity_main_userBtn.setOnClickListener {
            if(fbsconnect.fb_user()!=null){
                val intent = Intent(applicationContext, userActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(applicationContext, loginMenuActivity::class.java)
                startActivity(intent)
            }

        }

        //지역별 여행지 리스트 엑티비트
        Activity_main_btn1.setOnClickListener{
            val intent = Intent(applicationContext,listOfareatrip::class.java)
            startActivity(intent)
        }//btn1 클릭 리스너 닫음

        //지도로 보기
        Activity_main_btn2.setOnClickListener{
            val intent = Intent(applicationContext,MapActivity::class.java)
            startActivity(intent)
        }//btn2 클릭 리스너 닫음

        //커뮤니티
        Activity_main_btn3.setOnClickListener{
            val intent = Intent(applicationContext,CommunityActivity::class.java)
            startActivity(intent)
        }//btn3 클릭 리스너 닫음

        //테마별 리스트
        Activity_main_btn4.setOnClickListener{

        }//btn4 클릭 리스너 닫음
        

        //
        Activity_main_SearchBtn.setOnClickListener {
//            val intent = Intent(applicationContext,listOfareatrip::class.java)
//            startActivity(intent)
        }//검색 클릭 리스너 닫음

    }



}


/*
  createUserWithEmailAndPassword  회원가입
   signlnWithEmailAndPassword  로그인
  sendEmailVerification  회원 가입한 이메일 유효 확인
   updateEmail 회원 가입한 아이디 이메일 변경
  updatePassword  회원 가입한 아이디 패스워드 변경
  sendPasswordResetEmail  회원 가입한 비밀번호 재설정
  delete  회원 가입한 아이디 삭제
  reauthenticate  아이디 재 인증
 */