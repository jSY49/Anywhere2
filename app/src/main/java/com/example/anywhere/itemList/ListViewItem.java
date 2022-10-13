package com.example.anywhere.itemList;


//나한테 알맞게 함수 이름 수정 필.
public class ListViewItem {

    private String titleStr ;
    private String addrStr ;
    private String imgurl ;

    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setaddr(String addrStr) {
        addrStr = addrStr ;
    }
    public void setimgurl(String addimgurlrStr) {
        imgurl = imgurl ;
    }

    public String getTitle() {
        return this.titleStr ;
    }
    public String getaddr() {
        return this.addrStr ;
    }
    public String getimgurl() {
        return this.imgurl ;
    }

    ListViewItem(String imgurl,String titleStr,String addrStr){

        this.imgurl=imgurl;
        this.titleStr=titleStr;
        this.addrStr=addrStr;
    }

}