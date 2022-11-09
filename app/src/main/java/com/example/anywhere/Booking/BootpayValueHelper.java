package com.example.anywhere.Booking;

public class BootpayValueHelper {
    public static String pgToString(String key) {
        switch (key) {
            case "KCP":
                return "kcp";
            case "다날":
                return "danal";
            case "LGU+":
                return "lgup";
            case "이니시스":
                return "inicis";
            case "유디페이":
                return "udpay";
            case "나이스페이":
                return "nicepay";
            case "네이버페이":
                return "npay";
            case "페이앱":
                return "payapp";
            case "카카오페이":
                return "kakao";
            case "TPAY":
                return "tpay";
            case "페이레터":
                return "payletter";
            case "KICC":
                return "easypay";
            case "웰컴페이먼츠":
                return "welcome";
        }
        return "";
    }

    public static String methodToString(String key) {
        switch (key) {
            case "카드결제":
                return "card";
            case "휴대폰소액결제":
                return "phone";
            case "가상계좌":
                return "vbank";
            case "계좌이체":
                return "bank";
            case "카드정기결제":
                return "card_rebill";
            case "간편결제":
                return "";
            case "주문결제":
                return "";
            case "부트페이 간편결제":
                return "";
            case "본인인증":
                return "auth";
            case "카카오페이":
                return "kakao";
            case "네이버페이":
                return "npay";
            case "페이코":
                return "payco";
        }
        return "";
    }
}
