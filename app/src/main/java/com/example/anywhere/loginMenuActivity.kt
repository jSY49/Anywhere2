package com.example.anywhere


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.loginmenu.*

class loginMenuActivity : AppCompatActivity() {

    val fbsconnect = firebaseConnect()
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginmenu)

        fbsconnect.firbaseInit()



        //이메일로 로그인하기 버튼 클릭
        emailsignbtn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                finish()
                val intent = Intent(baseContext, emaillogin::class.java)
                startActivity(intent)


            }
        })

//        //구글로 로그인 하기
//        btn_googleSignIn?.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//                val signInIntent = googleSignInClient.signInIntent
//                startForResult.launch(signInIntent)
//
//
//            }
//
//        })
    }

//    private val startForResult = registerForActivityResult(ActivityResultContract.StartActivityForResult()){
//        result: ActivityResult ->
//        if(result.resultCode== RESULT_OK){
//            val intent:Intent =result.data!!
//            val task: Task<GoogleSignInAccount>=GoogleSignIn.getSignedInAccountFromIntent(intent)
//        }
//    }


}