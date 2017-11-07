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
import android.widget.Spinner;
import android.widget.Toast;

import com.xiaoxin.jhang.R;
import com.xiaoxin.jhang.service.AutoReplyService;
import com.xiaoxin.jhang.util.Contant;
import com.xiaoxin.jhang.util.SharedPreferencesUtils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.xiaoxin.jhang.util.PerformClickUtils.CONTENT;
import static com.xiaoxin.jhang.util.PerformClickUtils.NAME;
import static com.xiaoxin.jhang.util.PerformClickUtils.NUMBER;

public class SendMsgWx extends AppCompatActivity {

    private static final String TAG = "AutoSendMsgService";
    public static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";
    public static final String MM = "com.tencent.mm";

    private String name,content;
    private Button start;
    private EditText sendName,sendContent,etSendNumber;
    private AccessibilityManager accessibilityManager;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg_wx);

        initView();
        initSp();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinner.getSelectedItemPosition();
                checkAndStartService();
            }
        });
    }

    private void initSp() {
        sendName.setText(SharedPreferencesUtils.init(this, Contant.CONTACT).getString("friend"));
        sendContent.setText(SharedPreferencesUtils.init(this, Contant.CONTACT).getString("content"));
    }

    private void initView() {
        start = (Button) findViewById(R.id.testWechat);
        sendName = (EditText) findViewById(R.id.sendName);
        sendContent = (EditText) findViewById(R.id.sendContent);
        etSendNumber = (EditText) findViewById(R.id.et_sendNumber);
        mSpinner = (Spinner) findViewById(R.id.sp_select);

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
        SharedPreferencesUtils.init(this, Contant.CONTACT).putString("friend",name);
        SharedPreferencesUtils.init(this, Contant.CONTACT).putString("content",content);

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
        intent.setClassName(MM, LauncherUI);
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
        NUMBER = Integer.parseInt(etSendNumber.getText().toString().trim());
        Contant.selectPages = mSpinner.getSelectedItemPosition() == 0 ? true : false;
        AutoReplyService.hasSend = false;
        Contant.isMOniSendMsg = true;
    }


}
