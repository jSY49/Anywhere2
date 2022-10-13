package com.example.anywhere.Connect


import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class firebaseConnect {

    lateinit var auth: FirebaseAuth
    lateinit var db:   FirebaseFirestore
    lateinit var storage: FirebaseStorage


    fun firbaseInit(): FirebaseAuth {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        return auth
    }
    fun fb_email_signUp(email:String,password:String): Task<AuthResult> {

        return auth.createUserWithEmailAndPassword(email, password)
    }
    fun fb_email_login(email:String,password:String): Task<AuthResult> {

        return auth.signInWithEmailAndPassword(email,password)
    }
    fun fb_user(): FirebaseUser? {
        val user = Firebase.auth.currentUser
        return user
    }
    fun fb_uid(): String? {
        val user = Firebase.auth.getUid()
        return user
    }

    fun fb_userEmail(): String? {
        val user = Firebase.auth.currentUser
        val Uemail=user?.email
        return Uemail
    }
//    fun fb_logout():Unit{
//        auth.signOut()
//    }

    fun firbaseDBInit(): FirebaseFirestore {
        db = FirebaseFirestore.getInstance()
        return db
    }

    fun firebaseStorageInit():FirebaseStorage{
        storage= FirebaseStorage.getInstance()
        return storage
    }

}
