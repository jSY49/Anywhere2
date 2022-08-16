package com.example.anywhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anywhere.databinding.ActivitymapBinding;
import com.example.anywhere.databinding.MapFullscreenBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapFullscreen extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    double Lat,Lng;
    String Title;
    Button closebtn;
    private MapFullscreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MapFullscreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent secondIntent = getIntent();
        Lat=secondIntent.getDoubleExtra("Lat",37.5666);
        Lng=secondIntent.getDoubleExtra("Lng",126.9774);
        Title=secondIntent.getStringExtra("Title");

        closebtn=findViewById(R.id.closeBtn);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //정적
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //맵 클릭 이벤트 작동 안하도록
        View v = mapFragment.getView();
        v.setClickable(true);
        //여기까지
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady( GoogleMap googleMap) {

        GoogleMapOptions options = new GoogleMapOptions().liteMode(true);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);                     // 지도 유형 설정

        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false); // No toolbar needed in a lite preview


        LatLng Point = new LatLng(Lat, Lng);

        MarkerOptions markerOptions = new MarkerOptions();         // 마커 생성
        markerOptions.position(Point);
        markerOptions.title(Title);                                // 마커 제목
//        markerOptions.snippet("한국의 수도");                      // 마커 설명
        mMap.addMarker(markerOptions).showInfoWindow();            //showInfomedia는 처음부터 상세정보 출력
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Point, 14));  //줌 오류 수정

    }

}
