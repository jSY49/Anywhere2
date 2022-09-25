package com.example.anywhere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.anywhere.databinding.MapFullscreenBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapFullscreen extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    double Lat,Lng;
    String Title;
    Button closebtn;
    private MapFullscreenBinding binding;
    private UiSettings mUiSettings;
    Location location;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
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

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


/*
//카카오
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
*/
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mUiSettings= googleMap.getUiSettings();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mUiSettings.setCompassEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Lat,Lng), 18));
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Lat, Lng))
                .title(Title));


        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Lat, Lng), 16));

                }


            }
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "위치 권한 성공", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
