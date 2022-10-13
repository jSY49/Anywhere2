package com.example.anywhere.Festival;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.anywhere.R;
import com.example.anywhere.Connect.TourApi_;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ThisMonthFestivalActivity extends AppCompatActivity {

    ArrayAdapter<CharSequence> adspin1, adspin2, adMonthspin;
    Spinner spin1,spin2,monthSpin;
    String sp1 = "1", sp2 = "-1",totalCnt = "0", page = "1";

    int pageNum = 0, totalCntInteger = 0, tmpButton = 1;
    LinearLayout innerLinear;

    private RecyclerView recyclerView1;
    private FestivalAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<FestivalList> list;
    String startDateStr,endDateStr;
    int startDateInt,endDateInt;
    int monthcheck;
    int mon;

    private String Tag="ThisMonthFestivalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_this_month_festival);

        innerLinear=findViewById(R.id.innerLinear);
        list=new ArrayList<>();
        monthcheck=Integer.parseInt(getDate())%100;
        AddSpinner();
        RecyclerViewSetting();

    }

    public void SearchBtn(View view) {
        runthread();
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
    private void RecyclerViewSetting() {
        recyclerView1=findViewById(R.id.recyclerView);
        //간격
        RecyclerViewDecoration decoration_height = new RecyclerViewDecoration(15);
        recyclerView1.addItemDecoration(decoration_height);
        //경계선
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView1.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView1.addItemDecoration(dividerItemDecoration);
        // 리사이클러뷰 사이즈 고정
        recyclerView1.setHasFixedSize(true);
        // LinearLayoutManager 로 리사이클러뷰의 세팅을 변경할 수 있다 ex) 가로로 만들기
        layoutManager = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager);

    }

    private void AddSpinner() {

        spin1 = (Spinner) findViewById(R.id.spinner1);
        spin2 = (Spinner) findViewById(R.id.spinner2);
        monthSpin = (Spinner) findViewById(R.id.MonthSpinner);

        adspin1 = ArrayAdapter.createFromResource(this, R.array.firstSelect, android.R.layout.simple_spinner_dropdown_item);//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adspin1);
        adspin2 = ArrayAdapter.createFromResource(this, R.array.firstSelect, android.R.layout.simple_spinner_dropdown_item);//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양
        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adspin2);

        adMonthspin = ArrayAdapter.createFromResource(this, R.array.month, android.R.layout.simple_spinner_dropdown_item);//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양
        adMonthspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpin.setAdapter(adMonthspin);
        Log.d(Tag,"nowMonth="+monthcheck);
        monthSpin.setSelection(monthcheck-1);


        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                scndSpinner(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0)
                    sp2 = String.valueOf(-1);
                else
                    sp2 = String.valueOf(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        monthSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                MonthSpinner(i);
                getMonth();
                runthread();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    private void getMonth() {
        //스피너에서 나온 달을 계산해서 시작 끝 날짜 지정 되도록 수정

        int nowYearInt=Integer.parseInt(getDate())/100;

        startDateInt=(nowYearInt*100+mon)*100+1;
        startDateStr=String.valueOf(startDateInt);

        switch(mon){
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                endDateInt=(nowYearInt*100+mon)*100+31;
                break;
            case 4: case 6: case 9: case 11:
                endDateInt=(nowYearInt*100+mon)*100+30;
                break;
            case 2:
                endDateInt=(nowYearInt*100+mon)*100+28;
                break;
        }

        endDateStr=String.valueOf(endDateInt);

        Log.d(Tag,"startDate_"+startDateStr);
        Log.d(Tag, "endDate_"+endDateStr);

    }

    public void btnBack(View view) {
        finish();
    }

    public void scndSpinner(int i) {

        int whichone = R.array.second_seoul;  //기본
        switch (i) {
            case 0: whichone = R.array.second_seoul;    sp1 = "1";  break;
            case 1: whichone = R.array.second_incheon;  sp1 = "2";  break;
            case 2: whichone = R.array.second_daejeon;  sp1 = "3";  break;
            case 3: whichone = R.array.second_daegu;    sp1 = "4";  break;
            case 4: whichone = R.array.second_guangju;  sp1 = "5";  break;
            case 5: whichone = R.array.second_busan;    sp1 = "6";  break;
            case 6: whichone = R.array.second_ulsan;    sp1 = "7";  break;
            case 7: whichone = R.array.second_sejong;   sp1 = "8";  break;
            case 8: whichone = R.array.second_geonggi;  sp1 = "31"; break;
            case 9: whichone = R.array.second_gangwon;  sp1 = "32"; break;
            case 10:whichone = R.array.second_chungbuk; sp1 = "33"; break;
            case 11:whichone = R.array.second_chungnam; sp1 = "34"; break;
            case 12:whichone = R.array.second_gyeongbuk;sp1 = "35"; break;
            case 13:whichone = R.array.second_gyeongnam;sp1 = "36"; break;
            case 14:whichone = R.array.second_jeonbuk;  sp1 = "37"; break;
            case 15:whichone = R.array.second_jeonnam;  sp1 = "38"; break;
            case 16:whichone = R.array.second_jeju;     sp1 = "39"; break;
        }

        adspin2 = ArrayAdapter.createFromResource(this, whichone, android.R.layout.simple_spinner_dropdown_item);
        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adspin2);

    }

    public void MonthSpinner(int i) {

        switch (i) {
            case 0: mon = 1;  break;
            case 1: mon = 2;  break;
            case 2: mon = 3;  break;
            case 3: mon = 4;  break;
            case 4: mon = 5;  break;
            case 5: mon = 6;  break;
            case 6: mon = 7;  break;
            case 7:mon = 8;  break;
            case 8: mon = 9;  break;
            case 9: mon = 10;  break;
            case 10:mon = 11;  break;
            case 11:mon = 12;  break;

        }


    }


    public void runthread() {

        list.clear();

        TourApi_ tourapi_listCount = new TourApi_("searchFestival");
        tourapi_listCount.set_fest_Count_List_Url(startDateStr,endDateStr,sp1,sp2);
        String get_Url_listCount = tourapi_listCount.getUrl();


        TourApi_ tourapi = new TourApi_("searchFestival");
        tourapi.set_fest_List_Url(startDateStr,endDateStr,sp1,sp2,page);   //시작은 이번달1일 끝나는 날은 다음달 1일로
        String get_Url = tourapi.getUrl();

        Log.d(Tag+"_getURL", get_Url);

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
                get_area(get_Url);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                addButton();

                //이거 리사이클러용
                mAdapter = new FestivalAdapter(list,getApplicationContext());
                recyclerView1.setAdapter(mAdapter);
                recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            });

        }).start();


    }
    void get_area(String newUrl) throws IOException {

        try {
            URL url = new URL(newUrl);//문자열로 된 요청 url을 URL 객체로 생성.

            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, StandardCharsets.UTF_8)); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();
            String imgurl = null,title= null,addr= null,id= null,eventstartdate=null,eventenddate=null;
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
                            case "eventstartdate":
                                xpp.next();
                                eventstartdate = xpp.getText();
                                break;
                            case "eventenddate":
                                xpp.next();
                                eventenddate = xpp.getText();
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

                if(imgurl != null&&title!= null&&addr!= null&&id!= null&&eventstartdate!=null&&eventenddate!=null){
                    list.add(new FestivalList(title,imgurl,addr,id,eventstartdate,eventenddate));
                    imgurl = null;title= null;addr= null;id= null;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
            e.printStackTrace();
        }

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
                                Log.d(Tag,"totalCount="+totalCnt);
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

                runthread();
            });
            tmpButton++;

        }


    }


    public String getDate(){
        //날짜 계산해서 가져오기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;

    }
}