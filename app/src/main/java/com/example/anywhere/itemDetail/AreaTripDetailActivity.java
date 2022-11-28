package com.example.anywhere.itemDetail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.anywhere.BuildConfig;
import com.example.anywhere.Connect.TourApi_;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.R;
import com.github.chrisbanes.photoview.PhotoView;

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

public class AreaTripDetailActivity extends AppCompatActivity {

    Button bckbtn, fullscreenBtn;
    ImageView detailImg1;
    TextView detailNm, detailOv, detailaddr, view_more, view_close, weatherTv, moreDetail, moreDetailTag;
    String cId, Url, Title, addr, img1, img2, overview, th1, pty, reh, wsd, contenttypeid, AllStr, detailTag = "";
    ArrayList<String> category, obsrValue;
    double Lat, Lng;
    weatherCalculate weather;
    private final String Tag = "AreaTripDetailActivity";
    RelativeLayout fullImg_Lay;
    PhotoView fullImg;
    CustomProgressDialog customProgressDialog;
    String[] Tag_12, Tag_14, Tag_15, Tag_28, Tag_32, Tag_38, Tag_39;
    String[] TagName_12, TagName_14, TagName_15, TagName_28, TagName_32, TagName_38, TagName_39;
    String[] element_12, element_14, element_15, element_28, element_32, element_38, element_39;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_for_areatrip);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        detailImg1 = findViewById(R.id.detailImg);
        detailNm = findViewById(R.id.detailNm);
        detailOv = findViewById(R.id.detailOv);
        detailaddr = findViewById(R.id.detailaddr);
        view_more = findViewById(R.id.view_more);
        view_close = findViewById(R.id.view_close);
        //weatherTv = findViewById(R.id.weatherTv);
        fullImg = findViewById(R.id.fullImg);
        fullImg_Lay = findViewById(R.id.fullImg_Lay);
        moreDetailTag = findViewById(R.id.MoredetailTv1);

        category = new ArrayList<>();
        obsrValue = new ArrayList<>();

        Intent secondIntent = getIntent();
        cId = secondIntent.getStringExtra("contentId");


        //뒤로가기 버튼
        bckbtn = findViewById(R.id.btnback);
        bckbtn.setOnClickListener(view -> finish());

        fullscreenBtn = findViewById(R.id.fullscreenBtn);
        fullscreenBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MapFullscreenActivity.class);
            intent.putExtra("Lat", Lat);
            intent.putExtra("Lng", Lng);
            intent.putExtra("Title", Title);
            startActivity(intent);
        });

        try {
            runthread();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 나중에 다시 이용해 주세요", Toast.LENGTH_LONG).show();
//           finish();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
        }


    }

    private void settingTag() {
        Tag_12 = new String[]{"opendate", "restdate", "infocenter", "useseason", "usetime", "heritage1", "heritage2", "heritage3", "expguide", "expagerange", "accomcount", "parking", "chkbabycarriage", "chkpet", "chkcreditcard"};
        Tag_14 = new String[]{"infocenterculture", "parkingculture", "parkingfee", "restdateculture", "usefee", "usetimeculture", "scale", "spendtime", "accomcountculture", "chkbabycarriageculture", "chkcreditcardculture", "chkpetculture", "discountinfo"};
        Tag_15 = new String[]{"eventstartdate", "eventenddate", "playtime", "eventhomepage", "agelimit", "bookingplace", "placeinfo", "subevent", "program", "usetimefestival", "discountinfofestival", "spendtimefestival", "festivalgrade", "eventplace", "sponsor1", "sponsor1tel", "sponsor2", "sponsor2tel"};
        Tag_28 = new String[]{"openperiod", "reservation", "infocenterleports", "scaleleports", "accomcountleports", "restdateleports", "usetimeleports",
                "usefeeleports", "expagerangeleports", "parkingleports", "parkingfeeleports", "chkbabycarriageleports", "chkpetleports", "chkcreditcardleports"};
        Tag_32 = new String[]{"checkintime", "checkouttime", "accomcountlodging", "benikia", "chkcooking", "foodplace", "goodstay", "hanok", "infocenterlodging",
                "parkinglodging", "pickup", "roomcount", "reservationlodging", "reservationurl", "roomtype", "scalelodging", "subfacility", "barbecue", "b  eauty", "beverage"
                , "bicycle", "campfire", "fitness", "karaoke", "publicbath", "publicpc", "sauna", "seminar", "sports", "refundregulation"};
        Tag_38 = new String[]{"chkbabycarriage shopping", "chkcreditcard shopping", "chkpetshopping", "culturecenter", "fairday", "infocentershopping", "opendateshopping", "opentime"
                , "parkingshopping", "restdateshopping", "restroom", "saleitem", "saleitemcost", "scaleshopping", "shopguide"};
        Tag_39 = new String[]{"opentimefood", "restdatefood", "seat", "kidsfacility", "firstmenu", "smoking", "packing", "infocenterfood", "scalefood", "parkingfood", "opendatefood", "discountinfofood", "chkcreditcardfood", "reservationfood", "lcnsno"};

        TagName_12 = new String[]{"개장일", "쉬는날", "문의 및 안내", "이용시기", "이용 시간", "세계문화유산유무", "세계자연유산유무", "세계기록유산유무", "체험안내", "체험 가능 연령", "수용 인원", "주차시설", "유모차 대여 점보", "애완동물 동반 가능 정보", "신용카드 가능정보"};
        TagName_14 = new String[]{"문의및안내", "주차시설", "주차요금", "쉬는날", "이용요금", "이용시간", "규모", "관람소요시간", "수용인원", "유모차대여정보", "신용카드가능정보", "애완동물동반가능정보", "할인정보"};
        TagName_15 = new String[]{"행사시작일", "행사종료일", "공연시간", "행사홈페이지", "관람가능연령", "예매처", "행사장 위치 안내", "부대행사", "행사프로그램", "이용요금", "할인정보", "관람소요시간", "축제등급", "행사장소", "주최자정보1", "주최자연락처1", "주최자정보2", "주최자연락처2"};
        TagName_28 = new String[]{"개장시간", "예약안내", "문의 및 안내", "규모", "수용인원", "쉬는날", "이용시간", "입장료", "체험가능연령", "주차시설", "주차요금", "유모차 대여정보", "애완동물동반 가능정보", "신용카드 가능정보"};
        TagName_32 = new String[]{"입실시간", "퇴실시간", "수용가능인원", "베니키아여부", "객실내취사여부", "식음료장", "굿스테이여부", "한옥여부", "문의및안내", "주차시설", "픽업서비스", "객실수"
                , "예약안내", "예약안내홈페이지", "객실유형", "규모", " 부대시설 (기타)", "바비큐장여부", "뷰티시설정보", "식음료장여부", "자전거대여여부", "캠프파이어여부", "휘트니스센터여부", "노래방여부", "공용샤워실여부"
                , "공용 PC실여부", "사우나실여부", "세미나실여부", "스포츠시설여부", "환불규정"};
        TagName_38 = new String[]{"유모차대여정보", "신용카드가능정보", "애완동물동반가능정보", "문화센터바로가기", "장서는날", "문의및안내", "개장일", "영업시간", "주차시설", "쉬는날", "화장실설명", "판매품목", "판매품목별가격", "규모", "매장안내"};
        TagName_39 = new String[]{"영업시간", "쉬는 날", "좌석 수", "어린이놀이방여부", "대표메뉴", "금연/흡연여부", "포장가능", "문의 및 안내", "규모", "주차시설", "개업일","할인정보", "신용카드정보", "예약안내", "인허가번호"};


        element_12 = new String[15];
        element_14 = new String[13];
        element_15 = new String[18];
        element_28 = new String[14];
        element_32 = new String[30];
        element_38 = new String[15];
        element_39 = new String[15];


    }

    @SuppressLint("SetTextI18n")
    public void runthread() {
        customProgressDialog.show();
        new Thread(() -> {
            getXml_Detail();

//            weather = new weatherCalculate(Lat, Lng);
//            weather.transfer(weather, 0);
//
//            int x = (int) Math.round(weather.getxLat());
//            int y = (int) Math.round(weather.getyLon());
//            try {
//                getXml_Detail_weather(x, y);
//                //setting_weather();
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "날씨 호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
//            }


            runOnUiThread(() -> {
                setImg();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    overview = Html.fromHtml(overview, Html.FROM_HTML_MODE_LEGACY).toString();
                } else {
                    overview = Html.fromHtml(overview).toString();
                }
//                overview = overview.replaceAll("<br />", "");
//                overview = overview.replaceAll("<br/>", "");
//                overview = overview.replaceAll("<br>", "\n");
//                        overview=overview.replace(".",".\n ");
                detailNm.setText(Title);
                detailOv.setText("\n " + overview);
                if (detailOv.getLineCount() > 6) {
                    detailOv.setEllipsize(TextUtils.TruncateAt.END);
                    detailOv.setMaxLines(6);
                    view_more.setVisibility(View.VISIBLE);
                    detailOv.setText("\n " + overview);
                }
                view_more.setOnClickListener(view -> {
                    detailOv.setText("\n " + overview);
                    detailOv.setMaxLines(Integer.MAX_VALUE);
                    detailOv.setEllipsize(null);
                    view_more.setVisibility(View.INVISIBLE);
                    view_close.setVisibility(View.VISIBLE);
                });
                view_close.setOnClickListener(view -> {
                    detailOv.setEllipsize(TextUtils.TruncateAt.END);
                    detailOv.setMaxLines(6);
                    view_more.setVisibility(View.VISIBLE);
                    detailOv.setText("\n " + overview);
                    view_close.setVisibility(View.INVISIBLE);
                });
                detailaddr.setText(addr);

                //날씨
                // weatherTv.setText("지금 여기는 기온 " + th1 + "℃ 습도 " + reh + "% 풍속 " + wsd + "m/s " + pty);

            });
            customProgressDialog.dismiss();
        }).start();


    }

    void setting_weather() {
        for (int i = 0; i < category.size(); i++) {
            switch (category.get(i)) {
                case "T1H": //기온 ℃
                    th1 = obsrValue.get(i);
                    break;
                case "PTY": //강수형태 - 코드 : (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
                    pty = obsrValue.get(i);
                    break;
                case "REH": //습도 %
                    reh = obsrValue.get(i);
                    break;
                case "WSD": //풍속 m/s
                    wsd = obsrValue.get(i);
                    break;
            }
        }

        switch (pty) {
            //여기서 이미지 소스설정 해주기
            case "0":
                pty = "강수x";
                break;
            case "1":
                pty = "비";
                break;
            case "2":
                pty = "비/눈";
                break;
            case "3":
                pty = "눈";
                break;
            case "5":
                pty = "빗방울";
                break;
            case "6":
                pty = "빗방울눈날림";
                break;
            case "7":
                pty = "눈날림";
                break;
        }
    }

    void setImg() {
        Glide.with(this).load(img1).centerCrop().placeholder(R.drawable.brankimg).into(detailImg1);
    }

    void getXml_Detail() {
        TourApi_ tourapi = new TourApi_("detailCommon");
        tourapi.set_cIddetail_Url(cId);

        try {
            URL url = new URL(tourapi.getUrl());//문자열로 된 요청 url을 URL 객체로 생성.
            Log.d(Tag, "detailUrl_ " + url);
            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
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
                            case "contenttypeid":
                                xpp.next();
                                contenttypeid = xpp.getText();
                                Log.d(Tag, "ctId=" + contenttypeid);
                                break;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기
                        break;

                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        GetmoreDetail_Json();

    }


    void getXml_Detail_weather(int X, int Y) {

        String date, baseTime;
        date = getDate();
        baseTime = getTime();
        int reArrangeTime = Integer.parseInt(baseTime);
        if (reArrangeTime % 100 <= 40) {
//            Log.d(Tag+"_ReTime1", String.valueOf(reArrangeTime));
            reArrangeTime = reArrangeTime - (reArrangeTime % 100) - 100;
//            Log.d(Tag+"_ReTime2", String.valueOf(reArrangeTime));
            baseTime = String.valueOf(reArrangeTime);
        }

        String weatherUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst?serviceKey=" + BuildConfig.MY_API_KEY
                + "&numOfRows=20&pageNo=1&base_date=" + date + "&base_time=" + baseTime + "&nx=" + X + "&ny=" + Y;

        try {
            URL url = new URL(weatherUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            Log.d(Tag, "weatherUrl_ " + url);
            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
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
                        tag = xpp.getName(); //태그 이름 얻어오기
                        break;
                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
            Toast.makeText(getApplicationContext(), "날씨 호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(date);

    }

    private String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("kkmm");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(date);

    }

    public void fullImg(View view) {

        fullImg_Lay.setVisibility(View.VISIBLE);
//        Picasso.get().load(img1).error(R.drawable.brankimg).into(fullImg);
        Glide.with(this).load(img1).placeholder(R.drawable.brankimg).into(fullImg);
        fullImg.setVisibility(View.VISIBLE);
    }

    public void fullImgClose(View view) {
        fullImg_Lay.setVisibility(View.INVISIBLE);
        fullImg.setVisibility(View.INVISIBLE);
    }


    //수정 필요
    private void GetmoreDetail_Json() {
        TourApi_ ForDetailApi = new TourApi_("detailIntro");
        ForDetailApi.set_detailintro_Url(cId, contenttypeid);
        settingTag();

        new Thread(() -> {
            try {
                URL url = new URL(ForDetailApi.getUrl());//문자열로 된 요청 url을 URL 객체로 생성.
                Log.d(Tag, "MoreDetailUrl_ " + url);
                InputStream is = url.openStream(); //url위치로 입력스트림 연결
                //xml파싱
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is, StandardCharsets.UTF_8)); //inputstream 으로부터 xml 입력받기
                String tag;
                xpp.next();
                int eventType = xpp.getEventType();

                switch (contenttypeid) {
                    case "12":
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//태그 이름 얻어오기
                                    for (int i = 0; i < 15; i++) {
                                        if (Tag_12[i].equals(tag)) {
                                            xpp.next();
                                            element_12[i] = xpp.getText();
                                            break;
                                        }
                                    }
                                    break;
                            }
                            eventType = xpp.next();
                        }
                        for (int j = 0; j < 15; j++) {
                            if (element_12[j] != null) {
                                element_12[j] = Html.fromHtml(element_12[j]).toString();
                                if (j == 0 || j == 1 || j == 2) {
                                    if (element_12[j].equals("0")) {
                                        element_12[j] = "지정되지 않음";
                                    } else {
                                        element_12[j] = "지정됨";
                                    }
                                    detailTag += (TagName_12[j] + " : " + element_12[j] + "\n");
                                } else {
                                    detailTag += (TagName_12[j] + " : " + element_12[j] + "\n");
                                }
                            }
                        }
                        break;
                    case "14":
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//태그 이름 얻어오기
                                    for (int i = 0; i < 13; i++) {
                                        if (Tag_14[i].equals(tag)) {
                                            xpp.next();
                                            element_14[i] = xpp.getText();
                                            break;
                                        }
                                    }
                                    break;
                            }
                            eventType = xpp.next();
                        }
                        for (int j = 0; j < 13; j++) {
                            if (element_14[j] != null) {
                                element_14[j] = Html.fromHtml(element_14[j]).toString();
                                detailTag += (TagName_14[j] + " : " + element_14[j] + "\n");
                            }
                        }
                        break;
                    case "15":
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//태그 이름 얻어오기
                                    for (int i = 0; i < 18; i++) {
                                        if (Tag_15[i].equals(tag)) {
                                            xpp.next();
                                            element_15[i] = xpp.getText();
                                            break;
                                        }
                                    }
                                    break;
                            }
                            eventType = xpp.next();
                        }
                        for (int j = 0; j < 18; j++) {
                            if (element_15[j] != null) {
                                element_15[j] = Html.fromHtml(element_15[j]).toString();
                                detailTag += (TagName_15[j] + " : " + element_15[j] + "\n");
                            }
                        }
                        break;
                    case "28":
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//태그 이름 얻어오기
                                    for (int i = 0; i < 14; i++) {
                                        if (Tag_28[i].equals(tag)) {
                                            xpp.next();
                                            element_28[i] = xpp.getText();
                                            break;
                                        }
                                    }
                                    break;
                            }
                            eventType = xpp.next();
                        }
                        for (int j = 0; j < 14; j++) {
                            if (element_28[j] != null) {
                                element_28[j] = Html.fromHtml(element_28[j]).toString();
                                detailTag += (TagName_28[j] + " : " + element_28[j] + "\n");
                            }
                        }
                        break;
                    case "32":
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//태그 이름 얻어오기
                                    for (int i = 0; i < 30; i++) {
                                        if (Tag_32[i].equals(tag)) {
                                            xpp.next();
                                            element_32[i] = xpp.getText();
                                            break;
                                        }
                                    }
                                    break;
                            }
                            eventType = xpp.next();
                        }
                        for (int j = 0; j < 15; j++) {
                            if (element_32[j] != null) {
                                element_32[j] = Html.fromHtml(element_32[j]).toString();
                                if (element_32[j].equals("0")) {
                                    element_32[j] = "없음";
                                } else if (element_32[j].equals("1")) {
                                    element_32[j] = "있음";
                                }
                                detailTag += (TagName_32[j] + " : " + element_32[j] + "\n");
                            }
                        }
                        break;
                    case "38":
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//태그 이름 얻어오기
                                    for (int i = 0; i < 15; i++) {
                                        if (Tag_38[i].equals(tag)) {
                                            xpp.next();
                                            element_38[i] = xpp.getText();
                                            break;
                                        }
                                    }
                                    break;
                            }
                            eventType = xpp.next();
                        }
                        for (int j = 0; j < 15; j++) {
                            if (element_38[j] != null) {
                                element_38[j] = Html.fromHtml(element_38[j]).toString();
                                detailTag += (TagName_38[j] + " : " + element_38[j] + "\n");
                            }
                        }
                        break;
                    case "39":
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();//태그 이름 얻어오기
                                    for (int i = 0; i < 15; i++) {
                                        if (Tag_39[i].equals(tag)) {
                                            xpp.next();
                                            element_39[i] = xpp.getText();
                                            break;
                                        }
                                    }
                                    break;
                            }
                            eventType = xpp.next();
                        }
                        for (int j = 0; j < 15; j++) {
                            if (element_39[j] != null) {
                                element_39[j] = Html.fromHtml(element_39[j]).toString();
                                detailTag += (TagName_39[j] + " : " + element_39[j] + "\n");
                            }
                        }
                        break;
                }

            } catch (Exception e) {
                // TODO Auto-generated catch blocke.printStackTrace();
                Log.d(Tag, String.valueOf(e));

            }
            runOnUiThread(() -> {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    detailTag= Html.fromHtml(detailTag, Html.FROM_HTML_MODE_LEGACY).toString();
//                }else {
//                    detailTag=Html.fromHtml(detailTag).toString();
//                }
//                detailTag=detailTag.replaceAll("<br />","");
                moreDetailTag.setText(detailTag);
//                moreDetail.setText(AllStr);
            });

        }).start();
    }


}



