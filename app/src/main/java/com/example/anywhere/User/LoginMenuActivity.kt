package com.example.anywhere.User


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.loginmenu.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import android.widget.Toast
import com.example.anywhere.R

import com.example.anywhere.Connect.firebaseConnect

import com.google.android.gms.tasks.OnCompleteListener

import com.google.android.gms.common.api.ApiException

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*


class loginMenuActivity : AppCompatActivity() {

    val fbsconnect = firebaseConnect()
    var googleSignInClient :GoogleSignInClient?=null
    var GOOGLE_LOGIN_CODE=9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginmenu)

        fbsconnect.firbaseInit()

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)


        //이메일로 로그인하기 버튼 클릭
        emailsignbtn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
                val intent = Intent(baseContext, emaillogin::class.java)
                startActivity(intent)


            }
        })

        //구글로 로그인 하기
        btn_googleSignIn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val signInIntent: Intent = googleSignInClient!!.getSignInIntent()
                startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
            }

        })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOGIN_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    //사용자가 정상적으로 로그인하면 GoogleSignInAccount 객체에서 ID토큰 가져와서
    //Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase 인증
    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        fbsconnect.auth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//                        val firebaseUser: FirebaseUser? = fbsconnect.auth.getCurrentUser()
//                        if (firebaseUser != null) {
//                            val intent = Intent(application, UserActivity::class.java)
//                            startActivity(intent)
//                        }
                        finish()
                        Toast.makeText(this@loginMenuActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this@loginMenuActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                })
    }



}