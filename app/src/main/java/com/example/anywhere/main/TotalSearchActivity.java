package com.example.anywhere.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anywhere.Connect.TourApi_;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class TotalSearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView1, recyclerView2, recyclerView3, recyclerView4;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<ItemList> tourList, foodList, festivalList, AccomList;
    String keyWord;
    EditText search_edit;
    RelativeLayout layout;
    TextView trip_non_text, food_non_text, festival_non_text, accom_non_text;
    final String cIdTrip = "12", cIdFood = "39", cIdFestival = "15", cidAccom = "32";
    private String Tag = "TotalSearchActivity";
    int count;
    CustomProgressDialog customProgressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_search);

        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        search_edit = findViewById(R.id.SearchEditText);
//        recyclerView1 = findViewById(R.id.tripRecycler);
//        recyclerView2 = findViewById(R.id.foodRecycler);
//        recyclerView3 = findViewById(R.id.festivalRecycler);
//        recyclerView4 = findViewById(R.id.accommodationsRecycler);
        layout = findViewById(R.id.layout);
        trip_non_text = findViewById(R.id.trip_non_text);
        food_non_text = findViewById(R.id.food_non_text);
        festival_non_text = findViewById(R.id.festival_non_text);
        accom_non_text = findViewById(R.id.accommodations_non_text);


        tourList = new ArrayList<>();
        foodList = new ArrayList<>();
        festivalList = new ArrayList<>();
        AccomList = new ArrayList<>();

        mAdapter = new ListAdapter();

        Intent secondIntent = getIntent();
        keyWord = secondIntent.getStringExtra("keyWord");
        search_edit.setText(keyWord);

        RecyclerViewSetting();


        //키보드 밖 클릭 시 키보드 숨김
        layout.setOnTouchListener((v, event) -> {
            try {
                if(search_edit.requestFocus()){
                    hideKeyboard();
                }
            } catch (Exception e) {
                Log.d(Tag, "layTouchException", e);
            }
            return false;
        });

        //엔터키 이벤트 처리
        search_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    keyWord = search_edit.getText().toString();
                    if (keyWord.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "검색어를 입력해 주세요", Toast.LENGTH_LONG).show();
                    } else {
                        runthread(cIdTrip, recyclerView1);
                        runthread(cIdFood, recyclerView2);
                        runthread(cIdFestival, recyclerView3);
                        runthread(cidAccom, recyclerView4);
                        hideKeyboard();
                    }

                    return true;
                }
                return false;
            }
        });
    }

    private void runthread(String cIdType, RecyclerView recycler) {
        customProgressDialog.show();
        trip_non_text.setVisibility(View.INVISIBLE);
        food_non_text.setVisibility(View.INVISIBLE);
        festival_non_text.setVisibility(View.INVISIBLE);
        accom_non_text.setVisibility(View.INVISIBLE);

        TourApi_ tourapi = new TourApi_("searchKeyword");
        tourapi.set_totalList_URL(cIdType, keyWord);
        String get_Url = tourapi.getUrl();

        new Thread(() -> {

            try {
                get_Xml(get_Url, cIdType);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                switch (cIdType) {
                    case "12":
                        mAdapter = new ListAdapter(tourList, getApplicationContext());
                        break;
                    case "39":
                        mAdapter = new ListAdapter(foodList, getApplicationContext());
                        break;
                    case "15":
                        mAdapter = new ListAdapter(festivalList, getApplicationContext());
                        break;
                    case "32":
                        mAdapter = new ListAdapter(AccomList, getApplicationContext());
                        break;
                }

                recycler.setAdapter(mAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

                count = mAdapter.getItemCount();
                Log.d(Tag, "recyclerView Count=" + count);
                if (count == 0) {
                    switch (cIdType) {
                        case "12":
                            trip_non_text.setVisibility(View.VISIBLE);
                            break;
                        case "39":
                            food_non_text.setVisibility(View.VISIBLE);
                            break;
                        case "15":
                            festival_non_text.setVisibility(View.VISIBLE);
                            break;
                        case "32":
                            accom_non_text.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                customProgressDialog.dismiss();
            });


        }).start();
        tourList.clear();
        foodList.clear();
        festivalList.clear();
        AccomList.clear();
    }

    void get_Xml(String newUrl, String cid) throws IOException {

        try {
            URL url = new URL(newUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            Log.d(Tag, "url=" + url);
            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, StandardCharsets.UTF_8)); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();
            String imgurl = null, title = null, addr = null, id = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//태그 이름 얻어오기

                        switch (tag) {
                            case "item":
                                break;
                            case "firstimage":
                                xpp.next();
                                imgurl = xpp.getText();
                                break;
                            case "title":
                                xpp.next();
                                title = xpp.getText();
                                break;
                            case "addr1":
                                xpp.next();
                                addr = xpp.getText();
                                break;
                            case "contentid":
                                xpp.next();
                                id = xpp.getText();
                                break;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기

                        if (tag.equals("item")) //buffer.append("\n");// 첫번째 검색결과종료..줄바꿈

                            break;

                }

                if (imgurl != null && title != null && addr != null && id != null) {
                    switch (cid) {
                        case "12":
                            tourList.add(new ItemList(title, imgurl, addr, id));
                            break;
                        case "39":
                            foodList.add(new ItemList(title, imgurl, addr, id));
                            break;
                        case "15":
                            festivalList.add(new ItemList(title, imgurl, addr, id));
                            break;
                        case "32":
                            AccomList.add(new ItemList(title, imgurl, addr, id));
                            break;
                    }

                    imgurl = null;
                    title = null;
                    addr = null;
                    id = null;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
            e.printStackTrace();
        }

    }

    public void backBtn(View view) {
        finish();
    }

    public void search(View view) {
        keyWord = search_edit.getText().toString();

        if (keyWord.isEmpty()) {
            Toast.makeText(getApplicationContext(), "검색어를 입력해 주세요", Toast.LENGTH_LONG).show();
        } else {
            runthread(cIdTrip, recyclerView1);
            runthread(cIdFood, recyclerView2);
            runthread(cIdFestival, recyclerView3);
            runthread(cidAccom, recyclerView4);

            hideKeyboard();

        }

    }

    public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {

        private final int divWidth;

        public RecyclerViewDecoration(int divWidth) {
            this.divWidth = divWidth;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.right = divWidth;
        }
    }

    public void RecyclerViewSetting() {
        RecyclerViewDecoration decoration_height = new RecyclerViewDecoration(15);

        recyclerView1 = findViewById(R.id.tripRecycler);
        recyclerView1.addItemDecoration(decoration_height);
        DividerItemDecoration dividerItemDecoration1 = new DividerItemDecoration(recyclerView1.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView1.addItemDecoration(dividerItemDecoration1);
        recyclerView1.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager);


        recyclerView2 = findViewById(R.id.foodRecycler);
        recyclerView2.addItemDecoration(decoration_height);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerView2.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView2.addItemDecoration(dividerItemDecoration2);
        recyclerView2.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(layoutManager);

        recyclerView3 = findViewById(R.id.festivalRecycler);
        recyclerView3.addItemDecoration(decoration_height);
        DividerItemDecoration dividerItemDecoration3 = new DividerItemDecoration(recyclerView3.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView3.addItemDecoration(dividerItemDecoration3);
        recyclerView3.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView3.setLayoutManager(layoutManager);

        recyclerView4 = findViewById(R.id.accommodationsRecycler);
        recyclerView4.addItemDecoration(decoration_height);
        DividerItemDecoration dividerItemDecoration4 = new DividerItemDecoration(recyclerView4.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView4.addItemDecoration(dividerItemDecoration4);
        recyclerView4.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView4.setLayoutManager(layoutManager);

        runthread(cIdTrip, recyclerView1);
        runthread(cIdFood, recyclerView2);
        runthread(cIdFestival, recyclerView3);
        runthread(cidAccom, recyclerView4);
    }

    void hideKeyboard() {

        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isAcceptingText()) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }


    }
}