package com.example.ttstest.until;

import android.service.notification.StatusBarNotification;

import com.example.ttstest.info.Carrier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：Created by Administrator on 2020/11/24.
 * 邮箱：
 */
public class Auxiliary {

    private static Auxiliary auxiliary;

    public static Auxiliary getauxiliary() {
        if (auxiliary == null) {
            auxiliary = new Auxiliary();
        }
        return auxiliary;
    }

    public void weixin(StatusBarNotification sbn, String name) {
        if (sbn.getPackageName().equals(Carrier.WEIXINPACKAGE)) {
            //拿到取得消息的时间段
            DateFormat df = new SimpleDateFormat("HH:mm");
            Carrier.TIME = df.format(new Date());
            //拿到消息的字符串的长度进行截取标题与内容
            int k = name.length();
            for (int i = 0; i < name.length(); i++) {
                if (name.substring(i, i + 1).equals(":")) {
                    Carrier.HEADERS = name.substring(0, i).trim();
                    Carrier.MESSAGEBODY = name.substring(i + 1, k).trim();
                }
            }
            Carrier.BUNDLEID = sbn.getPackageName();
        }
    }

    public void QQswn(StatusBarNotification sbn, String name) {
        if (sbn.getPackageName().equals(Carrier.QQPACKAGE)) {
            //拿到取得消息的时间段
            DateFormat df = new SimpleDateFormat("HH:mm");
            Carrier.TIME = df.format(new Date());
            //拿到消息的字符串的长度进行截取标题与内容
            int k = name.length();
            for (int i = 0; i < name.length(); i++) {
                if (name.substring(i, i + 1).equals(":")) {
                    Carrier.HEADERS = name.substring(0, i).trim();
                    Carrier.MESSAGEBODY = name.substring(i + 1, k).trim();
                }
            }
            Carrier.BUNDLEID = sbn.getPackageName();
        }
    }

}
