package com.example.anywhere;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.anywhere.databinding.ActivitymapBinding;
import com.google.android.gms.maps.model.Marker;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements MapView.MapViewEventListener
{
    private MapView mapView;

    double Lat=37.510759,Lng=126.977943;
    private ActivitymapBinding binding;
    ArrayList<String> listTitle,listlat,listlng,listId;
    String lat,lng;
    private int tourflag=0,foodflag=0; //0이면 off/ 1이면 on 상태

    private ArrayList<MapPOIItem> POI_tour=new ArrayList<MapPOIItem>() ;
    private ArrayList<MapPOIItem> POI_rest=new ArrayList<MapPOIItem>() ;

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

        mapView = new MapView(MapActivity.this);
        mapView.setMapViewEventListener(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Lat, Lng), 5, true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);

    }

    public void closeBtn(View view) {
        listTitle.clear();
        listlat.clear();
        listlng.clear();
        listId.clear();
        finish();

    }

    //각각 온오프를 빠르게 하면 마커 색상이 동일하게 변경되는 오류가 있음.
    public void onofftour(View view){
        if(tourflag==0){
            tourflag=1; //현재 on
            get_list("tour");
            binding.activitymapReLYBtn1.setBackgroundColor(Color.rgb(255,255,255));
        }
        else{
            tourflag=0; //현재 off
            removeMarkerEach();
            binding.activitymapReLYBtn1.setBackgroundColor(Color.rgb(187,134,252));
        }
    }

    public void onofffood(View view){
        if(foodflag==0){
            foodflag=1; //현재 on
            get_list("restaurant");
            binding.activitymapReLYBtn2.setBackgroundColor(Color.rgb(255,255,255));
        }
        else{
            foodflag=0; //현재 off
            removeMarkerEach();
            binding.activitymapReLYBtn2.setBackgroundColor(Color.rgb(187,134,252));
        }
    }

    //여기 문제 있음
    public void markerupdate(View view){
        removeMarkerAll();
        if(tourflag==1&&foodflag==1){
            get_list("tour");
            get_list("restaurant");
        }
//        else if(tourflag==1||foodflag==1){
//            if(tourflag==1){
//                get_list("tour");
//            }
//            else if(foodflag==1){
//                get_list("restaurant");
//            }
//
//        }

        binding.updateBtn.setVisibility(View.INVISIBLE);
    }

    private void get_list(String w) {

        String get_Url;
        POI_rest.clear();
        POI_tour.clear();

        //현재 지도의 위.경도 받아오기
        MapPoint mapPoint = mapView.getMapCenterPoint();
        double x=mapPoint.getMapPointGeoCoord().latitude;
        double y=mapPoint.getMapPointGeoCoord().longitude;
        lat = String.valueOf((x*10000000)/10000000.0);
        lng = String.valueOf((y*10000000)/10000000.0);

        //해당하는 url 가져오기
        TourApi_ tourapi=new TourApi_("locationBasedList");
        if(w.equals("tour")){
            tourapi.set_tourList_forMap(lng,lat);
        }
        else if(w.equals("restaurant")){
            tourapi.set_foodList_forMap(lng,lat);
        }
        get_Url=tourapi.getUrl();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    set_area(get_Url);
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
                                MapPoint MARKER_POINT=MapPoint.mapPointWithGeoCoord(new_lat, new_lng);
                                if(w.equals("tour")){
                                    POI_tour.add(new MapPOIItem());
                                    POI_tour.get(i).setItemName(listTitle.get(i));
                                    POI_tour.get(i).setTag(0);
                                    POI_tour.get(i).setMapPoint(MARKER_POINT);
                                    POI_tour.get(i).setMarkerType(MapPOIItem.MarkerType.YellowPin);
                                    POI_tour.get(i).setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                                    mapView.addPOIItem(POI_tour.get(i));
                                }

                                else if(w.equals("restaurant")) {
                                    POI_rest.add(new MapPOIItem());
                                    POI_rest.get(i).setItemName(listTitle.get(i));
                                    POI_rest.get(i).setTag(0);
                                    POI_rest.get(i).setMapPoint(MARKER_POINT);
                                    POI_rest.get(i).setMarkerType(MapPOIItem.MarkerType.BluePin);
                                    POI_rest.get(i).setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                                    mapView.addPOIItem(POI_rest.get(i));
                                }

//
//                                MapPOIItem marker1 = new MapPOIItem();
//
//                                marker1.setItemName(listTitle.get(i));
//                                marker1.setTag(0);
//                                marker1.setMapPoint(MARKER_POINT);
//                                if(w.equals("tour"))
//                                    marker1.setMarkerType(MapPOIItem.MarkerType.YellowPin);
//                                else if(w.equals("restaurant"))
//                                    marker1.setMarkerType(MapPOIItem.MarkerType.BluePin);
//                                marker1.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
//                                mapView.addPOIItem(marker1);
                            }
                        }

                    }
                });

            }
        }).start();
    }


    private void set_area(String newUrl) throws IOException {

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


    private void removeMarkerAll(){
        mapView.removeAllPOIItems();

    }

    private void removeMarkerEach() {
        //관광지 마커 제거
        //각각 하나씩 on/off시
        if(tourflag==0){
            for(int i=0;i<POI_tour.size();i++)
                mapView.removePOIItem(POI_tour.get(i));
            POI_tour.clear();
        }

        if(foodflag==0){
            for(int i=0;i<POI_rest.size();i++)
                mapView.removePOIItem(POI_rest.get(i));
            POI_rest.clear();
        }
        else{
            mapView.removeAllPOIItems();
        }

    }

//    public void removePOIItems(ArrayList<MapPOIItem> poiItems){
//        removePOIItems(POI_tour);
//        POI_tour.clear();
//    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        if(tourflag==1||foodflag==1){
            binding.updateBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }



}
