package com.example.anywhere;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.anywhere.databinding.ActivityBeachlistBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class BeachListActivity extends AppCompatActivity {

    private ActivityBeachlistBinding binding;
    String sp="0",srt="0";
    ArrayList<String> listTitle,listimgUrl,listAddr,listId;
    ArrayAdapter<CharSequence> adspin1,adsortspin;

    //리스트 뷰
    ListView listview ;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBeachlistBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CheckTypesTask task = new CheckTypesTask();
        task.execute();

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        binding.ActivityBeachlistListview.setAdapter(adapter);

        listTitle=new ArrayList<String>();
        listAddr=new ArrayList<String>();
        listimgUrl=new ArrayList<String>();
        listId=new ArrayList<String>();


        adspin1 = ArrayAdapter.createFromResource(this, R.array.beachsearch, android.R.layout.simple_spinner_dropdown_item);//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ActivityBeachlistSpinner1.setAdapter(adspin1);

        adsortspin = ArrayAdapter.createFromResource(this, R.array.sort_list, android.R.layout.simple_spinner_dropdown_item);
        adsortspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ActivityBeachlistSortspinenr.setAdapter(adsortspin);

        binding.ActivityBeachlistSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//int i는 spinner에서 몇번째걸 선택했는지

                Log.d("spinner",String.valueOf(i));
                sp=spinnersetting(String.valueOf(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.ActivityBeachlistSortspinenr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adsortspin.getItem(i).equals("제목순")){
                    srt="O";
                }
                else if(adsortspin.getItem(i).equals("조회순")){
                    srt="P";
                }
                else if(adsortspin.getItem(i).equals("수정일순")){
                    srt="Q";
                }
                else if(adsortspin.getItem(i).equals("생성일순")){
                    srt="R";
                }
                runthread();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //검색 버튼
        binding.ActivityBeachlistReasearchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                adapter.clearAll(); //리스트 뷰를 모두 지우는 함수 호출
                runthread();

                CheckTypesTask task = new CheckTypesTask();
                task.execute();
            }
        });
        //리스트뷰 클릭 했을 때
        binding.ActivityBeachlistListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), areatripDetail.class);
                intent.putExtra("contentId",listId.get(position));

                startActivity(intent);

            }
        }) ;

        //뒤로가기 버튼
        binding.activityBeachlistBtnback.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();

            }
        });

    }

    String spinnersetting(String spinner){
        switch (spinner){
            case "0":  sp="2";break;
            case "1": sp="6";break;
            case "2": sp="7"; break;
            case "3": sp="31";break;
            case "4": sp="32";break;
            case "5": sp="34";break;
            case "6": sp="35";break;
            case "7": sp="36";break;
            case "8": sp="37";break;
            case "9": sp="38";break;
            case "10": sp="39";break;
        }

        return sp;
    }

    public void runthread(){

        TourApi_ tourapi=new TourApi_("areaBasedList");
        tourapi.set_beachList_Url(sp,srt);
        String get_Url=tourapi.getUrl();

        Log.d("set_tourdataList_getURL",get_Url);
        adapter.clearAll(); //리스트 뷰를 모두 지우는 함수 호출


        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                   get_area(get_Url);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        //아이템 추가.
                        int i=0,j=0;
                        for(i=0;i<listTitle.size();i++){
                            if(listimgUrl.get(i)==null)
                                break;
                            else{
                                adapter.addItem(new ListViewItem(listimgUrl.get(i), listTitle.get(i), listAddr.get(i))) ;
                            }
                        }
                        Log.d("HowmanyI ", String.valueOf(i));
                        binding.ActivityBeachlistListview.setAdapter(adapter);


                        //페이지 이동 버튼
                    }
                });

            }
        }).start();
    }

    void get_area(String newUrl) throws IOException {

        listTitle.clear();
        listimgUrl.clear();
        listAddr.clear();
        listId.clear();

        try {
            Log.d("totalCount_newUrl2: ",newUrl);
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
                        else if(tag.equals("firstimage")){
                            xpp.next();
                            listimgUrl.add(xpp.getText());
                        }
                        else if(tag.equals("title")){
                            xpp.next();
                            listTitle.add(xpp.getText());
                        }
                        else if(tag.equals("addr1")){
                            xpp.next();
                            listAddr.add(xpp.getText());
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


    private class CheckTypesTask extends AsyncTask<Void,Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(BeachListActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다.");
            // Show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i=0;i<5;i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}

