package com.example.anywhere.Festival;

public class FestivalList {

    String ImgUrl,title,addr,id,start,end;

    public FestivalList(String title,String ImgUrl, String addr,String id,String start,String end) {
        this.title = title;
        this.ImgUrl=ImgUrl;
        this.addr=addr;
        this.id=id;
        this.start=start;
        this.end=end;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl(){
        return ImgUrl;
    }

    public String getAddr() {
        return addr;
    }

    public String getId() {
        return id;
    }
    public String getStart(){
        return start;
    }
    public String getEnd(){
        return end;
    }

}
