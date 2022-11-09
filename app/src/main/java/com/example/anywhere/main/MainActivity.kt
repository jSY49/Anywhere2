package com.example.anywhere.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.anywhere.Booking.BookingActivity
import com.example.anywhere.Community.CommunityActivity
import com.example.anywhere.Connect.firebaseConnect
import com.example.anywhere.Festival.FestivalActivity
import com.example.anywhere.Map.MapActivity
import com.example.anywhere.R
import com.example.anywhere.User.emaillogin
import com.example.anywhere.User.userActivity
import com.example.anywhere.itemList.ListOfAreaTripActivity
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activityuser.*


class MainActivity : AppCompatActivity() {

    var Tag = "MainActivity"
    var backPressedTime: Long = 0
    val fbsconnect = firebaseConnect()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fbsconnect.firbaseInit()
        fbsconnect.firbaseDBInit()
        fbsconnect.firebaseStorageInit()
        getDb();


        // 애니메이션 재생
        val anim = AnimationUtils.loadAnimation(this, R.anim.blink_animation)
        Activity_main_TextView.startAnimation(anim)


        //유저버튼
        Activity_main_userBtn.setOnClickListener {
            if (fbsconnect.fb_user() != null) {
                val intent = Intent(applicationContext, userActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(applicationContext, emaillogin::class.java)
                startActivity(intent)
            }

        }

        //관광지 리스트 엑티비트
        Activity_main_TourBtn.setOnClickListener {
            val intent = Intent(
                applicationContext,
                ListOfAreaTripActivity::class.java
            )
            intent.putExtra("wantService", "areaBasedList")
            intent.putExtra("cId", "12")
            startActivity(intent)
        }//btn1 클릭 리스너 닫음

        //지도로 보기
        Activity_main_MapBtn.setOnClickListener {
            val intent = Intent(
                applicationContext,
                MapActivity::class.java
            )
            startActivity(intent)
        }//btn2 클릭 리스너 닫음

        //커뮤니티
        Activity_main_CommunityBtn.setOnClickListener {
            val intent = Intent(
                applicationContext,
                CommunityActivity::class.java
            )
            startActivity(intent)
        }//btn3 클릭 리스너 닫음

        //축제
        Activity_main_festivalBtn.setOnClickListener {
            val intent = Intent(
                applicationContext,
                FestivalActivity::class.java
            )
            startActivity(intent)
        }//btn4 클릭 리스너 닫음


        //상품 예약/구매
        Activity_main_BookingBtn.setOnClickListener {
            val intent = Intent(
                applicationContext,
                BookingActivity::class.java
            )
            startActivity(intent)
        }

        //음식점 리스트
        Activity_main_RestaurantBtn.setOnClickListener {
            val intent = Intent(
                applicationContext,
                ListOfAreaTripActivity::class.java
            )
            intent.putExtra("wantService", "areaBasedList")
            intent.putExtra("cId", "39")
            startActivity(intent)
        }
        //통합검색버튼
        Activity_main_SearchBtn.setOnClickListener {
            val keyword = Activity_main_EditText.text.toString()
            if (keyword.isNullOrEmpty()) {
                Toast.makeText(this, "검색어를 입력해 주세요", Toast.LENGTH_LONG).show()
            } else {
                hideKeyboard()
                val intent = Intent(applicationContext, TotalSearchActivity::class.java)
                intent.putExtra("keyWord", keyword)
                startActivity(intent)
            }

        }


        //엔터키 이벤트 처리
        Activity_main_EditText.setOnKeyListener { v, keyCode, event ->

            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                val keyword = Activity_main_EditText.text.toString()
                if (keyword.isNullOrEmpty()) {
                    Toast.makeText(this, "검색어를 입력해 주세요", Toast.LENGTH_LONG).show()
                } else {
                    hideKeyboard()
                    val intent = Intent(applicationContext, TotalSearchActivity::class.java)
                    intent.putExtra("keyWord", keyword)
                    startActivity(intent)
                }
            } else if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_BACK) {
                hideKeyboard()
                Activity_main_EditText.isClickable = false
                Activity_main_EditText.isFocusable = false
            }
            true
        }

        //키보드 밖 클릭 시 키보드 숨김
        layout.setOnTouchListener(OnTouchListener { v: View?, event: MotionEvent? ->
            try {
                hideKeyboard()
                Activity_main_EditText.isClickable = false
                Activity_main_EditText.isFocusable = false
            } catch (e: Exception) {
                Log.d(Tag, "layTouchException", e)
            }
            Activity_main_EditText.isFocusable = true
            Activity_main_EditText.isFocusableInTouchMode = true

            false

        })
    }

    fun hideKeyboard() {
        val inputManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            this.currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    override fun onBackPressed() {

        //현재시간보다 크면 종료
        if (backPressedTime + 3000 > System.currentTimeMillis()) {

            super.onBackPressed()
            finish()//액티비티 종료
        } else {
            Toast.makeText(
                applicationContext, "한번 더 뒤로가기 버튼을 누르면 종료됩니다.",
                Toast.LENGTH_SHORT
            ).show()
        }

        //현재 시간 담기
        backPressedTime = System.currentTimeMillis()
    }


    private fun getDb() {
        val docRef: DocumentReference =
            fbsconnect.db.collection("User").document(fbsconnect.fb_userEmail().toString())
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    var profilePhoto :String=document["Photo"].toString()

                    if (profilePhoto.equals("o")) {
                        //스토리지 참조
                        getStorage()
                    } else{
                        Glide.with(this).load(profilePhoto).placeholder(R.drawable.ic_baseline_account_circle_24).into(Activity_main_userBtn)
                    }

                } else {
                    Log.d(Tag, "No such document")

                }
            } else {
                Log.d(Tag, "get failed with ", task.exception)
            }
        }


    }

    private fun getStorage() {
        Log.d(Tag, "get  Storage")
        var userEmail = fbsconnect.fb_userEmail()
        val storageRef = fbsconnect.storage.reference
        val pathReference = storageRef.child("usersProfile/$userEmail")

        pathReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(Activity_main_userBtn)
        }
        pathReference.downloadUrl.addOnFailureListener {
            profileImg.setImageResource(R.drawable.ic_baseline_account_circle_24)
        }

    }

    override fun onRestart() {
        super.onRestart()
        val user = fbsconnect.fb_user()

        if (user == null) {
            Activity_main_userBtn.setImageResource(R.drawable.ic_baseline_account_circle_24)
        }else{
            getDb()
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