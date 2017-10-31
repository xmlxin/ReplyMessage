package com.xiaoxin.jhang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaoxin.jhang.R;
import com.xiaoxin.jhang.service.AutoReplyService;
import com.xiaoxin.jhang.util.Contant;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.xiaoxin.jhang.util.PerformClickUtils.CONTENT;
import static com.xiaoxin.jhang.util.PerformClickUtils.NAME;
import static com.xiaoxin.jhang.util.PerformClickUtils.NUMBER;

public class SendMsgqq extends AppCompatActivity {


    private static final String TAG = "AutoSendMsgService";
    public static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";
    public static final String MM = "com.tencent.mobileqq";

    private String name,content;
    private Button start;
    private EditText sendName,sendContent,etSendNumber;
    private AccessibilityManager accessibilityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msgqq);

        initView();

        /**
         * 发送消息
         */
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndStartService();
            }
        });
    }


    private void initView() {
        start = (Button) findViewById(R.id.testsendqq);
        sendName = (EditText) findViewById(R.id.sendNameqq);
        sendContent = (EditText) findViewById(R.id.sendContentqq);
        etSendNumber = (EditText) findViewById(R.id.et_sendNumberqq);

    }

    private void checkAndStartService() {
        accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
            @Override
            public void onAccessibilityStateChanged(boolean b) {
                Log.d(TAG,"onAccessibilityStateChanged b = "+b);
                if(b){
                    goWecaht();
                }else{
                    openService();
                }
            }
        });

        name = sendName.getText().toString();
        content = sendContent.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"联系人不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(content)) {
            Toast.makeText(this,"内容不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!accessibilityManager.isEnabled()){
            openService();
        }else{
            goWecaht();
        }
    }

    /**
     * 打开辅助功能
     */
    private void openService() {
        try {
            //打开系统设置中辅助功能
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "找到微信自动发送消息，然后开启服务即可", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 打开微信
     */
    private void goWecaht() {
        setValue(name,content);
        AutoReplyService.hasSend = false;
        AutoReplyService.sendMsg = true;
        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(MM, Contant.mobileqq);
        startActivity(intent);
    }

    /**
     * 设置name content
     * @param name
     * @param content
     */
    public void setValue(String name,String content) {
        NAME = name;
        CONTENT = content;
        NUMBER = Integer.parseInt(etSendNumber.getText().toString());
        Log.e("ss", "setValue: NUMBER"+NUMBER);
        Log.e("sss", "setValue: NUMBER"+etSendNumber.getText().toString());
        AutoReplyService.hasSend = false;
        Contant.isMOniSendMsg = true;
    }
}
