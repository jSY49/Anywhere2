package com.example.anywhere.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.anywhere.Connect.TourApi_;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.PermissionUtils;
import com.example.anywhere.R;
import com.example.anywhere.databinding.ActivitymapBinding;
import com.example.anywhere.itemDetail.AreaTripDetailActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnCameraMoveListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;

    double Lat = 37.56556895, Lng = 126.97801979;
    private ActivitymapBinding binding;
    ArrayList<list> arrList;    //xml 파싱 데이터 저장
    int tourFlag = 0, foodFlag = 0, leisureFlag = 0, accomFlag = 0;
    private ArrayList<DataGetterSetters> dataList;
    int radius = 1000;
    private String Tag = "MapActivity";
    BottomSheetBehavior bottomSheetBehavior;
    String contentId;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    CustomProgressDialog customProgressDialog;
//    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    int preWidth = 100, preHeight = 100, afterWidth = 130, afterHeight = 130;
    TourApi_ tourapi = new TourApi_("locationBasedList");
    TourApi_ keywrdApi;
    public void backBtn(View view) {
        finish();
    }

    public void searchBtn(View view) throws SAXException {

        String keyword=binding.searchEditText.getText().toString();
        if(keyword.length()>0) {
            keywrdApi=new TourApi_("searchKeyword");
            keywrdApi.set_total_URL(keyword);
            try {
                dataList.clear();
            }catch (Exception e){

            }

            removeMarker();
            tourFlag = 1;foodFlag = 1;leisureFlag = 1;accomFlag = 1;
            onofftour(view);onofffood(view);onoffleisure(view);onoffAccom(view);
            if(binding.searchEditText.requestFocus()){
                hideKeyboard();
                binding.searchEditText.clearFocus();
            }

            set_area_Sax("search");
        }else{
            Toast.makeText(this, "검색어를 입력해 주세요", Toast.LENGTH_LONG).show();
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.6604297,127.7373157),7));
    }

//    public void PlacePicker(View view) {
//        Log.d(Tag,"placePicker clicked!");
//        // Set the fields to specify which types of place data to
//        // return after the user has made a selection.
//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
//
//        // Start the autocomplete intent.
//        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                .build(this);
//        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//    }

    public static class list {
        String title, lat, lng, id;

        public list(String title, String lng, String lat, String id) {
            this.title = title;
            this.lat = lat;
            this.lng = lng;
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public String getLat() {
            return lat;
        }

        public String getLng() {
            return lng;
        }

        public String getId() {
            return id;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitymapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        binding.map.setOnClickListener(v -> {
//            try{
//                hideKeyboard();
//                binding.searchEditText.clearFocus();
//            }catch (Exception e){
//                Log.d(Tag, "layTouchException", e);
//            }
//        });

        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.bottomsheet.setVisibility(View.INVISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomsheet);
        //필요?
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {   //bottomsheet state 정보

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {     //BottomSheet의 offset 정보

            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.i(Tag, "Place: " + place.getName() + ", " + place.getId());
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Log.i(Tag, status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Lat, Lng), 16));

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);

        enableMyLocation();


    }


    //종료버튼
    public void closeBtn(View view) {
        finish();
    }

    //관광지on/off
    public void onofftour(View view) throws SAXException {
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) binding.activitymapReLYBtn1.getLayoutParams();
        if (tourFlag == 0) {
            removeMarker();
            set_area_Sax("tour");
            tourFlag = 1; //현재 on
            foodFlag = 0;
            leisureFlag = 0;
            accomFlag = 0;
            params2.height = afterHeight;
            params2.width = afterWidth;
            binding.activitymapReLYBtn1.setLayoutParams(params2);
//            binding.activitymapReLYBtn1.setWidth
//            binding.activitymapReLYBtn1.setBackgroundColor(Color.WHITE);
//            binding.activitymapReLYBtn2.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn3.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn4.setBackgroundColor(Color.GRAY);
        } else {
            tourFlag = 0; //현재 off
            removeMarker();
//            binding.activitymapReLYBtn1.setBackgroundColor(Color.GRAY);
            params2.height = preHeight;
            params2.width = preWidth;
            binding.activitymapReLYBtn1.setLayoutParams(params2);
        }
    }

    //음식점 on/off
    public void onofffood(View view) throws SAXException {
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) binding.activitymapReLYBtn2.getLayoutParams();
        if (foodFlag == 0) {
            removeMarker();
            set_area_Sax("restaurant");
            foodFlag = 1; //현재 on
            tourFlag = 0;
            leisureFlag = 0;
            accomFlag = 0;
            params2.height = afterHeight;
            params2.width = afterWidth;
            binding.activitymapReLYBtn2.setLayoutParams(params2);
//            binding.activitymapReLYBtn1.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn2.setBackgroundColor(Color.WHITE);
//            binding.activitymapReLYBtn3.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn4.setBackgroundColor(Color.GRAY);
        } else {
            foodFlag = 0; //현재 off
            removeMarker();
//            binding.activitymapReLYBtn3.setBackgroundColor(Color.GRAY);
            params2.height = preHeight;
            params2.width = preWidth;
            binding.activitymapReLYBtn2.setLayoutParams(params2);
        }
    }

    //레저 on/off
    public void onoffleisure(View view) throws SAXException {
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) binding.activitymapReLYBtn3.getLayoutParams();
        if (leisureFlag == 0) {
            removeMarker();
            set_area_Sax("leisure");
            leisureFlag = 1; //현재 on
            tourFlag = 0;
            foodFlag = 0;
            accomFlag = 0;
            params2.height = afterHeight;
            params2.width = afterWidth;
            binding.activitymapReLYBtn3.setLayoutParams(params2);
//            binding.activitymapReLYBtn1.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn2.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn3.setBackgroundColor(Color.WHITE);
//            binding.activitymapReLYBtn4.setBackgroundColor(Color.GRAY);
        } else {
            leisureFlag = 0; //현재 off
            removeMarker();
//            binding.activitymapReLYBtn3.setBackgroundColor(Color.GRAY);
            params2.height = preHeight;
            params2.width = preWidth;
            binding.activitymapReLYBtn3.setLayoutParams(params2);
        }
    }

    //숙박 on/off
    public void onoffAccom(View view) throws SAXException {
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) binding.activitymapReLYBtn4.getLayoutParams();
        if (accomFlag == 0) {
            removeMarker();
            set_area_Sax("accommodations");
            accomFlag = 1;
            leisureFlag = 0;
            tourFlag = 0;
            foodFlag = 0;
            params2.height = afterHeight;
            params2.width = afterWidth;
            binding.activitymapReLYBtn4.setLayoutParams(params2);
//            binding.activitymapReLYBtn1.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn2.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn3.setBackgroundColor(Color.GRAY);
//            binding.activitymapReLYBtn4.setBackgroundColor(Color.WHITE);
        } else {
            accomFlag = 0; //현재 off
            removeMarker();
//            binding.activitymapReLYBtn4.setBackgroundColor(Color.GRAY);
            params2.height = preHeight;
            params2.width = preWidth;
            binding.activitymapReLYBtn4.setLayoutParams(params2);
        }
    }

    void removeMarker() {

        try {
            mMap.clear();
        } catch (NullPointerException e) {
            Log.d(Tag + "_Marker", String.valueOf(e));
        }


    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void set_area_Sax(String w) throws SAXException {

        customProgressDialog.show();

        String get_Url;
        String lat, lng;

        //현재 지도의 위.경도 받아오기

        double x = mMap.getCameraPosition().target.latitude;
        double y = mMap.getCameraPosition().target.longitude;
        lat = String.valueOf((x * 10000000) / 10000000.0);
        lng = String.valueOf((y * 10000000) / 10000000.0);



        switch (w) {
            case "tour":
                tourapi.set_tourList_forMap(lng, lat, radius);
                get_Url = tourapi.getUrl();
                break;
            case "restaurant":
                tourapi.set_foodList_forMap(lng, lat, radius);
                get_Url = tourapi.getUrl();
                break;
            case "leisure":
                tourapi.set_leisure_Sports_List_forMap(lng, lat, radius);
                get_Url = tourapi.getUrl();
                break;
            case "accommodations":
                tourapi.set_accommodations_List_forMap(lng, lat, radius);
                get_Url = tourapi.getUrl();
                break;
            case "search":
                get_Url = keywrdApi.getUrl();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + w);
        }


        Log.d(Tag + "Maplist_Url", get_Url);


        new Thread(() -> {
            // TODO Auto-generated method stub
            try {
                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();
                URL url = new URL(get_Url);//문자열로 된 요청 url을 URL 객체로 생성.
                DataHandler myDataHandler = new DataHandler();
                xmlR.setContentHandler(myDataHandler);
                xmlR.parse(new InputSource(url.openStream()));
                dataList = myDataHandler.getData();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                // TODO Auto-generated method stub

                for (int i = 0; i < dataList.size(); i++) {
                    try {
                        double new_lat = Double.parseDouble(dataList.get(i).getLat());
                        double new_lng = Double.parseDouble(dataList.get(i).getLon());
                        LatLng Point = new LatLng(new_lat, new_lng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(Point);
                        markerOptions.title(dataList.get(i).getTitle());
                        if (tourFlag == 1)
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        else if(foodFlag==1)
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                        else if(leisureFlag==1)
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        else if (accomFlag==1)
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        else
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        markerOptions.alpha((float) 0.7);
                        markerOptions.snippet(dataList.get(i).getTitle() + "#" + dataList.get(i).getContentId());
                        mMap.addMarker(markerOptions);

                    } catch (IndexOutOfBoundsException e) {
                        Log.d("marker error : ", String.valueOf(e));
                    }

                }
                customProgressDialog.dismiss();

            });

        }).start();


    }

    void updateMarker() {
        if (tourFlag == 1) {
            removeMarker();
            try {
                set_area_Sax("tour");
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        if (foodFlag == 1) {
            removeMarker();
            try {
                set_area_Sax("restaurant");
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        if (leisureFlag == 1) {
            removeMarker();
            try {
                set_area_Sax("leisure");
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        if (accomFlag == 1) {
            removeMarker();
            try {
                set_area_Sax("accommodations");
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCameraIdle() {
        updateMarker();
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.d(Tag + "_onMarkerClick", "click");
//        marker.showInfoWindow();
//        marker.isInfoWindowShown();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        binding.bottomsheet.setVisibility(View.VISIBLE);
        contentId = marker.getSnippet().split("#")[1]; //콘텐트아이디 가져옴
        runthread(contentId);

        /*
        STATE_COLLAPSED : height 만큼 보이게 됩니다.
        STATE_EXPANDED : 가득 차게 처리합니다.
        STATE_HIDDEN : 숨김 처리됩니다.
         */
        return true;
    }

    public void runthread(String contentId) {
        customProgressDialog.show();
        TourApi_ tourapi2 = new TourApi_("detailCommon");
        tourapi2.set_cIddetail_Url(contentId);
        Log.d(Tag + "_detailUrl", tourapi2.getUrl());

        new Thread(() -> {
            String title = null, imgUrl = null, overview = null;
            try {
                URL url = new URL(tourapi2.getUrl());//문자열로 된 요청 url을 URL 객체로 생성.
                InputStream is = url.openStream(); //url위치로 입력스트림 연결

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
                                case "title":
                                    xpp.next();
                                    title = xpp.getText();
                                    break;
                                case "firstimage":
                                    xpp.next();
                                    imgUrl = xpp.getText();
                                    break;
                                case "overview":
                                    xpp.next();
                                    overview = xpp.getText();
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
                Log.d(Tag + "_xmlError", String.valueOf(e));
            }


            String finalTitle = title;
            String finalImgUrl = imgUrl;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                overview = Html.fromHtml(overview, Html.FROM_HTML_MODE_LEGACY).toString();
            } else {
                overview = Html.fromHtml(overview).toString();
            }
            String finalOverview = overview;

            runOnUiThread(() -> {
                binding.sheetTitle.setText(finalTitle);
                Glide.with(this).load(finalImgUrl).centerCrop().placeholder(R.drawable.brankimg).into(binding.sheetImg);
                binding.sheetOverview.setEllipsize(TextUtils.TruncateAt.END);
                binding.sheetOverview.setMaxLines(2);
                binding.sheetOverview.setText(finalOverview);

            });
            customProgressDialog.dismiss();
        }).start();
    }


    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "위치이동이 안될 시 위치를 켜주세요.", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onCameraMove() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        CameraPosition cameraPosition = mMap.getCameraPosition();

        if (16 <= cameraPosition.zoom && cameraPosition.zoom <= 18) {
            radius = 1000;
        } else if (13 <= cameraPosition.zoom && cameraPosition.zoom <= 15) {
            radius = 3000;
        } else {
            radius = 5000;
        }


    }

    public void moreSee(View view) {
        Intent intent = new Intent(getApplicationContext(), AreaTripDetailActivity.class);
        intent.putExtra("contentId", contentId);

        startActivity(intent);
    }
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        if(inputManager.isAcceptingText()){
            inputManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS
            );
        }

    }

}