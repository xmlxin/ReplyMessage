package com.xiaoxin.jhang.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xiaoxin.jhang.R;
import com.xiaoxin.jhang.service.AutoReplyService;
import com.xiaoxin.jhang.util.TrackerWindowManager;

/**
 * Created by jinliangshan on 16/12/26.
 */
public class FloatingView extends LinearLayout {
    public static final String TAG = "FloatingView";

    private final Context mContext;
    private final WindowManager mWindowManager;
    private EditText et_conent,et_number;
    private Button mIvClose;
    private String mNumber;
    private String mContent;

    public FloatingView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.layout_float_view, this);
        et_conent = (EditText) findViewById(R.id.et_content);
        et_number = (EditText) findViewById(R.id.et_number);
        mIvClose = (Button) findViewById(R.id.iv_close);

        mIvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackerWindowManager trackerWindowManager = new TrackerWindowManager(mContext);
                trackerWindowManager.removeView();
                mNumber = et_number.getText().toString().trim();
                mContent = et_conent.getText().toString().trim();
                Log.e(TAG, "onClick: "+ mNumber);
                if (!"".equals(mContent) && !"".equals(mNumber)) {
                    mContext.startService(new Intent(mContext, AutoReplyService.class)
                            .putExtra(AutoReplyService.COMMAND, AutoReplyService.COMMAND_CLOSE)
                            .putExtra("sendContent", mContent)
                            .putExtra("sendNumber",Integer.parseInt(mNumber))
                    );
                }else {
                    Toast.makeText(mContext,"请填写完整",Toast.LENGTH_SHORT).show();
                }

                //AutoReplyService.isSend = false;
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    Point preP, curP;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                preP = new Point((int)event.getRawX(), (int)event.getRawY());
                break;

            case MotionEvent.ACTION_MOVE:
                curP = new Point((int)event.getRawX(), (int)event.getRawY());
                int dx = curP.x - preP.x,
                        dy = curP.y - preP.y;

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.getLayoutParams();
                layoutParams.x += dx;
                layoutParams.y += dy;
                mWindowManager.updateViewLayout(this, layoutParams);

                preP = curP;
                break;
        }

        return false;
    }

}
