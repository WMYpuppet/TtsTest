package com.example.ttstest.info;

/**
 * 作者：Created by Administrator on 2020/11/24.
 * 邮箱：
 */
public class Carrier {
    public static String HEADERS = "";         //消息头部
    public static String MESSAGEBODY = "";      //消息体
    public static String BUNDLEID = "";         //包名
    public static String TIME = "";             //时间
    public static final String SERVICERADIO = "BROADCAST";//广播的名称
    public static int FIRSTSUBSORIPT = 0;
    public static int SUBSORIPT = 0;//用来记录读取数据库的下标
    public static final String QQPACKAGE = "com.tencent.mobileqq";//qq包名
    public static final String WEIXINPACKAGE = "com.tencent.mm";//微信包名
    public static final int TAKENOTESWORD = 1;//用于区别Message
}