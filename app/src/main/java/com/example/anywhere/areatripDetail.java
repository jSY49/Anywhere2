package com.example.anywhere;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class areatripDetail extends AppCompatActivity {

    Button bckbtn,fullscreenBtn;
    ImageView detailImg1;
    TextView detailNm,detailOv,detailaddr,view_more,view_close,weatherTv;
    String cId,Url,Title,addr,img1,img2,overview,th1,pty,reh,wsd;
    ArrayList<String> category,obsrValue;
    double Lat,Lng;
    weatherCalculate weather;
    TourApi_ tourapi=new TourApi_("detailCommon");
    private final String Tag="areatripDetail";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_for_areatrip);

        detailImg1=findViewById(R.id.detailImg);
        detailNm =findViewById(R.id.detailNm);
        detailOv=findViewById(R.id.detailOv);
        detailaddr=findViewById(R.id.detailaddr);
        view_more=findViewById(R.id.view_more);
        view_close=findViewById(R.id.view_close);
        weatherTv=findViewById(R.id.weatherTv);

        category=new ArrayList<>();
        obsrValue=new ArrayList<>();

        Intent secondIntent = getIntent();
        cId=secondIntent.getStringExtra("contentId");

//        Url=tourapi.set_cIddetail_Url(cId);
        runthread();

        //뒤로가기 버튼
        bckbtn=findViewById(R.id.btnback);
        bckbtn.setOnClickListener(view -> finish());

        fullscreenBtn=findViewById(R.id.fullscreenBtn);
        fullscreenBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MapFullscreen.class);
            intent.putExtra("Lat",Lat);
            intent.putExtra("Lng",Lng);
            intent.putExtra("Title",Title);
            startActivity(intent);
        });

    }

    @SuppressLint("SetTextI18n")
    public void runthread(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("로딩중입니다.");
        dialog.show();

        new Thread(() -> {

            getXml_Detail();

            weather = new weatherCalculate(Lat,Lng);
            weather.transfer(weather,0);

            int x=(int)Math.round(weather.getxLat());
            int y=(int)Math.round(weather.getyLon());
            getXml_Detail_weather(x,y);
            setting_weather();

            runOnUiThread(() -> {
                setImg();
                overview=overview.replace("<br />","");
                overview=overview.replace("<br/>","");
                overview=overview.replace("<br>","\n");
//                        overview=overview.replace(".",".\n ");
                detailNm.setText(Title);
                detailOv.setText("\n "+overview);
                if(detailOv.getLineCount()>6){
                    detailOv.setEllipsize(TextUtils.TruncateAt.END);
                    detailOv.setMaxLines(6);
                    view_more.setVisibility(View.VISIBLE);
                    detailOv.setText("\n "+overview);
                }
                view_more.setOnClickListener(view -> {
                    detailOv.setText("\n "+overview);
                    detailOv.setMaxLines(Integer.MAX_VALUE);
                    detailOv.setEllipsize(null);
                    view_more.setVisibility(View.INVISIBLE);
                    view_close.setVisibility(View.VISIBLE);
                });
                view_close.setOnClickListener(view -> {
                    detailOv.setEllipsize(TextUtils.TruncateAt.END);
                    detailOv.setMaxLines(6);
                    view_more.setVisibility(View.VISIBLE);
                    detailOv.setText("\n "+overview);
                    view_close.setVisibility(View.INVISIBLE);
                });
                detailaddr.setText(addr);

                //날씨
                weatherTv.setText("지금 여기는 기온 "+th1+"℃ 습도 "+reh+"% 풍속 "+wsd+"m/s "+pty);
                dialog.dismiss();
            });

        }).start();
    }

    void setting_weather(){
        for(int i=0;i<category.size();i++){
            switch (category.get(i)){
                case "T1H": //기온 ℃
                    th1=obsrValue.get(i);
                    break;
                case "PTY": //강수형태 - 코드 : (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
                    pty=obsrValue.get(i);
                    break;
                case "REH": //습도 %
                    reh=obsrValue.get(i);
                    break;
                case "WSD": //풍속 m/s
                    wsd=obsrValue.get(i);
                    break;
            }
        }

        switch (pty){
            //여기서 이미지 소스설정 해주기
            case "0":
                pty="강수x";
                break;
            case "1":
                pty="비";
                break;
            case "2":
                pty="비/눈";
                break;
            case "3":
                pty="눈";
                break;
            case "5":
                pty="빗방울";
                break;
            case "6":
                pty="빗방울눈날림";
                break;
            case "7":
                pty="눈날림";
                break;
        }
    }

    void setImg(){
        Glide.with(this).load(img1).centerCrop().placeholder(R.drawable.brankimg).into(detailImg1);
    }
    void getXml_Detail(){

        String queryUrl=tourapi.set_cIddetail_Url(cId);
        Log.d(Tag,"detailUrl_ "+ queryUrl);

        try {
            URL url= new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            Log.d(Tag,"detailUrl_ "+ url);
            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, StandardCharsets.UTF_8) ); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//태그 이름 얻어오기

                        switch (tag) {
                            case "item":
                                break;
                            case "addr1":
                                xpp.next();
                                addr = xpp.getText();
                                break;
                            case "firstimage":
                                xpp.next();
                                img1 = xpp.getText();

                                break;
                            case "firstimage2":
                                xpp.next();
                                img2 = xpp.getText();

                                break;
                            case "mapx":
                                xpp.next();
                                Lng = Double.parseDouble(xpp.getText());

                                break;
                            case "mapy":
                                xpp.next();
                                Lat = Double.parseDouble(xpp.getText());
                                break;
                            case "overview":
                                xpp.next();
                                overview = xpp.getText();

                                break;
                            case "title":
                                xpp.next();
                                Title = xpp.getText();
                                break;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //태그 이름 얻어오기
                        break;

                }

                eventType= xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

    }


    void getXml_Detail_weather(int X, int Y){

        String date,baseTime;
        date=getDate();
        baseTime=getTime();
        int reArrangeTime=Integer.parseInt(baseTime);
        if(reArrangeTime%100<=40){
//            Log.d(Tag+"_ReTime1", String.valueOf(reArrangeTime));
            reArrangeTime=reArrangeTime-(reArrangeTime%100)-100;
//            Log.d(Tag+"_ReTime2", String.valueOf(reArrangeTime));
            baseTime=String.valueOf(reArrangeTime);
        }

        String weatherUrl="http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?serviceKey="+BuildConfig.MY_API_KEY
                +"&numOfRows=20&pageNo=1&base_date="+date+"&base_time="+baseTime+"&nx="+X+"&ny="+Y;

        try {
            URL url= new URL(weatherUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            Log.d(Tag,"weatherUrl_ "+ url);
            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, StandardCharsets.UTF_8) ); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//태그 이름 얻어오기

                        switch (tag) {
                            case "item":
                                break;
                            case "category":
                                xpp.next();
                                category.add(xpp.getText());
                                break;
                            case "obsrValue":
                                xpp.next();
                                obsrValue.add(xpp.getText());
                                break;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //태그 이름 얻어오기
                        break;
                }

                eventType= xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;

    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("kkmm");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        return dateResult;

    }
}



