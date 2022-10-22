package com.example.anywhere.Booking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anywhere.Connect.firebaseConnect;
import com.example.anywhere.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity {

    String Tag="BookingActivity";
    Spinner DoSpin;
    Spinner SiSpin;
    Spinner ThemeSpin;
    ArrayAdapter<CharSequence> DoSpinAdapter, SiSpinAdapter, ThemeSpinAdapter;
    String sp1 = "0", sp2 = "0", sp3 = "0";
    String sp1V="",sp2V="",sp3V="";

    private RecyclerView recyclerView;
    private BookingAdapter Adapter;
    private RecyclerView.LayoutManager layoutManager;
    private firebaseConnect fbsconnect;
    private ArrayList<item> list;


    public static class item {
        String title, price,addr,img,code;

        public String getImg() {
            return img;
        }

        public String getTitle() {
            return title;
        }

        public String getPrice() {
            return price;
        }

        public String getAddr() {
            return addr;
        }

        public String getCode() {
            return code;
        }

        public item(String title, String price, String addr, String img, String code) {
            this.title = title;
            this.price=price;
            this.addr=addr;
            this.img= img;
            this.code=code;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        SpinnerSetting();
        ItemSetting();
        dbSetting(sp1,sp2,sp3);
    }
    public void search(View view) {

       dbSetting(sp1,sp2,sp3);

    }

    private void ItemSetting() {
//        Adapter=new BookingAdapter();
        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();

        recyclerView = findViewById(R.id.BookingRecyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void dbSetting(String SP1,String SP2,String SP3) {
        list=new ArrayList<>();

        fbsconnect.db.collection("product")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = String.valueOf(document.get("title"));
                            String price = document.get("price") + "원";
                            String addr=String.valueOf(document.get("totalAddr"));
                            String img= String.valueOf(document.get("img"));
                            String code=document.getId();

                            if(!SP1.equals("0")||!SP2.equals("0")||!SP3.equals("0")){
                                String Addr1= String.valueOf(document.get("Addr1"));
                                String Addr2= String.valueOf(document.get("Addr2"));
                                String Theme= String.valueOf(document.get("theme"));
                                if(!SP1.equals("0")&&SP2.equals("0")&&SP3.equals("0")){//SP1만 선택
                                    if(Addr1.equals(sp1V))
                                        list.add(new item(title,price,addr,img,code));
                                }
                                else if(!SP1.equals("0")&&SP2.equals("0")&&!SP3.equals("0")){//SP1,SP3만 선택
                                    if(Addr1.equals(sp1V)&&Theme.equals(sp3V))
                                        list.add(new item(title,price,addr,img,code));
                                }
                                else if(!SP1.equals("0")&&!SP2.equals("0")&&SP3.equals("0")){//SP1,SP2만 선택
                                    if(Addr1.equals(sp1V)&&Addr2.equals(sp2V))
                                        list.add(new item(title,price,addr,img,code));

                                }else if(SP1.equals("0")&&SP2.equals("0")&&!SP3.equals("0")){//SP3만 선택
                                    if(Theme.equals(sp3V))
                                        list.add(new item(title,price,addr,img,code));

                                }else {//모두 선택 
                                    if(Addr1.equals(sp1V)&&Addr2.equals(sp2V)&&Theme.equals(sp3V))
                                        list.add(new item(title,price,addr,img,code));
                                }
                            }
                            else{   //아무것도 선택x (기본 전체-전체-전체)
                                list.add(new item(title,price,addr,img,code));
                            }


                        }
                        // 어댑터 세팅
                        Adapter = new BookingAdapter(list,getApplicationContext());
                        recyclerView.setAdapter(Adapter);

                    } else {

                        Log.d(Tag, "Error getting documents: ", task.getException());
                    }
                });
    }



    private void SpinnerSetting()  {
        DoSpin=findViewById(R.id.sp1);
        SiSpin=findViewById(R.id.sp2);
        ThemeSpin=findViewById(R.id.sp3);

        DoSpinAdapter = ArrayAdapter.createFromResource(this, R.array.firstSelectForBooking, android.R.layout.simple_spinner_dropdown_item);//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양
        DoSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DoSpin.setAdapter(DoSpinAdapter);

        ThemeSpinAdapter = ArrayAdapter.createFromResource(this, R.array.ProductTheme, android.R.layout.simple_spinner_dropdown_item);
        ThemeSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ThemeSpin.setAdapter(ThemeSpinAdapter);

        DoSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//int i는 spinner에서 몇번째걸 선택했는지
                sp1= String.valueOf(i);
                sp1V=DoSpin.getSelectedItem().toString();
                scndSpinner(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SiSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sp2V=SiSpin.getSelectedItem().toString();
               sp2= String.valueOf(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ThemeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sp3V=ThemeSpin.getSelectedItem().toString();
                sp3= String.valueOf(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    public void backBtn(View view) {finish();}
    public void scndSpinner(int i) {

        int whichone = R.array.ProductBasic;  //기본
        switch (i) {
            case 1: whichone = R.array.second_seoul; break;
            case 2: whichone = R.array.second_incheon;   break;
            case 3: whichone = R.array.second_daejeon;    break;
            case 4: whichone = R.array.second_daegu;      break;
            case 5: whichone = R.array.second_guangju;    break;
            case 6: whichone = R.array.second_busan;      break;
            case 7: whichone = R.array.second_ulsan;      break;
            case 8: whichone = R.array.second_sejong;     break;
            case 9: whichone = R.array.second_geonggi;   break;
            case 10: whichone = R.array.second_gangwon;  break;
            case 11:whichone = R.array.second_chungbuk;  break;
            case 12:whichone = R.array.second_chungnam;  break;
            case 13:whichone = R.array.second_gyeongbuk; break;
            case 14:whichone = R.array.second_gyeongnam; break;
            case 15:whichone = R.array.second_jeonbuk;   break;
            case 16:whichone = R.array.second_jeonnam;   break;
            case 17:whichone = R.array.second_jeju;      break;
        }

        SiSpinAdapter = ArrayAdapter.createFromResource(this, whichone, android.R.layout.simple_spinner_dropdown_item);
        SiSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SiSpin.setAdapter(SiSpinAdapter);

    }
}
