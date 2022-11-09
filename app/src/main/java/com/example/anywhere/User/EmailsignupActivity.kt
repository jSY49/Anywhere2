package com.example.anywhere.User

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.anywhere.Connect.firebaseConnect
import com.example.anywhere.R
import kotlinx.android.synthetic.main.email_sign_up.*


class emailsignupActivity : AppCompatActivity() {


    val Tag = "emailsignupActivity"

    var pickImageFromAlbum = 0
    var uriPhoto: Uri? = null
    val fbsconnect= firebaseConnect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_sign_up)


        fbsconnect.firbaseInit()
        fbsconnect.firbaseDBInit()
        fbsconnect.firebaseStorageInit()
        var storage = fbsconnect.storage;

        //프로필 사진 선택 업로드
        profileImg.setOnClickListener {
            //1.사진을 이미지뷰에 업로드
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
            //Open Album
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, pickImageFromAlbum)

        }


        //회원가입 버튼 클릭
        signupButton.setOnClickListener {
            //trim() 앞뒤 공백 제거
            var name = inputName.text.toString().trim()
            var nickName = inputNickName.text.toString().trim()
            var email = inputnewEmail.text.toString().trim()
            var password = inputPW.text.toString().trim()
            var photoIs: String?="x"
            if(name.isBlank()||nickName.isBlank()||email.isBlank()||password.isBlank()){
                Toast.makeText(this,"회원가입 정보를 모두 입력해 주세요",Toast.LENGTH_LONG).show()
            }else{
                var task = fbsconnect.fb_email_signUp(email, password)
                task.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if(uriPhoto!=null){
                            //이미지를 파이어베이스 스토리지에 업로드
                            // Create a storage reference from our app
                            val storageRef = storage.reference
                            val file = uriPhoto
                            val riversRef = storageRef.child("usersProfile/${email}")
                            val uploadTask = riversRef.putFile(file!!)
                            uploadTask.addOnFailureListener {
                                // Handle unsuccessful uploads
                            }.addOnSuccessListener { taskSnapshot ->
                                photoIs= "o"
                                dbSetting(photoIs,name,nickName,email)
                                Log.d(Tag,"profile Img Uri1= "+photoIs);
                            }

                        }


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.v(Tag , " 이메일 가입 실패_"+"createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_LONG).show()
                        finish()

                    }
                }
                task.addOnFailureListener {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                }
            }


        }
    }

    private fun dbSetting(photoIs: String?, name: String, nickName: String, email: String) {
        //가입정보 db저장(이메일)
        val profile = hashMapOf(
            "name" to name,
            "nickName" to nickName,
            "enterPrise" to "x",
            "Photo" to photoIs
        )
        Log.d(Tag,"profile Img Uri2= "+photoIs);
        fbsconnect.db.collection("User").document(email)
            .set(profile)
            .addOnSuccessListener {
                Log.d(
                    Tag,
                    "DocumentSnapshot successfully written!"
                )
            }
            .addOnFailureListener { e -> Log.w(Tag, "Error writing document", e) }
        // Sign in success, update UI with the signed-in user's information
        Log.v(TAG + " 이메일 가입 성공", "createUserWithEmail:success")
        Toast.makeText(baseContext, "회원가입 성공", Toast.LENGTH_SHORT).show()
        finish()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageFromAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                //Path for the selected image
                uriPhoto = data?.data
                profileImg.setImageURI(uriPhoto)


            }
        }
    }
}
