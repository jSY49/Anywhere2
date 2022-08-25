package com.example.anywhere;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anywhere.databinding.ActivityPostwritingBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class postWritingActivity extends AppCompatActivity {


    private @NonNull
    ActivityPostwritingBinding binding;
    private firebaseConnect fbsconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostwritingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fbsconnect=new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();



    }


    //액티비티 종료 버튼
    public void closeBtn(View view){
        finish();
    }

    //완료(게시)버튼
    public void POST(View view){
        if(binding.postName.getText().toString().getBytes().length <= 0||
                binding.postBody.getText().toString().getBytes().length <= 0){
            Toast.makeText(this.getApplicationContext(),"제목/본문을 입력해주세요.",Toast.LENGTH_LONG).show();
        }
        else{
            dbAdd();
        }


    }

    //사진 추가 버튼
    public void getPhoto(View view){

    }

    private void dbAdd(){

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("name", binding.postName.getText().toString());
        user.put("body", binding.postBody.getText().toString());
        user.put("user", fbsconnect.fb_userEmail());
        user.put("time",getTime());
//        user.put("img", );

// Add a new document with a generated ID
        fbsconnect.db.collection("post")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private String getTime(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;

    }

}
