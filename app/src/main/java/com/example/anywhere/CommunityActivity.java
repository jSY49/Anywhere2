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
import com.example.anywhere.databinding.ActivityCommunityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    private ActivityCommunityBinding binding;
    private firebaseConnect fbsconnect;

//    PagerAdapter adapter;
//    ViewPager viewPager;

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String>  mydb,myTime,docname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommunityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAdapter=new MyAdapter();

        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();
        recyclerView = binding.recycle1;

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // 리사이클러뷰 사이즈 고정
        recyclerView.setHasFixedSize(true);

        // LinearLayoutManager 로 리사이클러뷰의 세팅을 변경할 수 있다 ex) 가로로 만들기
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        
        mydb= new ArrayList<>();
        myTime= new ArrayList<>();
        docname= new ArrayList<>();

        dbsetting();

        //로그인 시에만 글쓰기 기능 가능
        fbsconnect.firbaseInit();
        FirebaseUser user = fbsconnect.fb_user();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        if (user != null) {
            fab.setVisibility(VISIBLE);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), postWritingActivity.class);
                startActivity(intent);

            }
        });

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRestart();
                binding.refresh.setRefreshing(false);
            }
        });

    }

    void dbsetting(){
        fbsconnect.db.collection("post")
                .orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String title = String.valueOf(document.get("name"));
                                String time = String.valueOf(document.get("time"));
                                String docNm= String.valueOf(document.getId());
                                mydb.add(0,title);
                                myTime.add(0,time);
                                docname.add(0,docNm);
                            }

                            // 어댑터 세팅
                            mAdapter = new MyAdapter(mydb,myTime,docname);
                            recyclerView.setAdapter(mAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        mydb.clear();
        myTime.clear();
        docname.clear();
        dbsetting();
    }

    //키워드 검색
    public void search(View view) {


    }
}









//        TabLayout tabs=binding.tabs;
//        tabs.addTab(tabs.newTab().setText(R.string.tabpost));
//        tabs.addTab(tabs.newTab().setText(R.string.tabReview));
//        tabs.addTab(tabs.newTab().setText(R.string.tabUser));
//        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
//
//        //viewPager에 adapter set, tabLayout Listener 선언
//        adapter = new PagerAdapter(getSupportFragmentManager(),tabs.getTabCount());
//        viewPager=binding.viewPager;
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
//
//
//        //Tab 이벤트에 대한 listener
//        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
