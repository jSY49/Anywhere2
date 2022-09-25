package com.example.anywhere;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anywhere.databinding.ActivityPostwritingBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class postWritingActivity extends AppCompatActivity {


    private ActivityPostwritingBinding binding;
    private firebaseConnect fbsconnect;
    private final int GALLERY_CODE=10;
    ImageView photo;
    Map<String, Object> user;
    int num=0,permission,permission2 ;
    ArrayList<Uri> uriList;
    RecyclerView recyclerView;  // 이미지를 보여줄 리사이클러뷰
    MyPostImgAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터
    String userHashName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostwritingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fbsconnect=new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();
        fbsconnect.firebaseStorageInit();


        user = new HashMap<>();
        uriList = new ArrayList<>();
        recyclerView=binding.recyclerView;

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

        // 권한ID를 가져옵니다
        permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1000);
            }
            return;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == true) {

            } else {
                loadAlbum();
            }
        }
    }


    private void dbAdd(){


        user.put("name", binding.postName.getText().toString());
        user.put("body", binding.postBody.getText().toString());
        user.put("user", fbsconnect.fb_userEmail());
        user.put("time",getTime());

        try {
            if(uriList.get(0)!=null){ user.put("img","o");}
        }catch (IndexOutOfBoundsException e){
            Log.d("Error", String.valueOf(e));
            user.put("img","x");
        }



        // Add a new document with a generated ID
        fbsconnect.db.collection("post")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    userHashName= documentReference.getId();
                    //Img storage
                    if(uriList.size()>0){

                        storageUproad(uriList,userHashName);
                    }

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));


        finish();
    }

    private String getTime(){

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 0);

        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(date);

    }

    //갤러리 실행 함수
    private void loadAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                adapter = new MyPostImgAdapter(uriList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }

                    adapter = new MyPostImgAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));     // 리사이클러뷰 수평 스크롤 적용
                }
            }
        }


    }
    private void storageUproad(ArrayList<Uri>ImgList,String hashId){

        // Create a storage reference from our app
        StorageReference storageRef = fbsconnect.storage.getReference();
        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("mountains.jpg");
        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");
        // While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
        StorageReference riversRef;
        UploadTask uploadTask = null;


        for(int i=0;i<ImgList.size();i++){
            Uri file = ImgList.get(i);
            riversRef= storageRef.child("Postimages/"+hashId+"/"+file.getLastPathSegment());
            //file.getLastPathSegment()
            uploadTask = riversRef.putFile(file);
        }
        try {
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }catch (NullPointerException e){
            Log.d("Img Null","_null");
        }


    }
}
