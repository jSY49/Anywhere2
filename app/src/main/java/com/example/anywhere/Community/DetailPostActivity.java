package com.example.anywhere.Community;

import static android.content.ContentValues.TAG;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.anywhere.Connect.firebaseConnect;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.databinding.ActivityDetailPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DetailPostActivity extends AppCompatActivity {


    private String DocId,userEmail;
    private firebaseConnect fbsconnect;
    private ActivityDetailPostBinding binding;

    private RecyclerView recyclerView;
    private MyCommnetAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> cUser, myTime, comment;

    ArrayList<Uri> uriList;
    RecyclerView recyclerView_img;  // 이미지를 보여줄 리사이클러뷰
    MyPostImgAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터
    String img;
    FirebaseUser user;
    CustomProgressDialog customProgressDialog;
    public void backBtn(View view) {    finish();
    }


    public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {

        private final int divWidth;

        public RecyclerViewDecoration(int divWidth)
        {
            this.divWidth = divWidth;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.right = divWidth;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();
        fbsconnect.firebaseStorageInit();

        //로그인 시에만 글쓰기 기능 가능
        fbsconnect.firbaseInit();
        user = fbsconnect.fb_user();

        //글 세팅
        Intent secondIntent = getIntent();
        DocId = secondIntent.getStringExtra("DocId");
        dbSetting();

        //img 세팅
        uriList = new ArrayList<>();
        recyclerView_img = binding.recyclerViewImg;
        //간격 세팅
        RecyclerViewDecoration decoration_height = new RecyclerViewDecoration(15);
        recyclerView_img.addItemDecoration(decoration_height);


        //댓글세팅
        mAdapter = new MyCommnetAdapter();
        recyclerView = binding.recycleview;
        //리사이클러뷰 경계선
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        // 리사이클러뷰 사이즈 고정
        recyclerView.setHasFixedSize(true);
        // LinearLayoutManager 로 리사이클러뷰의 세팅을 변경할 수 있다 ex) 가로로 만들기
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cUser = new ArrayList<>();
        myTime = new ArrayList<>();
        comment = new ArrayList<>();
        commentSetting();



        if (user != null) {
            binding.commentET.setVisibility(VISIBLE);
            binding.commentPostBtn.setVisibility(VISIBLE);
        } else {
            binding.commentET.setVisibility(INVISIBLE);
            binding.commentPostBtn.setVisibility(INVISIBLE);
        }


        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRestart();
                binding.refresh.setRefreshing(false);
            }
        });



    }
    //글 삭제
    public void deletePostBtn(View view) {
        customProgressDialog.show();
        fbsconnect.db.collection("post").document(DocId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(getApplicationContext(),"내 글이 삭제 되었습니다.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(getApplicationContext(),"내 글이 삭제 되었습니다.",Toast.LENGTH_LONG).show();
                    }
                });

        customProgressDialog.dismiss();
    }
    //코멘트 게시
    public void commentPOst(View view) {
        if (binding.commentET.getText().toString().getBytes().length <= 0) {
            Toast.makeText(this.getApplicationContext(), "코멘트를 입력해주세요.", Toast.LENGTH_LONG).show();
        } else {
            dbCommentAdd();
            binding.commentET.setText("");
        }
    }

    //해당 글 불러오기
    void dbSetting() {
        customProgressDialog.show();
        DocumentReference docRef = fbsconnect.db.collection("post").document(DocId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userEmail=String.valueOf(document.get("user"));
                        binding.userid.setText(String.valueOf(document.get("user")));
                        binding.posttime.setText(String.valueOf(document.get("time")));
                        binding.postName.setText(String.valueOf(document.get("name")));
                        binding.postBody.setText(String.valueOf(document.get("body")));
                        img = String.valueOf(document.get("img"));
                        if (img.equals("o")) {
                            loadImg();
                        }

                        if(userEmail.equals(fbsconnect.fb_userEmail())){
//                            binding.modifyBtn.setVisibility(VISIBLE);
                            binding.deleteBtn.setVisibility(VISIBLE);
                        }else{
//                            binding.modifyBtn.setVisibility(INVISIBLE);
                            binding.deleteBtn.setVisibility(INVISIBLE);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }

                    customProgressDialog.dismiss();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



    }

    //댓글 추가
    void dbCommentAdd() {
        Map<String, Object> user = new HashMap<>();
        user.put("comment", binding.commentET.getText().toString());
        user.put("user", fbsconnect.fb_userEmail());
        user.put("time", getTime());

// Add a new document with a generated ID
        fbsconnect.db.collection("post").document(DocId).collection("comment")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        onRestart();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    //코멘트 불러오기
    void commentSetting() {
        fbsconnect.db.collection("post").document(DocId).collection("comment")
                .orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String user = String.valueOf(document.get("user"));
                                String time = String.valueOf(document.get("time"));
                                String cmt = String.valueOf(document.get("comment"));
                                cUser.add(0, user);
                                myTime.add(0, time);
                                comment.add(0, cmt);
                            }

                            // 어댑터 세팅
                            mAdapter = new MyCommnetAdapter(cUser, myTime, comment);
                            recyclerView.setAdapter(mAdapter);
                            hideKeyboard();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }
    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.recyclerView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void loadImg() {

        try {
            StorageReference storageRef = fbsconnect.storage.getReference();
            StorageReference pathReference = storageRef.child("Postimages/" + DocId);

            pathReference.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {

                            for (StorageReference item : listResult.getItems()) {
                                item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            uriList.add(task.getResult());

                                        } else {
                                            // URL을 가져오지 못하면 토스트 메세지
                                            Toast.makeText(DetailPostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        Log.d("uriList", String.valueOf(uriList.size()));
                                        adapter = new MyPostImgAdapter(uriList, getApplicationContext());
                                        recyclerView_img.setAdapter(adapter);
                                        recyclerView_img.setLayoutManager(new LinearLayoutManager(DetailPostActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
                            }


                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Uh-oh, an error occurred!
                        }
                    });
        } catch (NullPointerException e) {
            Log.d("NullPointerException", String.valueOf(e));
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cUser.clear();
        myTime.clear();
        comment.clear();
        commentSetting();

    }

    private String getTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;

    }


}