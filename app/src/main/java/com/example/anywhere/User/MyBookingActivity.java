package com.example.anywhere.User;

import android.content.Intent;
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

import com.example.anywhere.Connect.firebaseConnect;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyBookingActivity extends AppCompatActivity {
    String Tag="MyBookingActivity";

    private firebaseConnect fbsconnect;
    private RecyclerView recyclerView;
    private MyBookingAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> mydb,myTime,pcode,enterProduct;
    String MyEmial,key;
    TextView postIsEmpty;
    int count;
    CustomProgressDialog customProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        postIsEmpty=findViewById(R.id.emptyBooking_Text);
        recyclerView = findViewById(R.id.myBookingRecycler);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent secondIntent = getIntent();
        key = secondIntent.getStringExtra("key");
        Log.d(Tag,"key="+key);


        mydb= new ArrayList<>();
        myTime= new ArrayList<>();
        pcode=new ArrayList<>();
        mAdapter=new MyBookingAdapter();

        enterProduct=new ArrayList<>();

        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();
        MyEmial=fbsconnect.fb_userEmail();
        switch (key){
            case "UserBooking": dbsetting();    break;
            case "enterprise" : dbsetting_enterprise(); break;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        mydb.clear();
        myTime.clear();
        pcode.clear();
        dbsetting();
    }
    public void backBtn(View view) {    finish();   }

    void dbsetting(){
        customProgressDialog.show();
        fbsconnect.db.collection("bookingIdList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(document.get("email").equals(MyEmial)){
                                    String itemName = String.valueOf(document.get("ItemName"));
                                    String time = String.valueOf(document.get("time"));
                                    String date = String.valueOf(document.get("date"));
                                    String code= String.valueOf(document.getId());
                                    mydb.add(0,itemName);
                                    myTime.add(0,date+time);
                                    pcode.add(0,code);
                                }

                            }

                            // 어댑터 세팅
                            mAdapter = new MyBookingAdapter(mydb,myTime,pcode);
                            recyclerView.setAdapter(mAdapter);

//                            count=mydb.size();
                            count=mAdapter.getItemCount();
                            Log.d(Tag,"recyclerView Count="+count);
                            if(count==0){
                                postIsEmpty.setVisibility(View.VISIBLE);
                            }

                        } else {

                            Log.d(Tag, "Error getting documents: ", task.getException());
                        }

                        customProgressDialog.dismiss();
                    }

                });

    }

    //사업자용DB
    void dbsetting_enterprise(){
        //상품 리스트 가져와
        fbsconnect.db.collection("User").document(MyEmial).collection("product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                enterProduct.add(document.getId());
                            }
                        } else {
                            Log.d(Tag, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //업체의 모든 예약 리스트 가져오기
        fbsconnect.db.collection("bookingIdList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for(int i=0;i<enterProduct.size();i++){
                                    if(document.get("productCode").equals(enterProduct.get(i))){
                                        String itemName = String.valueOf(document.get("ItemName"));
                                        String time = String.valueOf(document.get("time"));
                                        String date = String.valueOf(document.get("date"));
                                        String code= String.valueOf(document.getId());
                                        mydb.add(0,itemName);
                                        myTime.add(0,date+time);
                                        pcode.add(0,code);
                                    }
                                }
                            }

                            // 어댑터 세팅
                            mAdapter = new MyBookingAdapter(mydb,myTime,pcode);
                            recyclerView.setAdapter(mAdapter);

//                            count=mydb.size();
                            count=mAdapter.getItemCount();
                            Log.d(Tag,"recyclerView Count="+count);
                            if(count==0){
                                postIsEmpty.setVisibility(View.VISIBLE);
                            }

                        } else {

                            Log.d(Tag, "Error getting documents: ", task.getException());
                        }
                    }

                });


    }
}