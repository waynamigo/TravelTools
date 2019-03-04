package com.example.traveltools.constant;

/**
 * Created by waynamigo on 18-8-18.
 */

public class APPConfig {
    public final static String BASE_URL_PATH = "http://jd.han777.win:8086";
    public final static String SENDSMS =BASE_URL_PATH.concat("/sign/sendSMS/");
    public final static String SIGN_UP = BASE_URL_PATH.concat("/sign/up/");
    public final static String SIGN_IN = BASE_URL_PATH.concat("/sign/in/");
    public final static String SIGN_RENAME = BASE_URL_PATH.concat("/sign/rename/");
    public final static String FUCUS_VLIST = BASE_URL_PATH.concat("/focus/Vlist/");
    public final static String FOCUS_ADD = BASE_URL_PATH.concat("/focus/add/");
    public final static String FOCUS_DEL = BASE_URL_PATH.concat("/focus/del/");

    public final static String WEIBO = BASE_URL_PATH.concat("/weibo");
    public final static String WEIBO_SET_CONTENT = WEIBO.concat("/content/");
    public final static String WEIBO_CREATE = WEIBO.concat("/create/");
    public final static String DEL_WEIBO_PICTURE = WEIBO.concat("/picture/");
    public final static String ADD_WEIBO_PICTURE = WEIBO.concat("/picture/");
    public final static String GET_WEIBO_PICPATH = WEIBO.concat("/pictures/");
    public final static String GET_WEIBO_CONTENT = WEIBO.concat("/weibo/");
    public final static String FILE = BASE_URL_PATH.concat("/file");
    public final static String FILE_UPLOAD = FILE.concat("/upload/");
    public final static String FILE_DOWNLOAD = FILE.concat("/download/{?}");
    public final static String WEIBO_ALL = WEIBO.concat("/all/");
    public final static String AMAP_KEY = "9ea7d90ca02575f99fadd4844de8c1f0";
    public final static String RESET_PSW = "/sign/resetpsw/";
    public final static String COMMENT =BASE_URL_PATH.concat("/comment");
    public final static String ADD_COMMENT=COMMENT.concat("/add/");
    public final static String GETALL_COMMENT=COMMENT.concat("/getall/");

}
