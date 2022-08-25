package com.example.anywhere;

import static android.content.ContentValues.TAG;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.anywhere.databinding.ActivityCommunityBinding;
import com.example.anywhere.databinding.ActivityDetailPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DetailPostActivity extends AppCompatActivity {


    private String DocId;
    private firebaseConnect fbsconnect;
    private ActivityDetailPostBinding binding;
    
    private RecyclerView recyclerView;
    private MyCommnetAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> cUser,myTime,comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();

        //글 세팅
        Intent secondIntent = getIntent();
        DocId=secondIntent.getStringExtra("DocId");
        dbSetting();


        //댓글세팅
        mAdapter=new MyCommnetAdapter();
        recyclerView = binding.recycleview;
        //리사이클러뷰 경계선
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        // 리사이클러뷰 사이즈 고정
        recyclerView.setHasFixedSize(true);
        // LinearLayoutManager 로 리사이클러뷰의 세팅을 변경할 수 있다 ex) 가로로 만들기
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cUser= new ArrayList<>();
        myTime= new ArrayList<>();
        comment= new ArrayList<>();
        commentSetting();
    
        //로그인 시에만 글쓰기 기능 가능
        fbsconnect.firbaseInit();
        FirebaseUser user = fbsconnect.fb_user();

        if (user != null) {
            binding.commentET.setVisibility(VISIBLE);
            binding.commentPostBtn.setVisibility(VISIBLE);
        } else {
            binding.commentET.setVisibility(View.INVISIBLE);
            binding.commentPostBtn.setVisibility(View.INVISIBLE);
        }


        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRestart();
                binding.refresh.setRefreshing(false);
            }
        });

    }
    
    //코멘트 게시
    public void commentPOst(View view) {
        if(binding.commentET.getText().toString().getBytes().length <= 0){
            Toast.makeText(this.getApplicationContext(),"제목/본문을 입력해주세요.",Toast.LENGTH_LONG).show();
        }
        else{
            dbCommentAdd();
        }
    }

    //해당 글 불러오기
    void dbSetting(){
        DocumentReference docRef = fbsconnect.db.collection("post").document(DocId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        binding.userid.setText(String.valueOf(document.get("user")));
                        binding.posttime.setText(String.valueOf(document.get("time")));
                        binding.postName.setText(String.valueOf(document.get("name")));
                        binding.postBody.setText(String.valueOf(document.get("body")));

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    //댓글 추가
    void dbCommentAdd(){
        Map<String, Object> user = new HashMap<>();
        user.put("comment", binding.commentET.getText().toString());
        user.put("user", fbsconnect.fb_userEmail());
        user.put("time",getTime());

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
    void commentSetting(){
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
                                String cmt= String.valueOf(document.get("comment"));
                                cUser.add(0,user);
                                myTime.add(0,time);
                                comment.add(0,cmt);
                            }

                            // 어댑터 세팅
                            mAdapter = new MyCommnetAdapter(cUser,myTime,comment);
                            recyclerView.setAdapter(mAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    public void closeBtn(View view) {
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cUser.clear();
        myTime.clear();
        comment.clear();
        commentSetting();

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