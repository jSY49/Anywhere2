package com.example.anywhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.anywhere.databinding.MapFullscreenBinding;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


public class MapFullscreen extends AppCompatActivity {

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


        MapView mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Lat, Lng), true);
        // 줌 레벨 변경
        mapView.setZoomLevel(1, true);
//        // 중심점 변경 + 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);

        MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(Lat, Lng);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(Title);
        marker.setTag(0);
        marker.setMapPoint(MARKER_POINT);
        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

    }


}
