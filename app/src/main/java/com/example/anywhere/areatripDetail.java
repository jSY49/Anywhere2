package com.example.anywhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class areatripDetail extends AppCompatActivity {

    private GoogleMap mMap;
    Button bckbtn,fullscreenBtn;
    ImageView detailImg1;
    TextView detailNm,detailOv,detailaddr;
    String cId,Url,Title,addr,img1,img2,overview;
    double Lat,Lng;
    TourApi_ tourapi=new TourApi_("detailCommon");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_for_areatrip);

        detailImg1=findViewById(R.id.detailImg);
        detailNm =findViewById(R.id.detailNm);
        detailOv=findViewById(R.id.detailOv);
        detailaddr=findViewById(R.id.detailaddr);

        Intent secondIntent = getIntent();
        cId=secondIntent.getStringExtra("contentId");

        Url=tourapi.set_cIddetail_Url(cId);
        //positionTv.setText(Url);
        runthread();


//        //정적
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        //맵 클릭 이벤트 작동 안하도록
//        View view = mapFragment.getView();
//        view.setClickable(false);
//        //여기까지
//        mapFragment.getMapAsync(this);



        //뒤로가기 버튼
        bckbtn=findViewById(R.id.btnback);
        bckbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fullscreenBtn=findViewById(R.id.fullscreenBtn);
        fullscreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapFullscreen.class);
                intent.putExtra("Lat",Lat);
                intent.putExtra("Lng",Lng);
                intent.putExtra("Title",Title);
                startActivity(intent);
            }
        });

    }

//    @Override
//    public void onMapReady( GoogleMap googleMap) {
//
//        GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
//        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);                     // 지도 유형 설정
//
//        mMap = googleMap;
//        mMap.setBuildingsEnabled(true); //3D buildings
//        mMap.getUiSettings().setMapToolbarEnabled(false); // No toolbar needed in a lite preview
//
//        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//
//
//                LatLng Point = new LatLng(Lat, Lng);
//
//                MarkerOptions markerOptions = new MarkerOptions();         // 마커 생성
//                markerOptions.position(Point);
//                markerOptions.title(Title);                                // 마커 제목
////        markerOptions.snippet("한국의 수도");                      // 마커 설명
//                mMap.addMarker(markerOptions).showInfoWindow();            //showInfomedia는 처음부터 상세정보 출력
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Point, 16));  //줌 오류 수정
//            }
//        }); // 구글맵 로딩이 완료되면 카메라 위치 조정
//
//    }
    
    /*
        이거 오류 나
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Point));                 // 초기 위치
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));                 // 줌의 정도
        
     */



    public void runthread(){


        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub


                getXml_Detail();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        setImg();
                        overview=overview.replace("<br />","");
                        overview=overview.replace("<br/>","");
                        overview=overview.replace("<br>","\n");
                        overview=overview.replace(".",".\n ");
                        detailNm.setText(Title);
                        detailOv.setText("\n "+overview);
                        detailaddr.setText("주소 : "+addr);

                    }
                });

            }
        }).start();
    }


    void setImg(){
        Glide.with(this).load(img1).centerCrop().placeholder(R.drawable.brankimg).into(detailImg1);
    }
    void getXml_Detail(){

        String queryUrl=tourapi.set_cIddetail_Url(cId);

        try {
            URL url= new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//태그 이름 얻어오기

                        if(tag.equals("item")) ;// 첫번째 검색결과
                        else if(tag.equals("addr1")){
                            xpp.next();
                            addr=xpp.getText();
                        }

                        else if(tag.equals("firstimage")){
                            xpp.next();
                            img1=xpp.getText();

                        }
                        else if(tag.equals("firstimage2")){
                            xpp.next();
                            img2=xpp.getText();

                        }
                        else if(tag.equals("mapx")){
                            xpp.next();
                            Lng=Double.parseDouble(xpp.getText());

                        }
                        else if(tag.equals("mapy")){
                            xpp.next();
                            Lat=Double.parseDouble(xpp.getText());

                        }
                        else if(tag.equals("overview")){
                            xpp.next();
                            overview=xpp.getText();

                        }
                        else if(tag.equals("title")){
                            xpp.next();
                            Title=xpp.getText();
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
}




/*
예시
<body>
<items>
<item>
✔<addr1>서울특별시 동대문구 서울시립대로2길 59</addr1>
✔<addr2>(답십리동)</addr2>
<areacode>1</areacode>
<booktour>0</booktour>
<cat1>A02</cat1>
<cat2>A0202</cat2>
<cat3>A02020700</cat3>
<contentid>2763807</contentid>
<contenttypeid>12</contenttypeid>
<createdtime>20211027143001</createdtime>
✔<firstimage>http://tong.visitkorea.or.kr/cms/resource/49/2773049_image2_1.jpg</firstimage>
✔<firstimage2>http://tong.visitkorea.or.kr/cms/resource/49/2773049_image3_1.jpg</firstimage2>
✔<mapx>127.0490977427</mapx>
✔<mapy>37.5728520032</mapy>
<mlevel>6</mlevel>
<modifiedtime>20211111104249</modifiedtime>
✔<overview>서울 동대문구 답십리 천변에 위치한 근린공원이다. 어린이 놀이터와 연못, 팔각정 정자, 장미 넝쿨 터널, 소나무 쉼터, 산책로까지 작지만 아기자기하게 조성된 공원이다. 농구장과 배드민턴장도 있다. 전매청 자리를 공원으로 조성하면서 옛날 답십리 일대 간데메(中山) 자연 부락 마을의 토박이 이름을 따서 간데메공원이라 이름을 정했다고 한다.</overview>
<sigungucode>11</sigungucode>
✔<title>간데메공원</title>
<zipcode>02595</zipcode>
 */