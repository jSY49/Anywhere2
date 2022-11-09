package com.example.anywhere.itemList;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.anywhere.Connect.TourApi_;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.R;
import com.example.anywhere.itemDetail.AreaTripDetailActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class ListOfAreaTripActivity extends Activity {
    String Tag = "ListOfAreaTripActivity";
    Button bckbtn, searchbtn;
    String sp1 = "1", sp2 = "-1", srt = "O", totalCnt = "0", page = "1";
    int pageNum = 0, totalCntInteger = 0, tmpButton = 1;
    ArrayAdapter<CharSequence> adspin1, adspin2, adsortspin;
    ArrayList<String> listTitle, listimgUrl, listAddr, listId;
    Spinner spin1;
    Spinner spin2;
    Spinner sortspin;
    LinearLayout innerLinear;
    //리스트 뷰
    ListView listview;
    ListViewAdapter adapter;

    String wantService;
    String contenttypeid;

    CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.areatrip);

        //로딩창 객체 생성
        customProgressDialog = new CustomProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Intent secondIntent = getIntent();
        wantService=secondIntent.getStringExtra("wantService");
        contenttypeid=secondIntent.getStringExtra("cId");

        bckbtn = findViewById(R.id.btnback);
        searchbtn = findViewById(R.id.Areasearch_btn);
        spin1 = (Spinner) findViewById(R.id.spinner1);
        spin2 = (Spinner) findViewById(R.id.spinner2);
        sortspin = (Spinner) findViewById(R.id.sortspinenr);
        innerLinear = findViewById(R.id.innerLinear);

        listTitle = new ArrayList<>();
        listAddr = new ArrayList<>();
        listimgUrl = new ArrayList<>();
        listId = new ArrayList<>();

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기

        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);


        adspin1 = ArrayAdapter.createFromResource(this, R.array.firstSelect, android.R.layout.simple_spinner_dropdown_item);//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adspin1);

        adsortspin = ArrayAdapter.createFromResource(this, R.array.sort_list, android.R.layout.simple_spinner_dropdown_item);
        adsortspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortspin.setAdapter(adsortspin);
        sortspin.setSelection(1);


        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//int i는 spinner에서 몇번째걸 선택했는지

                scndSpinner(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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


        sortspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adsortspin.getItem(i).equals("제목순")) {
                    srt = "O";
                } else if (adsortspin.getItem(i).equals("조회순")) {
                    srt = "P";
                } else if (adsortspin.getItem(i).equals("수정일순")) {
                    srt = "Q";
                } else if (adsortspin.getItem(i).equals("생성일순")) {
                    srt = "R";
                }
                adapter.clearAll(); //리스트 뷰를 모두 지우는 함수 호출
                runthread();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //검색 버튼
        searchbtn.setOnClickListener(view -> {
            adapter.clearAll(); //리스트 뷰를 모두 지우는 함수 호출
            page = "1";
            //runthread_pageNum();
            runthread();

        });


        //뒤로가기 버튼
        bckbtn.setOnClickListener(view -> finish());

        //리스트뷰 클릭 했을 때
        listview.setOnItemClickListener((parent, v, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), AreaTripDetailActivity.class);
            intent.putExtra("contentId", listId.get(position));
            startActivity(intent);

        });

    }


    public void runthread() {

        customProgressDialog.show();


        TourApi_ tourapi_listCount = new TourApi_(wantService);
        tourapi_listCount.set_tourdataList_Count_URL(sp1, sp2,contenttypeid);
        String get_Url_listCount = tourapi_listCount.getUrl();

        TourApi_ tourapi = new TourApi_(wantService);
        tourapi.set_tourdataList_Url(sp1, sp2, srt, page,contenttypeid);
        String get_Url = tourapi.getUrl();


        Log.d(Tag+"_getURL", get_Url);


        new Thread(() -> {
            // TODO Auto-generated method stub
            get_listCount(get_Url_listCount);
            totalCntInteger = Integer.parseInt(totalCnt);
            if (totalCntInteger < 50) {
                pageNum = 1;
            } else if (totalCntInteger >= 50) {
                pageNum = totalCntInteger / 50;
                if (totalCntInteger % 50 != 0) {
                    pageNum += 1;
                }
            }

            Log.d(Tag+"_pageNum : " , String.valueOf(pageNum));
            try {
                get_area(get_Url);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


            runOnUiThread(() -> {
                // TODO Auto-generated method stub
                //아이템 추가.
                addButton();

                int i;
                for (i = 0; i < listTitle.size(); i++) {
                    if (listimgUrl.get(i) == null) {
                        listimgUrl.add("brank Img");
//                        break;
                    }
                    adapter.addItem(new ListViewItem(listimgUrl.get(i), listTitle.get(i), listAddr.get(i)));
                }
                listview.setAdapter(adapter);



//                dialog.dismiss();
                customProgressDialog.dismiss();


            });

        }).start();

    }


    public void addButton() {
        if (tmpButton > 1) {
            innerLinear.removeAllViews();
            tmpButton = 1;
        }

        for (int i = 0; i < pageNum; i++) {
            Button pagebutton = new Button(this);
            pagebutton.setText(String.valueOf(i + 1));
            if (String.valueOf(tmpButton).equals(page)) {
                Log.d(Tag + "_tmpBtn", String.valueOf(tmpButton));
                Log.d(Tag + "_page", page);
                pagebutton.setTextColor(Color.BLACK);
                pagebutton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            } else {
                pagebutton.setTextColor(Color.parseColor("#FF6E6E6E"));
            }

            pagebutton.setBackgroundColor(Color.parseColor("#00ff0000"));
            pagebutton.setWidth(10);
//            pagebutton.setId(DYNAMIC_VIEW_ID+i);
            pagebutton.setTag(i + 1);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = 80;
            params.setMargins(10, 0, 10, 0);
            innerLinear.addView(pagebutton, params);
            pagebutton.setOnClickListener(view -> {
                Log.d(Tag + "_pagebuttonId_Click", String.valueOf(pagebutton.getTag()));
                page = String.valueOf(pagebutton.getTag());
                adapter.clearAll();
                //runthread_pageNum();
                runthread();
            });
            tmpButton++;

        }


    }


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

        adspin2 = ArrayAdapter.createFromResource(ListOfAreaTripActivity.this, whichone, android.R.layout.simple_spinner_dropdown_item);
        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adspin2);

    }


    void get_listCount(String Url) {
        try {
            Log.d(Tag+"_totalCount: ", Url);
            URL url = new URL(Url);//문자열로 된 요청 url을 URL 객체로 생성.

            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
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
                            case "totalCnt":
                                xpp.next();
                                totalCnt = xpp.getText();
                                break;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기
                        if (tag.equals("item")) //buffer.append("\n");// 첫번째 검색결과종료..줄바꿈
                            break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
            e.printStackTrace();
        }

    }

    //+ areaCode=??&sigunguCode=??
    void get_area(String newUrl) throws IOException {

        listTitle.clear();
        listimgUrl.clear();
        listAddr.clear();
        listId.clear();

        try {
            URL url = new URL(newUrl);//문자열로 된 요청 url을 URL 객체로 생성.

            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            //xml파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
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
                            case "firstimage":
                                xpp.next();
                                listimgUrl.add(xpp.getText());
                                break;
                            case "title":
                                xpp.next();
                                listTitle.add(xpp.getText());
                                break;
                            case "addr1":
                                xpp.next();
                                listAddr.add(xpp.getText());
                                break;
                            case "contentid":
                                xpp.next();
                                listId.add(xpp.getText());
                                break;
                        }

                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기

                        if (tag.equals("item")) //buffer.append("\n");// 첫번째 검색결과종료..줄바꿈

                            break;

                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
            e.printStackTrace();
        }

    }


}

/*
JDK에서 지원하는 java.lang.Thread 제공


<Thread 생성자>
 Thread()
 Thread(String s) 스레드 이름
 Thread(Runnable r) 인터페이스 객체        //메소드 run만 정의되어 있음
 Thread(Runnable r, String s) 인터페이스 객체와 스레드 이름

<Thread 메소드>
 static void sleep(long msec) throws Interrupted Exception  msec에 지정된 밀리초 동안 대기
 String getName() 스레드의 이름을 s로 설정
 void setName(String s) 스레드의 이름을 s로 설정
 void start() 스레드를 시작 run() 메소드 호출
 int getPriority()  스레드의 우선 순위를 반환
 void setpriority(int p) 스레드의 우선순위를 p값으로
 boolean isAlive() 스레드가 시작되었고 아직 끝나지 않았으면 true 끝났으면 false 반환
 void join() throws InterruptedException 스레드가 끝날 때 까지 대기
 void run() 스레드가 실행할 부분 기술 (오버라이딩 사용)
 void suspend() 스레드가 일시정지 resume()에 의해 다시시작 할 수 있다.
 void resume() 일시 정지된 스레드를 다시 시작.
 void yield() 다른 스레드에게 실행 상태를 양보하고 자신은 준비 상태로
 */