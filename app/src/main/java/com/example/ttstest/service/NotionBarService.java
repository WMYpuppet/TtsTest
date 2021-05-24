package com.example.ttstest.service;

import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.ttstest.broadcast.Monitora;
import com.example.ttstest.info.Carrier;
import com.example.ttstest.until.Auxiliary;

/**
 * 作者：Created by Administrator on 2020/11/24.
 * 邮箱：
 */
public class NotionBarService extends NotificationListenerService {

    Monitora monitora;
    String str=null;

    @Override
    public void onCreate() {
        super.onCreate();
        //动态注册广播
        monitora=new Monitora();
        IntentFilter iFilter = new IntentFilter(Carrier.SERVICERADIO);
        registerReceiver(monitora, iFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        try {
            str=sbn.getNotification().tickerText.toString();
            if(str!=null) {
                if (sbn.getPackageName().equals(Carrier.WEIXINPACKAGE)&&str.indexOf(":")!=-1) {
                    Auxiliary.getauxiliary().weixin(sbn, str);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.setAction(Carrier.SERVICERADIO);
                            intent.putExtra("headers", Carrier.HEADERS);           //消息头部的名称
                            intent.putExtra("context", Carrier.MESSAGEBODY);      //消息的内容
                            intent.putExtra("time", Carrier.TIME);                //时间
                            intent.putExtra("bundleid", Carrier.BUNDLEID);       //消息来自哪个应用的包名
                            sendBroadcast(intent);
                        }
                    }).start();
                }else if (sbn.getPackageName().equals(Carrier.QQPACKAGE)&&str.indexOf(":")!=-1) {
                    Auxiliary.getauxiliary().QQswn(sbn, str);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.setAction(Carrier.SERVICERADIO);
                            intent.putExtra("headers", Carrier.HEADERS);           //消息头部的名称
                            intent.putExtra("context", Carrier.MESSAGEBODY);      //消息的内容
                            intent.putExtra("time", Carrier.TIME);              //时间
                            intent.putExtra("bundleid", Carrier.BUNDLEID);         //消息来自哪个应用的包名
                            sendBroadcast(intent);
                        }
                    }).start();
                }else {
                    Log.e("wq============null","其他类型信息");
                }
            }else {
                Log.e("str==============","等待消息中、、、、");
            }
        }catch (NullPointerException e){
            e.fillInStackTrace();
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(monitora);
    }
}

