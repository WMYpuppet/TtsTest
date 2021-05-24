package com.example.ttstest;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.ttstest.db.DBHelper;
import com.example.ttstest.info.Carrier;
import com.example.ttstest.info.Person;
import com.example.ttstest.service.NotionBarService;
import com.example.ttstest.service.PersonService;
import com.example.ttstest.until.Vocality;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SpeechSynthesizer mTts;
    private PersonService personService;
    // 默认发音人
    private String playman = "xiaoyan";
    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue;
    private SharedPreferences spSubsoript, spPlayman;
    String mess = "";
    private Toast mToast;
    private int selectedNum = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Carrier.TAKENOTESWORD:
                    mess = msg.obj.toString();
                    Vocality.getvocality().combining(mess, playman, mTts, mSynListener, getApplicationContext());//合成语音
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
    }


    private void initLayout() {
        //沉浸状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (!isNotificationListenerEnabled(this)) {
            openNotificationListenSettings();
        }
        toggleNotificationListenerService();

        findViewById(R.id.tts_btn_person_select).setOnClickListener(this);
        findViewById(R.id.btn_notification).setOnClickListener(this);
        findViewById(R.id.tts_start).setOnClickListener(this);
        findViewById(R.id.tts_stop).setOnClickListener(this);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        spSubsoript = getSharedPreferences("Subsoript", Context.MODE_PRIVATE);
        spPlayman = getSharedPreferences("ttsPlayman", Context.MODE_PRIVATE);
        //记录程序退出时读取的最后一条信息
        if (spSubsoript.getInt("current ", 0) > 0) {
            Carrier.SUBSORIPT = spSubsoript.getInt("current ", 0);//app初始化的时候拿到存储下来的坐标，如果没有存储就取0下标
            Carrier.FIRSTSUBSORIPT = spSubsoript.getInt("firstSubsoript", 0);
        }
        //记录发音人选择项
        selectedNum = spPlayman.getInt("pos", 0);
        playman = spPlayman.getString("playman", "");

        Intent intent = new Intent(this, NotionBarService.class); //启动服务
        startService(intent);

        DBHelper dbOpenHelper = new DBHelper(this); //初始化数据库
        dbOpenHelper.getWritableDatabase();
        personService = new PersonService(this); //初始化数据库操作类

        mTts = SpeechSynthesizer.createSynthesizer(this, null);//得到SpeechSynthesizer操作类进行设置
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries); // 云端发音人名称列表
        mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);

        //数据库+Handle+线程 让此方法一直处于运行状态
        chushihua();
    }

    //获取数据库消息，发送出去
    public void chushihua() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (personService != null && !personService.equals("") && Carrier.FIRSTSUBSORIPT < personService.selectPerson().size()) {
                    List<Person> persons = personService.selectPerson();
                    if (Carrier.FIRSTSUBSORIPT == Carrier.SUBSORIPT) {
                        Message message = new Message();
                        message.what = Carrier.TAKENOTESWORD;
                        if (persons.get(Carrier.SUBSORIPT).getBundleid().equals("com.tencent.mm")) {
                            String matter = persons.get(Carrier.SUBSORIPT).getMatter();   //消息内容
                            String time = persons.get(Carrier.SUBSORIPT).getTime();       //消息的时间
                            String tit = persons.get(Carrier.SUBSORIPT).getTitie();       //发消息的人
                            message.obj = "来自微信的" + tit + "说:" + matter + "   " + time;
                            handler.handleMessage(message);//将消息发送到handler里面进行下一步操作
                        } else if (persons.get(Carrier.SUBSORIPT).getBundleid().equals("com.tencent.mobileqq")) {
                            String matter = persons.get(Carrier.SUBSORIPT).getMatter();   //消息内容
                            String time = persons.get(Carrier.SUBSORIPT).getTime();       //消息的时间
                            String tit = persons.get(Carrier.SUBSORIPT).getTitie();       //发消息的人
                            message.obj = "来自QQ的" + tit + "说:" + matter + "   " + time;
                            handler.handleMessage(message);//将消息发送到handler里面进行下一步操作
                        }

                        Carrier.FIRSTSUBSORIPT++;
                        Carrier.SUBSORIPT++;
                        SharedPreferences.Editor editor = spSubsoript.edit();
                        editor.putInt("current ", Carrier.SUBSORIPT);
                        editor.putInt("firstSubsoript", Carrier.FIRSTSUBSORIPT);
                        editor.commit();
                    }
                } else {
                    intiso();
                }
            }
        }).start();
    }

    private void intiso() {
        chushihua();
    }


    @Override
    public void onClick(View v) {
        if (null == mTts) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }
        switch (v.getId()) {
            case R.id.btn_notification:
//                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
//                startActivity(intent);
                if (!isNotificationListenerEnabled(this)) {
                    openNotificationListenSettings();
                }
                toggleNotificationListenerService();

                break;
            case R.id.tts_btn_person_select:
                showPresonSelectDialog();
                break;
            case R.id.tts_start:
                mTts.resumeSpeaking();
                break;
            case R.id.tts_stop:
                mTts.pauseSpeaking();
                break;
        }

    }


    //个性语音
    private void showPresonSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("在线语音发音人选项");
        builder.setSingleChoiceItems(mCloudVoicersEntries, selectedNum,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playman = mCloudVoicersValue[which];
                        //个性语音没有英文
//                                if ("catherine".equals(playman) || "henry".equals(playman) || "vimary".equals(playman)) {
//
//                                } else {
//
//                                }
                        selectedNum = which;
                        SharedPreferences.Editor editor = spPlayman.edit();
                        editor.putString("playman", playman);
                        editor.putInt("pos", which);
                        editor.commit();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }


    //科大讯飞自带方法
    SynthesizerListener mSynListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");


        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError error) {
            chushihua();
            if (error == null) {
                showTip("播放完成");


            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    //检测通知监听服务是否被授权
    public boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }

    //打开通知监听设置页面
    public void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //把应用的NotificationListenerService实现类disable再enable，即可触发系统rebind操作
    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(this, NotionBarService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(this, NotionBarService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    //吐司
    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}
