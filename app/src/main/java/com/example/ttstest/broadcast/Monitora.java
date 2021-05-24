package com.example.ttstest.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.ttstest.info.Person;
import com.example.ttstest.service.PersonService;

/**
 * 作者：Created by Administrator on 2020/11/24.
 * 邮箱：
 */
public class Monitora  extends BroadcastReceiver {
    private PersonService personService;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Person person = new Person(bundle.getString("headers"), bundle.getString("context"), bundle.getString("time"), bundle.getString("bundleid"));
        personService = new PersonService(context);
        personService.save(person);
    }
}

