package com.example.anywhere.Booking;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anywhere.BuildConfig;
import com.example.anywhere.Connect.firebaseConnect;
import com.example.anywhere.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.BootpayAnalytics;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class productBookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ImageView datePick, timePick;
    TextView dateText, timeText, priceText;
    EditText humanText, nameText,phoneText;
    Spinner PGspin;
    ArrayAdapter<CharSequence> adspin;

    ScrollView layout;
    String code, price, epEmail, itemName;
    LinearLayout innerLinear;
    private firebaseConnect fbsconnect;
    ArrayList<String> timeSet, alreadyBookTime, alreadyBookUser;
    String date,time,userEmail,userName,humanCount,phoneNum,OrderId;
    String Tag = "productBookingActivity";
    private String bootKey= BuildConfig.BOOT_APP_ID;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_booking);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        price = intent.getStringExtra("price");
        epEmail = intent.getStringExtra("epEmail");
        itemName = intent.getStringExtra("itemName");

        timeSet = new ArrayList<>();
        alreadyBookTime = new ArrayList<>();
        alreadyBookUser = new ArrayList<>();

        datePick = findViewById(R.id.date_btn);
        timePick = findViewById(R.id.time_btn);
        dateText = findViewById(R.id.date_text_pick);
        timeText = findViewById(R.id.time_text_pick);
        layout = findViewById(R.id.scrollView);
        priceText = findViewById(R.id.price_text);
        humanText = findViewById(R.id.human_text_pick);
        innerLinear = findViewById(R.id.timeDataLay);
        nameText = findViewById(R.id.name_text_pick);
        phoneText= findViewById(R.id.PhoneNumber_text_pick);
        PGspin = (Spinner) findViewById(R.id.spinner1);

        phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());   //번호입력 자동 하이픈
        adspin = ArrayAdapter.createFromResource(this, R.array.method, android.R.layout.simple_spinner_dropdown_item);
        adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PGspin.setAdapter(adspin);

        // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        // 앱에서 확인하지 말고 꼭 웹 사이트에서 확인하자. 앱의 application id 갖다 쓰면 안됨!!!
        BootpayAnalytics.init(this,bootKey);

        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();

        //DatePickerDialog생성
        datePick.setOnClickListener(view -> {

            Calendar myCalendar = Calendar.getInstance();
            int year = myCalendar.get(Calendar.YEAR);
            int month = myCalendar.get(Calendar.MONTH);
            int DAY = myCalendar.get(Calendar.DAY_OF_MONTH);

            Calendar minDate= Calendar.getInstance();
            minDate.set(year,month,DAY+1);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, DAY);
            datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());
            datePickerDialog.show();
        });
        //키보드 밖 클릭 시 키보드 숨김
        layout.setOnTouchListener((v, event) -> {
            try {
                hideKeyboard();
                updatePrice();
            } catch (Exception e) {
                Log.d(Tag, "layTouchException", e);
            }


            return false;
        });
        humanText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력난에 변화가 있을 시 조치
                updatePrice();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때 조치
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에 조치
            }
        });
        timePick.setOnClickListener(view2 -> {

            if (dateText.getText().toString().isEmpty()) {
                Toast.makeText(productBookingActivity.this, "예약 날짜를 먼저 선택해 주세요", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> {
                    getDatelist();

                    runOnUiThread(() -> {
                        innerLinear.removeAllViews();
                        setTimeButton();
                        timeSet.clear();

                    });
                }).start();
            }
        });
    }

    //시간선택 버튼 생성
    private void setTimeButton() {

        Collections.sort(timeSet);
        Collections.sort(alreadyBookTime);
        for (int i = 0; i < alreadyBookTime.size(); i++)
            Log.d(Tag, "이미예약시간=" + alreadyBookTime.get(i));


        for (int i = 0; i < timeSet.size(); i++) {
            Button timeBtn = new Button(this);
            timeBtn.setText(timeSet.get(i));
            timeBtn.setWidth(10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 10, 10, 10);

            if (alreadyBookTime.contains(timeSet.get(i))) {
                timeBtn.setTextColor(Color.parseColor("#8C8C8C"));
                timeBtn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                innerLinear.addView(timeBtn, params);
                timeBtn.setOnClickListener(view -> {
                    Toast.makeText(productBookingActivity.this, "이미 예약되어있는 시간입니다.\n다른 시간을 골라주세요", Toast.LENGTH_SHORT).show();
                });
            } else {
                timeBtn.setBackgroundColor(Color.parseColor("#E8D9FF"));
                innerLinear.addView(timeBtn, params);
                timeBtn.setOnClickListener(view -> {
                    timeText.setText(timeBtn.getText());
                    innerLinear.removeAllViews();
                });
            }

        }

    }

    //예약시간 리스트 가져옴
    @SuppressLint("ResourceType")
    private void getDatelist() {
        //그냥데이터 추가용
//        DocumentReference db = fbsconnect.db.collection("bookingList").document(epEmail);
//        Map<String, Object> data = new HashMap<>();
//        data.put("t1", "10:00");
//        data.put("t2", "15:30");
//        data.put("t3", "18:00");
//        data.put("t5", "20:00");
//        data.put("t6", "22:00");
//        db.collection("20221016_1")
//                .document("timeset").set(data);

        DocumentReference docRef = fbsconnect.db.collection("bookingList").document(epEmail)
                .collection(code).document("timeset");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String timeList = String.valueOf(document.getData().values());
                    StringTokenizer tk = new StringTokenizer(timeList, ",[] ");
                    int i = 0;
                    while (tk.hasMoreTokens()) {  //다음 토큰이 있다면
                        timeSet.add(tk.nextToken());
                        //Log.d(Tag, timeSet.get(i++));
                    }
                } else {
                    Log.d(Tag, "No such document");
                }
            } else {
                Log.d(Tag, "get failed with ", task.getException());
            }
        });
        getCantBookinglist();
    }

    //인원별 가격 업데이트
    private void updatePrice() {
        int p, num;
        if (humanText.getText().toString().isEmpty())
            num = 0;
        else
            num = Integer.parseInt(humanText.getText().toString());
        p = Integer.parseInt(price);
        p = p * num;
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(p);
        priceText.setText(formattedStringPrice + "원");
    }

    public void backBtn(View view) {
        finish();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        timeText.setText("");
        timeSet.clear();
        alreadyBookTime.clear();
        alreadyBookUser.clear();
        dateText.setText(i + "." + (i1 + 1) + "." + i2);
    }

    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    //예약 버튼 클릭 이벤트
    public void BookBtn(View view) {

        date = dateText.getText().toString();
        time = timeText.getText().toString();
        userEmail = fbsconnect.fb_userEmail();
        userName = nameText.getText().toString();
        humanCount = humanText.getText().toString();
        phoneNum=phoneText.getText().toString();

        OrderId=date+time;
        OrderId = OrderId.replaceAll("\\.", "");
        OrderId = OrderId.replaceAll("\\:", "");
        Log.d(Tag, "OrderId=" + OrderId);

        int i;

        for (i = 0; i < alreadyBookUser.size(); i++) {
            if (alreadyBookUser.get(i).equals(userEmail)) {
                Toast.makeText(this, "이미 같은 날 예약이 되어 있습니다.\n기존 예약을 취소하고 진행해 주세요", Toast.LENGTH_LONG).show();
                break;
            }
        }
        Log.d(Tag, String.valueOf(i));
        if (i >= alreadyBookUser.size()) {
            if (date.isEmpty() || time.isEmpty() || userName.isEmpty() || humanCount.isEmpty()) {
                Toast.makeText(this, "항목을 모두 입력해 주세요.", Toast.LENGTH_LONG).show();
            } else {
                tryPay();

            }
        }

    }
    
    //결제시도
    private void tryPay() {


        BootUser user = new BootUser().setPhone(phoneNum); // 구매자 정보

        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)

        String pg = BootpayValueHelper.pgToString("이니시스");
        String method = BootpayValueHelper.methodToString(PGspin.getSelectedItem().toString());

        List items = new ArrayList<>();
        BootItem item1 = new BootItem().setName(itemName).setId(code).setQty(1).setPrice(Double.valueOf(price));
        items.add(item1);

        Payload payload = new Payload();
        payload.setApplicationId(bootKey)
                .setOrderName("부트페이 결제테스트")
                .setPg(pg)
                .setMethod(method)
                .setOrderId(OrderId)
                .setPrice(Double.valueOf(price))
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

//        Map map = new HashMap<>();
//        map.put("1", "abcdef");
//        map.put("2", "abcdef55");
//        map.put("3", 1234);
//        payload.setMetadata(map);


        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        Log.d("bootpay", "cancel: " + data);
                    }

                    @Override
                    public void onError(String data) {
                        Log.d("bootpay", "error: " + data);
                    }

                    @Override
                    public void onClose() {
                        Bootpay.removePaymentWindow();
                    }

//                    @Override
//                    public void onClose(String data) {
//                        Log.d("bootpay", "close: " + data);
//                        Bootpay.removePaymentWindow();
//                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " +data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("bootpay", "confirm: " + data);
//                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                        return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d(Tag,"Pay done="+data);
                        setPayResDatabase();
                    }
                }).requestPayment();

    }

    //결제 결과 db저장
    private void setPayResDatabase() {


        FirebaseFirestore db = fbsconnect.db.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("time", time);
        data.put("peopleNumber", humanCount);
        data.put("pay", priceText.getText().toString());
        data.put("name", userName);
        data.put("BookingTime", getTime());
        data.put("pNum",phoneNum);


        db.collection("bookingpaydone").document(code).collection(date)
                .document(userEmail)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(productBookingActivity.this, "예약이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.d(Tag, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> Log.w(Tag, "Error writing document", e));

        data.put("email", userEmail);
        data.put("ItemName", itemName);
        data.put("date", date);
        data.put("pNum",phoneNum);
        data.put("productCode",code);

        db.collection("bookingIdList").document(OrderId)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(Tag, "DocumentSnapshot successfully written!_2");
                })
                .addOnFailureListener(e -> Log.w(Tag, "Error writing document", e));

        finish();
    }

    private void getCantBookinglist() {
        String date = dateText.getText().toString();

        FirebaseFirestore db = fbsconnect.db.getInstance();
        db.collection("bookingpaydone").document(code).collection(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                alreadyBookTime.add(String.valueOf(document.get("time")));
                                alreadyBookUser.add(document.getId());
                                Log.d(Tag, "alreadyID=" + document.getId());
                            }
                        } else {
                            Log.d(Tag, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    //    +epEmail+"/"+code+"/"+date
    private String getTime() {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 0);

        Date date = calendar.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(date);

    }



}