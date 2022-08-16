package com.example.anywhere;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anywhere.databinding.ActivitymapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener
{
    private GoogleMap mMap;
    double Lat=37.510759,Lng=126.977943;
    private ActivitymapBinding binding;
    ArrayList<String> listTitle,listlat,listlng,listId;
    String lat,lng;
    private ArrayList<Marker> markerList = new ArrayList();
    private int tourflag=0; //0이면 off/ 1이면 on 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitymapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        listTitle=new ArrayList<String>();
        listlat=new ArrayList<String>();
        listlng=new ArrayList<String>();
        listId=new ArrayList<String>();

        //정적
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        View v = mapFragment.getView();
        v.setClickable(true);
        //여기까지
        mapFragment.getMapAsync(this);

        //닫기버튼
        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        //관광지 버튼 온오프
        binding.activitymapReLYBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tourflag==0){
                    tourflag=1;
                   set_tour();
                   binding.activitymapReLYBtn1.setBackgroundColor(Color.rgb(209,178,255));
                }
                else{
                    tourflag=0;
                    removeMarkerTour();
                    binding.activitymapReLYBtn1.setBackgroundColor(Color.rgb(95,0,255));
                }
                Log.d("tourFlag", String.valueOf(tourflag));
            }
        });
        //음식점 버튼 온오프
        binding.activitymapReLYBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    @Override
    public void onMapReady( GoogleMap googleMap) {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);                     // 지도 유형 설정

        mMap = googleMap;

        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);

        mMap.setBuildingsEnabled(true); //3D buildings
        mMap.getUiSettings().setMapToolbarEnabled(false); // No toolbar needed in a lite preview
        mMap.getUiSettings().setMapToolbarEnabled(false);

        LatLng Point = new LatLng(Lat, Lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Point, 13));  //줌 오류 수정


    }



    void set_tour() {

        lat = String.valueOf(Math.round((mMap.getCameraPosition().target.latitude)*10000000)/10000000.0);
        lng = String.valueOf(Math.round((mMap.getCameraPosition().target.longitude)*10000000)/10000000.0);

        TourApi_ tourapi=new TourApi_("locationBasedList");
        tourapi.set_tourList_forMap(lng,lat);
        String get_Url=tourapi.getUrl();

//        tourlist=new String[4][];   //title,lat,lng,contentid)

        Log.d("set_maptour_getURL",get_Url);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    get_area(get_Url);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        for(int i=0;i<listTitle.size();i++){
                            if(listTitle.get(i)!=null){
                                double new_lat=Double.parseDouble(listlat.get(i));
                                double new_lng=Double.parseDouble(listlng.get(i));
//                                Marker marker = mMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(new_lat,new_lng))
//                                        .title(tourlist[0][i])
//                                        .snippet(tourlist[3][i]));
//                                markerList.add(marker);
                                LatLng Point = new LatLng(new_lat, new_lng);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(Point);
                                markerOptions.title(listTitle.get(i));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                markerOptions.alpha((float) 0.7);
                                mMap.addMarker(markerOptions);

                            }

                        }

                    }
                });

            }
        }).start();
    }

    void get_area(String newUrl) throws IOException {

        listTitle.clear();
        listlat.clear();
        listlng.clear();
        listId.clear();

        try {
            Log.d("totalCount_newUrl1: ",newUrl);
            URL url= new URL(newUrl);//문자열로 된 요청 url을 URL 객체로 생성.

            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        Log.d("start_document","start xml!");
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//태그 이름 얻어오기

                        if(tag.equals("item")) ;// 첫번째 검색결과
                        else if(tag.equals("title")){
                            xpp.next();
                            listTitle.add(xpp.getText());
                        }
                        else if(tag.equals("mapx")){
                            xpp.next();
                            listlng.add(xpp.getText());
                        }
                        else if(tag.equals("mapy")){
                            xpp.next();
                            listlat.add(xpp.getText());
                        }
                        else if(tag.equals("contentid")){
                            xpp.next();
                            listId.add(xpp.getText());
                        }

                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //태그 이름 얻어오기

                        if(tag.equals("item")) //buffer.append("\n");// 첫번째 검색결과종료..줄바꿈

                            break;

                }

                eventType= xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
            e.printStackTrace();
        }

    }

    @Override
    public void onCameraIdle() {
        removeMarkerAll();

        if(tourflag==1){
            set_tour();
        }
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    private void removeMarkerAll() {
//        for (Marker marker : markerList) {
//            marker.remove();
//        }
        mMap.clear();

    }
    private void removeMarkerTour() {
        mMap.clear();
    }





}
