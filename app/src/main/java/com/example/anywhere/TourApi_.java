package com.example.anywhere;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TourApi_ {

    String basicUrl="https://api.visitkorea.or.kr/openapi/service/rest/KorService/";
    String[][] arealist;
    int i;

    public TourApi_() {
        //String settingUrl="https://api.visitkorea.or.kr/openapi/service/rest/KorService/";
    }
    public TourApi_(String wantService) {
        basicUrl += wantService + "?ServiceKey=" + BuildConfig.MY_API_KEY + "&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest";
    }



    String getUrl(){
        //settingUrl += wantService + "?ServiceKey=" + BuildConfig.MY_API_KEY + "&numOfRows=30&pageNo=1&MobileOS=ETC&MobileApp=AppTest";
        return basicUrl;
    }
    
    String set_tourdataList_Url(String sp1, @NonNull String sp2, String srt){
        Log.d("pr_set__Url: ",basicUrl);
        if(sp2.equals("-1")){
            basicUrl += "&areaCode="+sp1+"&contentTypeId=12"+"&arrange="+srt;
        }
        else{
            basicUrl += "&areaCode="+sp1+"&sigunguCode="+sp2+"&contentTypeId=12"+"&arrange="+srt;//contentTypeId 12는 관광지   arrange는 정렬
        }
        Log.d("set_tourdataList_Url: ",basicUrl);
        return basicUrl;
    }

    String set_beachList_Url(String sp,String srt){
        basicUrl += "&areaCode="+sp+"&contentTypeId=12"+"&arrange="+srt+"&cat1=A01&cat2=A0101&cat3=A01011200";
        return basicUrl;
    }
    String set_tourList_forMap(String mapX,String mapY){
        basicUrl+="&contentTypeId=12"+"&mapX="+mapX+"&mapY="+mapY+"&radius=3000";
        return basicUrl;
    }

//    //+ areaCode=??&sigunguCode=??
//    String[][] get_area(String newUrl) throws IOException {
//
//
//        arealist= new String[4][1000];
//
//        int i=0,j=0,k=0,c=0;
//        //StringBuffer buffer=new StringBuffer();
//
//        try {
//            Log.d("totalCount_newUrl1: ",newUrl);
//            URL url= new URL(newUrl);//문자열로 된 요청 url을 URL 객체로 생성.
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000);
//            conn.setConnectTimeout(15000);
//            conn.setRequestMethod("GET");
//            conn.setDoInput(true);
//            conn.connect();
//
//            String line;
//            String result = "";
//
//            BufferedReader bf;
//            bf = new BufferedReader(new InputStreamReader(url.openStream()));
//            while ((line = bf.readLine()) != null) {
//                result = result.concat(line);
//
//            }
//
//            JSONObject root = new JSONObject(result);
//            JSONArray arr = root.getJSONArray("items");
//
//            for(int i = 0; i< arr.length() ; i++){
//                JSONObject item = arr.getJSONObject(i);
//                Log.d("corona",item.getString("name"));
//                corona_item corona_item = new corona_item(
//                        item.getString("lat"),
//                        item.getString("lng"),
//                        item.getString("addr"),
//                        item.getString("code"),
//                        item.getString("created_at"),
//                        item.getString("name"),
//                        item.getString("remain_stat"),
//                        item.getString("stock_at"),
//                        item.getString("type")
//                );
//
//
//
//
//            }
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch blocke.printStackTrace();
//            e.printStackTrace();
//        }
//
//
//        //return buffer.toString();//StringBuffer 문자열 객체 반환
//        return arealist;
//
//    }


    String set_cIddetail_Url(String cId){

        basicUrl +="&contentId="+cId+"&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y";
        Log.d("cId_url",basicUrl);
        return basicUrl;
    }




}



