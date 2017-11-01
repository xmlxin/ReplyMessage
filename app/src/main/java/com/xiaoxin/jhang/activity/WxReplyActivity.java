package com.xiaoxin.jhang.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaoxin.jhang.R;
import com.xiaoxin.jhang.util.Contant;
import com.xiaoxin.jhang.util.SharedPreferencesUtils;

public class WxReplyActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "WxReplyActivity";
    private CheckBox mAutoReply;
    private EditText deleteReply,replyFriend;
    private Button btDeleteReply,btReplyFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_reply);

        initView();
//        mAutoReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.e(TAG, "onCheckedChanged: "+isChecked );
//                if (isChecked) { //选中
//                    deleteReply.setFocusableInTouchMode(true);
//                    deleteReply.setFocusable(true);
//                    deleteReply.requestFocus();
//
//                    replyFriend.setFocusable(false);
//                    replyFriend.setFocusableInTouchMode(false);
//                }else { //未选中
//                    replyFriend.setFocusableInTouchMode(true);
//                    replyFriend.setFocusable(true);
//                    replyFriend.requestFocus();
//
//                    deleteReply.setFocusable(false);
//                    deleteReply.setFocusableInTouchMode(false);
//
//                }
//            }
//        });

    }

    private void initView() {

        mAutoReply = (CheckBox) findViewById(R.id.cb_aotu_reply);
        deleteReply = (EditText) findViewById(R.id.et_reply_delete);
        replyFriend = (EditText) findViewById(R.id.et_reply_friend);
        btDeleteReply = (Button) findViewById(R.id.bt_reply_delete);
        btReplyFriend = (Button) findViewById(R.id.bt_add_reply_friend);

        btDeleteReply.setOnClickListener(this);
        btReplyFriend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_reply_delete:
                if (mAutoReply.isChecked()) {
                    checkIsNull(deleteReply.getText().toString().trim());
                    Contant.isAutoReply = true;
                    String delteReplyValues = SharedPreferencesUtils.init(this, Contant.SP_REPLY).getString("deleteReply","");
                    SharedPreferencesUtils.init(this, Contant.SP_REPLY).putString("deleteReply",delteReplyValues + deleteReply.getText().toString().trim());
                }else {
                    Toast.makeText(this,"请勾选",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_add_reply_friend:
                if (mAutoReply.isChecked()) {
                    Toast.makeText(this,"请取消勾选",Toast.LENGTH_SHORT).show();
                }else {
                    Contant.isAutoReply = false;
                    String replyFriendValues = SharedPreferencesUtils.init(this, Contant.SP_REPLY).getString("reply_friend","");
                    SharedPreferencesUtils.init(this, Contant.SP_REPLY).putString("reply_friend",replyFriendValues + replyFriend.getText().toString().trim());
                    Toast.makeText(this,"添加成功，还可以添加其他好友",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkIsNull(String edittext) {
        if (TextUtils.isEmpty(edittext)) {
            Toast.makeText(this,"空数据",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
