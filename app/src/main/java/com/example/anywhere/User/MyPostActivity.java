package com.example.anywhere.User;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anywhere.Community.MyAdapter;
import com.example.anywhere.Connect.firebaseConnect;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyPostActivity extends AppCompatActivity {

    String Tag="MyPostActivity";

    private firebaseConnect fbsconnect;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> mydb,myTime,docname,img;
    String MyEmial;
    TextView postIsEmpty;
    int count;
    CustomProgressDialog customProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        postIsEmpty=findViewById(R.id.emptyPosT_Text);
        recyclerView = findViewById(R.id.myPostRecycler);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mydb= new ArrayList<>();
        myTime= new ArrayList<>();
        docname= new ArrayList<>();
        img=new ArrayList<>();

        mAdapter=new MyAdapter();


        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();
        MyEmial=fbsconnect.fb_userEmail();
        dbsetting();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        mydb.clear();
        myTime.clear();
        docname.clear();
        dbsetting();
    }
    public void backBtn(View view) {    finish();   }

    void dbsetting(){
        customProgressDialog.show();
        fbsconnect.db.collection("post")
                .orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(document.get("user").equals(MyEmial)){
                                    String title = String.valueOf(document.get("name"));
                                    String time = String.valueOf(document.get("time"));
                                    String docNm= document.getId();
                                    String imgcheck=String.valueOf(document.get("img"));
                                    mydb.add(0,title);
                                    myTime.add(0,time);
                                    docname.add(0,docNm);
                                    img.add(0,imgcheck);
                                    Log.d("Img",imgcheck);
                                }

                            }

                            // 어댑터 세팅
                            mAdapter = new MyAdapter(mydb,myTime,docname,img);
                            recyclerView.setAdapter(mAdapter);

//                            count=mydb.size();
                            count=mAdapter.getItemCount();
                            Log.d(Tag,"recyclerView Count="+count);
                            if(count==0){
                                postIsEmpty.setVisibility(View.VISIBLE);
                            }

                            customProgressDialog.dismiss();
                        } else {

                            Log.d(Tag, "Error getting documents: ", task.getException());
                        }
                    }

                });




    }
    


}