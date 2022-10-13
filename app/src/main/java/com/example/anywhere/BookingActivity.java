package com.example.anywhere;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {


    Spinner DoSpin;
    Spinner SiSpin;
    Spinner ThemeSpin;
    ArrayAdapter<CharSequence> DoSpinAdapter, SiSpinAdapter, ThemeSpinAdapter;
    String sp1 = "1", sp2 = "-1", sp3 = "O";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        DoSpin=findViewById(R.id.sp1);
        SiSpin=findViewById(R.id.sp2);
        ThemeSpin=findViewById(R.id.sp3);

        DoSpinAdapter = ArrayAdapter.createFromResource(this, R.array.firstSelect, android.R.layout.simple_spinner_dropdown_item);//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양
        DoSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DoSpin.setAdapter(DoSpinAdapter);

        ThemeSpinAdapter = ArrayAdapter.createFromResource(this, R.array.ProductTheme, android.R.layout.simple_spinner_dropdown_item);
        ThemeSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ThemeSpin.setAdapter(ThemeSpinAdapter);

        DoSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//int i는 spinner에서 몇번째걸 선택했는지

                scndSpinner(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SiSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0)
                    sp2 = String.valueOf(-1);
                else
                    sp2 = String.valueOf(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void backBtn(View view) {finish();}
    public void scndSpinner(int i) {

        int whichone = R.array.second_seoul;  //기본
        switch (i) {
            case 0: whichone = R.array.second_seoul;    sp1 = "1";  break;
            case 1: whichone = R.array.second_incheon;  sp1 = "2";  break;
            case 2: whichone = R.array.second_daejeon;  sp1 = "3";  break;
            case 3: whichone = R.array.second_daegu;    sp1 = "4";  break;
            case 4: whichone = R.array.second_guangju;  sp1 = "5";  break;
            case 5: whichone = R.array.second_busan;    sp1 = "6";  break;
            case 6: whichone = R.array.second_ulsan;    sp1 = "7";  break;
            case 7: whichone = R.array.second_sejong;   sp1 = "8";  break;
            case 8: whichone = R.array.second_geonggi;  sp1 = "31"; break;
            case 9: whichone = R.array.second_gangwon;  sp1 = "32"; break;
            case 10:whichone = R.array.second_chungbuk; sp1 = "33"; break;
            case 11:whichone = R.array.second_chungnam; sp1 = "34"; break;
            case 12:whichone = R.array.second_gyeongbuk;sp1 = "35"; break;
            case 13:whichone = R.array.second_gyeongnam;sp1 = "36"; break;
            case 14:whichone = R.array.second_jeonbuk;  sp1 = "37"; break;
            case 15:whichone = R.array.second_jeonnam;  sp1 = "38"; break;
            case 16:whichone = R.array.second_jeju;     sp1 = "39"; break;
        }

        SiSpinAdapter = ArrayAdapter.createFromResource(this, whichone, android.R.layout.simple_spinner_dropdown_item);
        SiSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SiSpin.setAdapter(SiSpinAdapter);

    }
}