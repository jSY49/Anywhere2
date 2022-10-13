package com.example.anywhere.Festival;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anywhere.Connect.TourApi_;
import com.example.anywhere.R;

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


public class FestivalActivity extends AppCompatActivity {

    private RecyclerView recyclerView1;
    private FestivalAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    String startDateStr,endDateStr;
    int startDateInt,endDateInt;
    ArrayList<FestivalList> festivalList;
    TextView thisDate;

    private String Tag="FestivalActivity";



    //top20페이지
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival);

        festivalList=new ArrayList<>();
        thisDate=findViewById(R.id.thisDate);

        RecyclerViewSetting();

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

    public void RecyclerViewSetting(){
        recyclerView1=findViewById(R.id.festival_recycler1);
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

        runthread();
    }
    public void backBtn(View view) {
        finish();
    }
    public void viewMorethismonthFest(View view) {

        Intent intent = new Intent(getApplicationContext(), ThisMonthFestivalActivity.class);
        startActivity(intent);

    }

    public void runthread() {

        festivalList.clear();
        startDateInt=getDate("total");
        startDateStr= String.valueOf(startDateInt);
        int NowMonth= getDate("month");
        if(12==NowMonth){
            endDateInt =(getDate("year")+1)*10000+getDate("month")*100;
        }else{
            endDateInt =getDate("year")*10000+(getDate("month")+1)*100;
        }

        if(getDate("day")==31){
            switch (endDateInt%100){
                case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                 endDateInt=endDateInt+getDate("day");
                 break;
            case 4: case 6: case 9: case 11:
                endDateInt=endDateInt+30;
                break;
            case 2:
                endDateInt=endDateInt+28;
                break;
            }
        }else{
            endDateInt=endDateInt+getDate("day");
        }
//        startDateInt=Integer.parseInt(nowDate);
//        startDateInt=startDateInt-(startDateInt%100)+1;
//        startDateStr=String.valueOf(startDateInt);
//
//        endDateInt=Integer.parseInt(nowDate);
//        int monthcheck;
//        monthcheck = (endDateInt / 100);
//        monthcheck %= 100;
//
//        switch(monthcheck){
//            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
//                 endDateInt=endDateInt-(endDateInt%100)+31;
//                 break;
//            case 4: case 6: case 9: case 11:
//                endDateInt=endDateInt-(endDateInt%100)+30;
//                break;
//            case 2:
//                endDateInt=endDateInt-(endDateInt%100)+28;
//                break;
//
//
//        }

        endDateStr=String.valueOf(endDateInt);

        Log.d(Tag,"startDate_"+startDateStr);
        Log.d(Tag, "endDate_"+endDateStr);

        TourApi_ tourapi = new TourApi_("searchFestival");
        tourapi.set_fest_List_Top30_Url(startDateStr,endDateStr);   //시작은 이번달1일 끝나는 날은 다음달 1일로
        String get_Url = tourapi.getUrl();

        Log.d(Tag+"_getURL", get_Url);

        new Thread(() -> {
            
            try {
                get_area(get_Url);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            
            runOnUiThread(() -> {
                thisDate.setText(startDateStr+"~"+endDateStr);
                //이거 리사이클러용
                mAdapter = new FestivalAdapter(festivalList,getApplicationContext());
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
                    festivalList.add(new FestivalList(title,imgurl,addr,id,eventstartdate,eventenddate));
                    imgurl = null;title= null;addr= null;id= null;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
            e.printStackTrace();
        }

    }

    @SuppressLint("SimpleDateFormat")
    public int getDate(String which){
        //날짜 계산해서 가져오기
        SimpleDateFormat sdf;
        switch (which){
            case "year":    sdf = new SimpleDateFormat("yyyy");break;
            case "month":   sdf = new SimpleDateFormat("MM");break;
            case "day":    sdf = new SimpleDateFormat("dd");break;
            case "total":    sdf = new SimpleDateFormat("yyyyMMdd");break;
            default:
                throw new IllegalStateException("Unexpected value: " + which);
        }
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        int dateResult = Integer.parseInt(sdf.format(date));
        return dateResult;
    }
}