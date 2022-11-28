package com.example.anywhere.main;


public class ItemList {

    String ImgUrl,title,addr,id;

    public ItemList(String title, String ImgUrl, String addr, String id) {
        this.title = title;
        this.ImgUrl=ImgUrl;
        this.addr=addr;
        this.id=id;
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


}
