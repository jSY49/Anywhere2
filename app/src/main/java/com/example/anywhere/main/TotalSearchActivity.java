package com.example.anywhere.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    private RecyclerView recyclerView1;
    private ListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    int pageNum = 0, totalCntInteger = 0, tmpButton = 1;
    ArrayList<ItemList> List;
    String keyWord,totalCnt = "0", page = "1";
    EditText search_edit;
    RelativeLayout layout;
    LinearLayout innerLinear;
    TextView searchCount;
    final String cIdTrip = "12", cIdFood = "39", cIdFestival = "15", cidAccom = "32";
    private String Tag = "TotalSearchActivity";
    int count;
    CustomProgressDialog customProgressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_search);

        innerLinear=findViewById(R.id.innerLinear);
        searchCount=findViewById(R.id.searchCount);

        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        search_edit = findViewById(R.id.SearchEditText);
        layout = findViewById(R.id.layout);

        List = new ArrayList<>();
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
                        runthread(recyclerView1);

                        hideKeyboard();
                    }

                    return true;
                }
                return false;
            }
        });
    }

    private void runthread(RecyclerView recycler) {
        customProgressDialog.show();
        TourApi_ tourapi_listCount = new TourApi_("searchKeyword");
        tourapi_listCount.set_dataList_Count_URL(keyWord);
        String get_Url_listCount = tourapi_listCount.getUrl();

        TourApi_ tourapi = new TourApi_("searchKeyword");
        tourapi.set_totalList_URL(keyWord);
        String get_Url = tourapi.getUrl();

        new Thread(() -> {
            get_listCount(get_Url_listCount);
            totalCntInteger = Integer.parseInt(totalCnt);
            if (totalCntInteger < 50) {
                pageNum = 1;
            } else if (totalCntInteger >= 50) {
                pageNum = totalCntInteger / 50;
                if (totalCntInteger % 50 != 0) {
                    pageNum += 1;
                }
            }

            Log.d(Tag+"_pageNum : " , String.valueOf(pageNum));
            try {
                get_Xml(get_Url);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                searchCount.setText("\""+keyWord+"\""+" 검색결과("+totalCntInteger+")");
                addButton();

                mAdapter = new ListAdapter(List, getApplicationContext());
                recycler.setAdapter(mAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

                customProgressDialog.dismiss();
            });


        }).start();
        List.clear();
    }
    void get_listCount(String Url) {
        try {
            Log.d(Tag+"_totalCount: ", Url);
            URL url = new URL(Url);//문자열로 된 요청 url을 URL 객체로 생성.

            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, StandardCharsets.UTF_8)); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//태그 이름 얻어오기

                        switch (tag) {
                            case "item":
                                break;
                            case "totalCnt":
                                xpp.next();
                                totalCnt = xpp.getText();
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
                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
            e.printStackTrace();
        }

    }
    public void addButton() {
        if (tmpButton > 1) {
            innerLinear.removeAllViews();
            tmpButton = 1;
        }

        for (int i = 0; i < pageNum; i++) {
            Button pagebutton = new Button(this);
            pagebutton.setText(String.valueOf(i + 1));
            if (String.valueOf(tmpButton).equals(page)) {
                Log.d(Tag + "_tmpBtn", String.valueOf(tmpButton));
                Log.d(Tag + "_page", page);
                pagebutton.setTextColor(Color.BLACK);
                pagebutton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            } else {
                pagebutton.setTextColor(Color.parseColor("#FF6E6E6E"));
            }

            pagebutton.setBackgroundColor(Color.parseColor("#00ff0000"));
            pagebutton.setWidth(10);
//            pagebutton.setId(DYNAMIC_VIEW_ID+i);
            pagebutton.setTag(i + 1);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = 80;
            params.setMargins(10, 0, 10, 0);
            innerLinear.addView(pagebutton, params);
            pagebutton.setOnClickListener(view -> {
                Log.d(Tag + "_pagebuttonId_Click", String.valueOf(pagebutton.getTag()));
                page = String.valueOf(pagebutton.getTag());
//                adapter.clearAll();
//                //runthread_pageNum();
                runthread(recyclerView1);
            });
            tmpButton++;

        }


    }

    void get_Xml(String newUrl) throws IOException {

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
                    List.add(new ItemList(title, imgurl, addr, id));
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
            runthread(recyclerView1);

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

        recyclerView1 = findViewById(R.id.Recycler);
        recyclerView1.addItemDecoration(decoration_height);
        DividerItemDecoration dividerItemDecoration1 = new DividerItemDecoration(recyclerView1.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView1.addItemDecoration(dividerItemDecoration1);
        recyclerView1.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager);

        runthread(recyclerView1);

    }

    void hideKeyboard() {

        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isAcceptingText()) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }


    }
}