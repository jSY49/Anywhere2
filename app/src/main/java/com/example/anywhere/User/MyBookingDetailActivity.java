package com.example.anywhere.User;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anywhere.BuildConfig;
import com.example.anywhere.Connect.firebaseConnect;
import com.example.anywhere.CustomProgressDialog;
import com.example.anywhere.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;

public class MyBookingDetailActivity extends AppCompatActivity {
    String Tag = "MyBookingDetailActivity";
    String pCode,productCode,orderId;//pCode=주문번호 productCode=상품 코드 orderId=영수증ID
    private final String bootRestKey= BuildConfig.BOOT_REST_KEY;
    private String bootPrivateKey=BuildConfig.BOOT_PRIVATE_KEY;

    private firebaseConnect fbsconnect;
    TextView Code, CompleteTime, itemName, userName, date, time, pNum, price;
    CustomProgressDialog customProgressDialog;
    boolean cancleCheck=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking_detail);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Code = findViewById(R.id.bookingCode_TextView);
        CompleteTime = findViewById(R.id.bookingComplete_TextView);
        itemName = findViewById(R.id.bookingItemName_TextView);
        userName = findViewById(R.id.bookingUserName_TextView);
        date = findViewById(R.id.bookingDate_TextView);
        time = findViewById(R.id.bookingTime_TextView);
        pNum = findViewById(R.id.bookingPeopleNum_TextView);
        price = findViewById(R.id.bookingTotalPrice_TextView);


        Intent secondIntent = getIntent();
        pCode = secondIntent.getStringExtra("pCode");

        fbsconnect = new firebaseConnect();
        fbsconnect.firbaseInit();
        fbsconnect.firbaseDBInit();
        fbsconnect.firebaseStorageInit();

        dbSetting();


    }

    private void dbSetting() {
        customProgressDialog.show();
        DocumentReference docRef = fbsconnect.db.collection("bookingIdList").document(pCode);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Code.setText(document.getId());
                        CompleteTime.setText((CharSequence) document.get("BookingTime"));
                        itemName.setText((CharSequence) document.get("ItemName"));
                        userName.setText((CharSequence) document.get("name"));
                        date.setText((CharSequence) document.get("date"));
                        time.setText((CharSequence) document.get("time"));
                        pNum.setText((CharSequence) document.get("peopleNumber"));
                        price.setText((CharSequence) document.get("pay"));
                        productCode= (String) document.get("productCode");
                        orderId= (String) document.get("orderId");
                        Log.d(Tag, "orderId: "+ orderId);
                    } else {
                        Log.d(Tag, "No such document");
                    }
                } else {
                    Log.d(Tag, "get failed with ", task.getException());
                }

                customProgressDialog.dismiss();
            }
        });
    }

    public void backBtn(View view) {
        finish();
    }

    public void cancelBtn(View view) {
        //bookingIdList 랑 bookingPaydone db 삭제
        //페이부트 결제 취소 요청

//        cancelPay();
//        if(cancleCheck){
//            deleteBookingPaydonDb();
//            deletebookingIdListDb();
//            finish();
//        }
//        else{
//            Log.d(Tag, "다시 취소를 시도해 주세요.");
//        }
        deleteBookingPaydonDb();
        deletebookingIdListDb();
        finish();
    }

    //취소가 안되염~
    private void cancelPay() {

        try {
            Bootpay bootpay = new Bootpay(bootRestKey, bootPrivateKey);
            HashMap token = bootpay.getAccessToken();
            if(token.get("error_code") != null) { //failed
                return;
            }
            Log.d(Tag, "orderId: "+ orderId);
            Cancel cancel = new Cancel();
            cancel.receiptId = orderId;
            cancel.cancelUsername = "사용자(고객)";
            cancel.cancelMessage = "테스트 결제";

            HashMap res = bootpay.receiptCancel(cancel);
            if(res.get("error_code") == null) { //success
                Log.d(Tag, "receiptCancel success: "+ res);
                cancleCheck=true;
            } else {
                Log.d(Tag, "receiptCancel false: "+ res);
                cancleCheck=false;
            }
        }catch (NoSuchFieldError | InvocationTargetException Ne){
            cancleCheck=false;
            Log.d(Tag, String.valueOf(Ne));
        } catch (Exception e) {
            cancleCheck=false;
            Log.d(Tag, String.valueOf(e));
        }
    }

    private void deletebookingIdListDb() {
        fbsconnect.db.collection("bookingIdList").document(pCode)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(Tag, "bookingIdList successfully deleted!");
                        Toast.makeText(getApplicationContext(),"예약이 취소 되었습니다.",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(Tag, "Error deleting document", e);
                    }
                });
    }

    private void deleteBookingPaydonDb() {
        String DATE = date.getText().toString();
        String UderId = fbsconnect.fb_userEmail();

        Log.d(Tag, "productCode="+productCode);
        Log.d(Tag, "DATE="+DATE);
        Log.d(Tag, "UderId="+UderId);


        fbsconnect.db.collection("bookingpaydone").document(productCode).collection(DATE).document(UderId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(Tag, "bookingpaydone successfully deleted!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(Tag, "Error deleting document", e);
                    }
                });
    }
}