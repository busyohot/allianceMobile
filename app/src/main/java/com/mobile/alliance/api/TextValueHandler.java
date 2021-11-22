package com.mobile.alliance.api;

public class TextValueHandler {

    //공통
    //알림톡 발송 주소
    public final static String TALK_URL = "https://alimtalk-api.bizmsg.kr/v2/sender/";
    //카카오맵 주소
    public final static String KAKAO_MAP_URL = "https://dapi.kakao.com/v2/local/";






    public static String BASE_URL = null;
    //카페24_이미지업로드_ftp
    public static String FTP_HOST = null;
    //ftp 포트
    public static Integer FTP_PORT = null;
    //카페24
    public static String FTP_USER = null;
    public static String FTP_PASS = null;
    //ftp 기본 디렉토리
    public static String FTP_DIR = null;
    //http_이미지보는_주소
    public static String HTTP_HOST = null;
    //App 다운받는 주소
    public static String API_URL = null;
    //App 다운받는 경로
    public static String API_URL_PATH = null;


    public void type(String type){
        if(type.equals("alliance"))
        {
            //BASE_URL             = "http://gloakoreahost.cafe24.com/mobile/";      //WAS
            BASE_URL             = "http://www.alliance1.co.kr/mobile/";      //WAS
            FTP_HOST            =   "gloakoreaimage.cafe24.com";
            FTP_PORT            = 21;
            FTP_USER             = "gloakoreaimage";
            FTP_PASS             ="host!@image78";
            FTP_DIR              = "www/alliance/";
            HTTP_HOST            = "http://gloakoreaimage.cafe24.com/alliance/";
            API_URL              = "http://www.alliance1.co.kr/alliance/alliance.apk";
            API_URL_PATH         = "http://www.alliance1.co.kr/alliance/";
        }
        else if (type.equals("local"))
        {
            //was
            BASE_URL             = "http://145.122.24.26:8080/mobile/";

            //BASE_URL = "http://145.122.24.222:8080/mobile/";

            //BASE_URL = "http://busyo.iptime.org:8080/mobile/";

            FTP_HOST             = "145.122.24.222";
            FTP_PORT             = 21;
            FTP_USER             = "busyohot";
            FTP_PASS             ="Hot3136!@!@";
            FTP_DIR              = "web/alliance/";
            HTTP_HOST            = "http://145.122.24.222/alliance/";
            API_URL              = "http://145.122.24.222/alliance/alliance.apk";
            API_URL_PATH         = "http://145.122.24.222/alliance/";
        }
    }
}
