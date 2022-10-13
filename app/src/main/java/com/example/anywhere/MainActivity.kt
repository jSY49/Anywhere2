package com.example.anywhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

import com.example.anywhere.Community.CommunityActivity
import com.example.anywhere.Connect.firebaseConnect
import com.example.anywhere.Festival.FestivalActivity
import com.example.anywhere.Map.MapActivity
import com.example.anywhere.User.loginMenuActivity
import com.example.anywhere.User.userActivity
import com.example.anywhere.itemList.ListOfAreaTripActivity


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

        //관광지 리스트 엑티비트
        Activity_main_TourBtn.setOnClickListener{
            val intent = Intent(applicationContext,
                ListOfAreaTripActivity::class.java)
            intent.putExtra("wantService","areaBasedList")
            intent.putExtra("cId","12")
            startActivity(intent)
        }//btn1 클릭 리스너 닫음

        //지도로 보기
        Activity_main_MapBtn.setOnClickListener{
            val intent = Intent(applicationContext,
                MapActivity::class.java)
            startActivity(intent)
        }//btn2 클릭 리스너 닫음

        //커뮤니티
        Activity_main_CommunityBtn.setOnClickListener{
            val intent = Intent(applicationContext,
                CommunityActivity::class.java)
            startActivity(intent)
        }//btn3 클릭 리스너 닫음

        //축제
        Activity_main_festivalBtn.setOnClickListener{
            val intent = Intent(applicationContext,
                FestivalActivity::class.java)
            startActivity(intent)
        }//btn4 클릭 리스너 닫음
        

        //상품 예약/구매
        Activity_main_BookingBtn.setOnClickListener {
            val intent = Intent(applicationContext,BookingActivity::class.java)
            startActivity(intent)
        }

        //음식점 리스트
        Activity_main_RestaurantBtn.setOnClickListener {
            val intent = Intent(applicationContext,
                ListOfAreaTripActivity::class.java)
            intent.putExtra("wantService","areaBasedList")
            intent.putExtra("cId","39")
            startActivity(intent)
        }
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