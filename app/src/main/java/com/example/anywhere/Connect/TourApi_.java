package com.example.anywhere.Connect;

import androidx.annotation.NonNull;

import com.example.anywhere.BuildConfig;

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

    public String getUrl(){
        //settingUrl += wantService + "?ServiceKey=" + BuildConfig.MY_API_KEY + "&numOfRows=30&pageNo=1&MobileOS=ETC&MobileApp=AppTest";
        return basicUrl;
    }
    public void set_tourdataList_Count_URL(String sp1, @NonNull String sp2,String cId){
        if(sp2.equals("-1")){
            basicUrl += "&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp1+"&contentTypeId="+cId+"&listYN=N";
        }
        else{
            basicUrl += "&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp1+"&sigunguCode="+sp2+"&contentTypeId="+cId+"&listYN=N";
        }

    }
    public void set_tourdataList_Url(String sp1, @NonNull String sp2, String srt,String page,String cId){
        if(sp2.equals("-1")){
            basicUrl += "&numOfRows=50&pageNo="+page+"&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp1+"&contentTypeId="+cId+"&arrange="+srt;
        }
        else{
            basicUrl += "&numOfRows=50&pageNo="+page+"&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp1+"&sigunguCode="+sp2+"&contentTypeId="+cId+"&arrange="+srt;//contentTypeId 12는 관광지   arrange는 정렬
        }

    }

    public void set_beachList_Url(String sp,String srt){
        basicUrl += "&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&areaCode="+sp+"&contentTypeId=12"+"&arrange="+srt+"&cat1=A01&cat2=A0101&cat3=A01011200";
    }
    public void set_tourList_forMap(String mapX, String mapY, int radius){
        String rad=String.valueOf(radius);
        basicUrl+="&numOfRows=200&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentTypeId=12"+"&mapX="+mapX+"&mapY="+mapY+"&radius="+rad;
    }

    public void set_foodList_forMap(String mapX,String mapY,int radius){
        String rad=String.valueOf(radius);
        basicUrl+="&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentTypeId=39"+"&mapX="+mapX+"&mapY="+mapY+"&radius=1000";
    }
    public void set_leisure_Sports_List_forMap(String mapX,String mapY,int radius){
        String rad=String.valueOf(radius);
        basicUrl+="&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentTypeId=28"+"&mapX="+mapX+"&mapY="+mapY+"&radius=1000";
    }
    public void set_accommodations_List_forMap(String mapX,String mapY,int radius){
        String rad=String.valueOf(radius);
        basicUrl+="&numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentTypeId=32"+"&mapX="+mapX+"&mapY="+mapY+"&radius=1000";
    }


    public void set_fest_List_Top30_Url(String startDate,String endDate){
        basicUrl+="&numOfRows=30&pageNo=1&MobileOS=ETC&MobileApp=AppTest&listYN=Y&eventStartDate="+startDate+"&eventEndDate="+endDate+"&arrange=P";
    }

    public void set_fest_List_Url(String startDate,String endDate,String sp1,String sp2,String page){
        if(sp2.equals("-1")){
            basicUrl+="&numOfRows=50&pageNo=1&MobileOS=ETC&MobileApp=AppTest&listYN=Y&eventStartDate="+startDate+"&eventEndDate="+endDate+"&arrange=P&areaCode="+sp1;
        }
        else{
            basicUrl+="&numOfRows=50&pageNo=1&MobileOS=ETC&MobileApp=AppTest&listYN=Y&eventStartDate="+startDate+"&eventEndDate="+endDate+"&arrange=P&areaCode="+sp1+"&sigunguCode="+sp2;
        }

    }

    public void set_fest_Count_List_Url(String startDate,String endDate,String sp1,String sp2){
        if(sp2.equals("-1")){
            basicUrl+="&MobileOS=ETC&MobileApp=AppTest&listYN=Y&eventStartDate="+startDate+"&eventEndDate="+endDate+"&arrange=P&areaCode="+sp1;
        }
        else{
            basicUrl+="&MobileOS=ETC&MobileApp=AppTest&listYN=Y&eventStartDate="+startDate+"&eventEndDate="+endDate+"&arrange=P&areaCode="+sp1+"&sigunguCode="+sp2;
        }

    }


    //https://apis.data.go.kr/B551011/KorService/searchFestival?serviceKey=GkxH72IrlxUiXOuSOz8x6jiRGHtpJVy7WJulZjSPj4r%2F8oHi2DeuiILUn6de5ODPCKkVIOnCzwoFLzz72AuXdQ%3D%3D
    // &numOfRows=1000&pageNo=1&MobileOS=ETC&MobileApp=AppTest&listYN=Y&eventStartDate=20220901&eventEndDate=20221001&arrange=P

    public void set_cIddetail_Url(String cId){
        basicUrl +="&numOfRows=1&pageNo=1&MobileOS=ETC&MobileApp=AppTest"+"&contentId="+cId+"&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y";
    }

    public void set_detailintro_Url(String cId,String ctId){
        basicUrl +="&MobileOS=ETC&MobileApp=AppTest"+"&contentId="+cId+"&contentTypeId="+ctId;
    }

    public void set_totalList_URL(String cId,String keyword){
        basicUrl += "&MobileOS=ETC&MobileApp=AppTest&numOfRows=50"+"&contentTypeId="+cId+"&listYN=Y"+"&keyword="+keyword;

    }
}



