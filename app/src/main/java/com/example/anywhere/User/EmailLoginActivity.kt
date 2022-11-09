package com.example.anywhere.User

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anywhere.Connect.firebaseConnect
import com.example.anywhere.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.email_sign_in.*
import kotlinx.android.synthetic.main.email_sign_in.btn_googleSignIn


class emaillogin : AppCompatActivity() {


    val fbsconnect = firebaseConnect()
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    val Tag="emaillogin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_sign_in)

        fbsconnect.firbaseInit()
        fbsconnect.firbaseDBInit()

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //이메일 로그인
        loginButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                var email = edtID.text.toString().trim()
                var password = edtPW.text.toString().trim()

                if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                    Toast.makeText(applicationContext, "Id/pw를 입력해 주세요", Toast.LENGTH_LONG).show()
                } else {
                    loginuser(email, password)
                }

            }
        })

        //이메일로 회원가입 버튼 클릭
        emailmakebtn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(applicationContext, emailsignupActivity::class.java)
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


    private fun loginuser(email: String, password: String) {
        val fbsconnect = firebaseConnect()
        fbsconnect.firbaseInit()
        var task = fbsconnect.fb_email_login(email, password)

        task.addOnCompleteListener(this) { task ->
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOGIN_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

                //사용자 db저장(구글로그인)
                if (account != null) {

                    var personName = account.getDisplayName();
                    var personGivenName = account.getGivenName();
                    var personFamilyName = account.getFamilyName();
                    var personEmail = account.getEmail();
                    var personId = account.getId();
                    var personPhoto: Uri? = account.getPhotoUrl();


                    val profile = hashMapOf(
                        "name" to personName,
                        "nickName" to personFamilyName,
                        "enterPrise" to "x",
                        "Photo" to personPhoto
                    )
                    personEmail?.let {
                        fbsconnect.db.collection("User").document(it)
                            .set(profile)
                            .addOnSuccessListener { Log.d(Tag, "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w(Tag, "Error writing document", e) }
                    }

                    Log.d(Tag, "handleSignInResult:personName "+personName);
                    Log.d(Tag, "handleSignInResult:personGivenName "+personGivenName);
                    Log.d(Tag, "handleSignInResult:personEmail "+personEmail);
                    Log.d(Tag, "handleSignInResult:personId "+personId);
                    Log.d(Tag, "handleSignInResult:personFamilyName "+personFamilyName);
                    Log.d(Tag, "handleSignInResult:personPhoto "+personPhoto);
                }

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
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                })
    }
}