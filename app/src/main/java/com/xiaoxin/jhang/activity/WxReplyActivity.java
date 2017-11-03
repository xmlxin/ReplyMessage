package com.xiaoxin.jhang.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
                    //Toast.makeText(this,"请勾选",Toast.LENGTH_SHORT).show();
                    showSnackbar(deleteReply);
                }
                break;
            case R.id.bt_add_reply_friend:
                if (mAutoReply.isChecked()) {
                    //Toast.makeText(this,"请取消勾选",Toast.LENGTH_SHORT).show();
                    Snackbar.make(replyFriend,"请取消勾选",Snackbar.LENGTH_SHORT).show();
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

    public void showSnackbar(View view) {
        Snackbar snackbar = Snackbar.make(view, "content", Snackbar.LENGTH_SHORT);
        snackbar.setAction("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: sss");
            }
        });
        snackbar.setActionTextColor(Color.RED);
        TextView tvContent= (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tvContent.setTextColor(Color.GREEN);//设置snackbar内容颜色
        snackbar.getView().setBackgroundColor(Color.BLUE);//设置snackbar背景颜色

        // 设置内容图标Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round);
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        tvContent.setCompoundDrawables(drawable, drawable, drawable, drawable);   // 给TextView左边添加图标tvContent.setGravity(Gravity.CENTER_VERTICAL);  // 让文字居中tvContent.setCompoundDrawablePadding(130); // 设置padding
        snackbar.show();
    }
}
