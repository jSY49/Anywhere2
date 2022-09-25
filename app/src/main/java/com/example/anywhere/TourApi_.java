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

    //구버전 tourAPI 3.0
//    String basicUrl="https://api.visitkorea.or.kr/openapi/service/rest/KorService/";
    //신버전 tourAPI 4.0
    String basicUrl="http://apis.data.go.kr/B551011/KorService/";


    public TourApi_() {
        //String settingUrl="https://api.visitkorea.or.kr/openapi/service/rest/KorService/";
    }

    public TourApi_(String wantService) {
        basicUrl += wantService + "?serviceKey=" + BuildConfig.MY_API_KEY;
    }

    String getUrl(){
        //settingUrl += wantService + "?ServiceKey=" + BuildConfig.MY_API_KEY + "&numOfRows=30&pageNo=1&MobileOS=ETC&MobileApp=AppTest";
        return basicUrl;
    }
    String set_tourdataList_Count_URL(String sp1, @NonNull String sp2){
        if(sp2.equals("-1")){
            basicUrl += "&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp1+"&contentTypeId=12"+"&listYN=N";
        }
        else{
            basicUrl += "&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp1+"&sigunguCode="+sp2+"&contentTypeId=12"+"&listYN=N";
        }

        return basicUrl;
    }
    String set_tourdataList_Url(String sp1, @NonNull String sp2, String srt,String page){
        if(sp2.equals("-1")){
            basicUrl += "&numOfRows=50&pageNo="+page+"&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp1+"&contentTypeId=12"+"&arrange="+srt;
        }
        else{
            basicUrl += "&numOfRows=50&pageNo="+page+"&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp1+"&sigunguCode="+sp2+"&contentTypeId=12"+"&arrange="+srt;//contentTypeId 12는 관광지   arrange는 정렬
        }

        return basicUrl;
    }

    String set_beachList_Url(String sp,String srt){
        basicUrl += "&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp+"&contentTypeId=12"+"&arrange="+srt+"&cat1=A01&cat2=A0101&cat3=A01011200";
        return basicUrl;
    }
    String set_tourList_forMap(String mapX,String mapY,int radius){
        String rad=String.valueOf(radius);
        basicUrl+="&numOfRows=200&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentTypeId=12"+"&mapX="+mapX+"&mapY="+mapY+"&radius="+rad;
        return basicUrl;
    }

    String set_foodList_forMap(String mapX,String mapY,int radius){
        String rad=String.valueOf(radius);
        basicUrl+="&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentTypeId=39"+"&mapX="+mapX+"&mapY="+mapY+"&radius=1000";
        return basicUrl;
    }
    String set_leisure_Sports_List_forMap(String mapX,String mapY,int radius){
        String rad=String.valueOf(radius);
        basicUrl+="&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentTypeId=28"+"&mapX="+mapX+"&mapY="+mapY+"&radius=1000";
        return basicUrl;
    }


    String set_fest_List_Url(){
        basicUrl+="&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentTypeId=15"+"&arrange=B&cat1=A02&cat2=A0207";
        return basicUrl;
    }

    String set_cIddetail_Url(String cId){

        basicUrl +="&numOfRows=1&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentId="+cId+"&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y";
        return basicUrl;
    }




}



