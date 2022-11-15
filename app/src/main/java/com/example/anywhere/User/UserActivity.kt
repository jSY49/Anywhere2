package com.example.anywhere.User

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.anywhere.Community.MyPostImgAdapter
import com.example.anywhere.Connect.firebaseConnect
import com.example.anywhere.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_img_full_screen.*
import kotlinx.android.synthetic.main.activityuser.*


class userActivity : AppCompatActivity() {
    val fbsconnect = firebaseConnect()
    val Tag = "userActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activityuser)

        fbsconnect.firbaseInit()
        fbsconnect.firbaseDBInit()
        fbsconnect.firebaseStorageInit()
        getDb();
        val user = fbsconnect.fb_user()

        profileNm.setText(user?.email)


        //로그아웃
        logoutBtn?.setOnClickListener {
            Log.v("로그아웃", "로그아웃상태")
            fbsconnect.firbaseInit().signOut()

            if (user != null) {
                Toast.makeText(applicationContext, "로그아웃 성공.", Toast.LENGTH_LONG).show()
                finish()


            } else {
                Toast.makeText(applicationContext, "로그아웃 실패.", Toast.LENGTH_LONG).show()
            }
        }
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
                        Glide.with(this).load(profilePhoto).placeholder(R.drawable.free_icon_profile_4646084).into(profileImg)
                    }
                    if(document["enterPrise"].toString().equals("o")){
                        TopText.setText("내정보(사업자)")
                        relativeLayout4.setVisibility(View.VISIBLE)

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
        var userEmail=fbsconnect.fb_userEmail()
        val storageRef = fbsconnect.storage.reference
        val pathReference = storageRef.child("usersProfile/$userEmail")
//        var storage:FirebaseStorage  = FirebaseStorage.getInstance("gs://anywhere-2b312.appspot.com");
//        val gsReference =storage.getReferenceFromUrl("gs://anywhere-2b312.appspot.com/usersProfile/$userEmail.jpg")

        pathReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(profileImg)
        }
        pathReference.downloadUrl.addOnFailureListener{
            profileImg.setImageResource(R.drawable.ic_baseline_account_circle_24)
        }

    }

    fun myPostBtn(view: android.view.View) {
        val intent = Intent(
            applicationContext,
            MyPostActivity::class.java
        )
        startActivity(intent)

    }

    fun myBookingBtn(view: android.view.View) {
        val intent = Intent(applicationContext, MyBookingActivity::class.java)
        intent.putExtra("key","UserBooking")
        startActivity(intent)
    }

    fun backBtn(view: android.view.View) {
        finish()
    }

    fun enterPriseBtn(view: android.view.View) {
        val intent = Intent(applicationContext,MyBookingActivity::class.java)
        intent.putExtra("key","enterprise")
        startActivity(intent)
    }
}