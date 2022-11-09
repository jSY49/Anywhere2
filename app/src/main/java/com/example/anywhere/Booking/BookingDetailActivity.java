package com.example.anywhere.Booking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.anywhere.BuildConfig;
import com.example.anywhere.Connect.firebaseConnect;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.R;
import com.example.anywhere.itemDetail.MapFullscreenActivity;
import com.example.anywhere.itemDetail.weatherCalculate;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

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

public class BookingDetailActivity extends AppCompatActivity {

    String Tag="BookingDetailActivity";
    private firebaseConnect fbsconnect;
    String code;
    TextView title,addr,ov,MoreD,weatherTv,Pprice;
    ImageView img,detailImg;
    String imgurl,lat,lon,name, th1, pty, reh, wsd,epEmail;
    RelativeLayout fullImg_Lay;
    PhotoView fullImg;
    String price;

    ArrayList<String> category, obsrValue;
    weatherCalculate weather;
    CustomProgressDialog customProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Intent intent = getIntent();
        code = intent.getStringExtra("code");

        fullImg = findViewById(R.id.fullImg);
        fullImg_Lay = findViewById(R.id.fullImg_Lay);
        title=findViewById(R.id.detailNm);
        addr=findViewById(R.id.detailaddr);
        ov=findViewById(R.id.detailOv);
        MoreD=findViewById(R.id.MoredetailTv1);
        img=findViewById(R.id.detailImg);
        weatherTv=findViewById(R.id.weatherTv);
        Pprice=findViewById(R.id.detailPrice);
        obsrValue = new ArrayList<>();
        category = new ArrayList<>();

        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();



        settingData();
    }

    private void settingData() {
        customProgressDialog.show();
        DocumentReference docRef = fbsconnect.db.collection("product").document(code);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name= String.valueOf(document.get("title"));
                        title.setText(name);
                        price=String.valueOf(document.get("price"));
                        Pprice.setText(price+"원");
                        addr.setText(String.valueOf(document.get("totalAddr")));
                        ov.setText(String.valueOf(document.get("overview")));
                        MoreD.setText(String.valueOf(document.get("info")));
                        imgurl= String.valueOf(document.get("img"));
                        lat= String.valueOf(document.get("lat"));
                        lon= String.valueOf(document.get("lon"));
                        epEmail= String.valueOf(document.get("id"));
                        Log.d(Tag, epEmail);
                        runThread();
                        setImg();
                    } else {
                        Log.d(Tag, "No such document");
                    }
                } else {
                    Log.d(Tag, "get failed with ", task.getException());
                }

                customProgressDialog.dismiss();
            }
        });



    }

    private void runThread() {
        new Thread(() -> {
            Log.d(Tag,"where="+lat+","+lon);
            double latitude=Double.parseDouble(lat);
            double longitude=Double.parseDouble(lon);

            weather = new weatherCalculate(latitude,longitude);
            weather.transfer(weather, 0);

            int x = (int) Math.round(weather.getxLat());
            int y = (int) Math.round(weather.getyLon());
            try {
                getXml_Detail_weather(x, y);
                setting_weather();
            }
            catch (Exception e){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> Toast.makeText(getApplicationContext(), "날씨 호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show(), 0);
            }

            runOnUiThread(() -> {
                //날씨
                weatherTv.setText("지금 여기는 기온 " + th1 + "℃ 습도 " + reh + "% 풍속 " + wsd + "m/s " + pty);
            });

        }).start();
    }

    private void setImg() {
        Log.d(Tag, "imgUrl: "+imgurl);
        Glide.with(this).load(imgurl).placeholder(R.drawable.brankimg).centerCrop().into(img);
    }

    public void fullImg(View view) {
        fullImg_Lay.setVisibility(View.VISIBLE);
//        Picasso.get().load(img1).error(R.drawable.brankimg).into(fullImg);
        Glide.with(this).load(imgurl).placeholder(R.drawable.brankimg).into(fullImg);
        fullImg.setVisibility(View.VISIBLE);
    }

    public void fullImgClose(View view) {
        fullImg_Lay.setVisibility(View.INVISIBLE);
        fullImg.setVisibility(View.INVISIBLE);
    }

    public void backBtn(View view) {
        finish();
    }

    public void goToMap(View view) {
        Intent intent = new Intent(getApplicationContext(), MapFullscreenActivity.class);
        intent.putExtra("Lat", Double.parseDouble(lat));
        intent.putExtra("Lng", Double.parseDouble(lon));
        intent.putExtra("Title", name);
        startActivity(intent);
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

    public void gotoBooking(View view) {
        FirebaseUser user = fbsconnect.fb_user();
        if(user==null){
            Toast.makeText(this,"로그인 후 이용 가능합니다.",Toast.LENGTH_LONG).show();
        }else{
            Intent intent = new Intent(getApplicationContext(), productBookingActivity.class);
            intent.putExtra("code",code);
            intent.putExtra("price",price);
            intent.putExtra("epEmail",epEmail);
            intent.putExtra("itemName",name);
            startActivity(intent);
        }

    }
}
