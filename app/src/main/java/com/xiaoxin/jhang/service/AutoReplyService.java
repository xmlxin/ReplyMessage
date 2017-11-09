package com.xiaoxin.jhang.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.xiaoxin.jhang.util.Contant;
import com.xiaoxin.jhang.util.PerformClickUtils;
import com.xiaoxin.jhang.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoReplyService extends AccessibilityService {

    private static final String TAG = "AutoReplyService";

    public static final String COMMAND = "COMMAND";
    public static final String COMMAND_OPEN = "COMMAND_OPEN";
    public static final String COMMAND_CLOSE = "COMMAND_CLOSE";
    public String packageName,className;

    boolean hasAction = false;
    boolean locked = false;
    boolean background = false;
    private String name;
    private String scontent;
    AccessibilityNodeInfo itemNodeinfo;
    private KeyguardManager.KeyguardLock kl;
    private Handler handler = new Handler();

    public static boolean hasSend;
    public static boolean sendMsg;

    // 模拟回复
    private List<String> allNameList = new ArrayList<>();
    private int mRepeatCount;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String command = intent.getStringExtra(COMMAND);
        String sendContent = intent.getStringExtra("sendContent");
        int sendNumber = intent.getIntExtra("sendNumber",0);
        Log.e("xiaoxin", "sendContent: "+sendContent +"  number" + sendNumber );
        if (packageName != null) {
            Log.e(TAG, "packageName: "+packageName );
        } else {
            Log.e(TAG, "packageName: null" );
        }
        if(command != null && !"".equals(command)) {
            switch (packageName) {
                case Contant.MM_PNAME:
                    sendMsg(Contant.chatUI_EditText_id,sendContent,sendNumber);
                    break;
                case Contant.MOBILEQQ_PNAME:
                    sendMsg(Contant.mobileqq_et,sendContent,sendNumber);
                    break;
                case Contant.SINA_PNAME:
                    if (className.equals(Contant.SINA_CLASSNAME)) {
                        Log.e(TAG, "onStartCommand: classname" );
                        sendSinaMsg(Contant.sina_reply_et,sendContent,sendNumber);
                    }else {
                        Log.e(TAG, "onStartCommand: "+Contant.SINA_PNAME);
                        sendMsg(Contant.sina_et,sendContent,sendNumber);
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendSinaMsg(String packageNameEdit, String sendContent,int sendNumber) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("xiaoxin", "remove view: ");
        for (int i = 0; i < sendNumber; i++) {
            if (fill(packageNameEdit,sendContent)) {
                send();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PerformClickUtils.findTextAndClick(this,"评论");
                Log.e("xiaoxin", "send success: " );
            }else {
                Log.e("xiaoxin", "fill: 复制失败");
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMsg(String packageNameEdit, String sendContent,int sendNumber) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("xiaoxin", "remove view: ");
        for (int i = 0; i < sendNumber; i++) {
            if (fill(packageNameEdit,sendContent)) {
                send();

                PerformClickUtils.findTextAndClick(this,"评论");
                Log.e("xiaoxin", "send success: " );
            }else {
                Log.e("xiaoxin", "fill: 复制失败");
            }
        }
    }

    /**
     * 必须重写的方法，响应各种事件。
     * @param event
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        packageName = event.getPackageName().toString();
        className = event.getClassName().toString();
        Log.e("xiaoxin", "packageName: "+packageName );
        Log.e("xiaoxin", "className: "+event.getClassName().toString() );
        Log.d("maptrix", "get event = " + eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件,自动回复
                if (!packageName.equals(Contant.MM_PNAME)) { //不是微信返回
                    return;
                }
                List<CharSequence> texts = event.getText();
                for (int i = 0; i < texts.size(); i++) {
                    Log.e("maptrix", "onAccessibilityEvent: "+texts.get(i) );
                }
                String name = (texts.toString().substring(0,texts.toString().indexOf(":"))).substring(1);
                Log.e("maptrix", "onAccessibilityEvent: name"+name+Contant.isAutoReply);
                if (Contant.isAutoReply) { //全部自动回复
                    String deleteReplyValues = SharedPreferencesUtils.init(this, Contant.SP_REPLY).getString("deleteReply","");
                    if ((deleteReplyValues.indexOf(name)) != -1) {
                        return;
                    }
                }else {//指定回复
                    String replyFriendValues = SharedPreferencesUtils.init(this, Contant.SP_REPLY).getString("reply_friend","");
                    Log.e(TAG, "replyFriendValues: "+replyFriendValues );
                    if ((replyFriendValues.indexOf(name)) == -1) {
                        Log.e("maptrix", "不包含 不是这个人 不自动回复 " );
                        return;
                    }
                }
//                if (texts.toString().indexOf("臭妮子") == -1) {
//                    Log.e("maptrix", "不包含 不是这个人 不自动回复 " );
//                    return;
//                }
                Log.e("maptrix", "自动回复 " );
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        if (!TextUtils.isEmpty(content)) {
                            if (isScreenLocked()) {
                                locked = true;
                                wakeAndUnlock();
                                if (isAppForeground(Contant.MM_PNAME)) {
                                    background = false;
                                    sendNotifacationReply(event);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendNotifacationReply(event);
                                            if (fill()) {
                                                send();
                                            }
                                        }
                                    }, 1000);
                                } else {
                                    background = true;
                                    sendNotifacationReply(event);
                                }
                            } else {
                                locked = false;
                                if (isAppForeground(Contant.MM_PNAME)) {
                                    background = false;
                                    sendNotifacationReply(event);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (fill()) {
                                                send();
                                            }
                                        }
                                    }, 1000);
                                } else {
                                    background = true;
                                    sendNotifacationReply(event);
                                }
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                    //sendMsg = true;

                if (Contant.isMOniSendMsg) {
                    moniSendMsg(event);
                }else {
                    reply(event);
                }

                break;
        }
    }

    public void reply(AccessibilityEvent event) {
            Log.d("maptrix", "get type window down event");
            if (!hasAction) return;
            itemNodeinfo = null;
            String className = event.getClassName().toString();
            if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                if (fill()) {
                    send();
                }else {
                    if(itemNodeinfo != null){
                        itemNodeinfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (fill()) {
                                    send();
                                }
                                back2Home();
                                release();
                                hasAction = false;
                            }
                        }, 1000);
                        return;
                    }
                }
            }
            //bring2Front();
            back2Home();
            release();
            hasAction = false;
    }

    public void moniSendMsg(AccessibilityEvent event) {
        if(hasSend){
            return;
        }
        String currentActivity = event.getClassName().toString();
        /**
         * 第一次打开微信的时候有问题，不会执行任何操作
         * log显示的是getRootInActiveWindow()没有返回任何东西，暂时无法解决
         * 如果微信已经打开，再次操作就没有问题
         */
        if(currentActivity.equals("com.tencent.mm.ui.LauncherUI")) {
            if (Contant.selectPages) {
                PerformClickUtils.findTextAndClick(this,"微信");
                AccessibilityNodeInfo itemInfo = TraversalAndFindContacts();
                if (itemInfo != null) {
                    PerformClickUtils.performClick(itemInfo);
                    for (int i = 0; i < PerformClickUtils.NUMBER; i++) {
                        if(fillSend(i)){
                            send();
                            Log.e(TAG, "moniSendMsg: 发送消息" );
                        }
                    }
                }else {
                    hasSend=true;
                }
            }else {
                PerformClickUtils.findTextAndClick(this,"通讯录");
                //getWxName();
                AccessibilityNodeInfo itemInfo = TraversalAndFindContacts();
                if (itemInfo != null) {
                    PerformClickUtils.performClick(itemInfo);
                }else {
                    hasSend=true;
                }
            }
        }else if(currentActivity.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")){
            PerformClickUtils.findTextAndClick(this,"发消息");
        }else if(currentActivity.equals("com.tencent.mm.ui.chatting.En_5b8fbb1e")){
            Log.e("sss", "moniSendMsg:number " +PerformClickUtils.NUMBER);
            for (int i = 0; i < PerformClickUtils.NUMBER; i++) {
                if(fillSend(i)){
                    send();
                    Log.e(TAG, "moniSendMsg: 发送消息" );
                }
            }
            Contant.isMOniSendMsg = false;
        }
    }

    /**
     * 向下滚动遍历寻找联系人
     * 如果滚动到底没有找到，向上再滚动一遍（防止当前通讯录已经位于中间位置）
     * @return
     */
    private AccessibilityNodeInfo TraversalAndFindContacts() {
        allNameList.clear();
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        List<AccessibilityNodeInfo> listview = rootNode.findAccessibilityNodeInfosByViewId(Contant.contactUI_listview_id);
        Log.d(TAG, "listview " + listview.isEmpty());
        boolean toEnd = false;
        //第一次执行后退操作需要清空名字列表
        boolean firstExcScrollBackward = true;
        if(!listview.isEmpty()){
            while (true) {
                List<AccessibilityNodeInfo> nameList = rootNode.findAccessibilityNodeInfosByViewId(Contant.contactUI_name_id);
                List<AccessibilityNodeInfo> itemList = rootNode.findAccessibilityNodeInfosByViewId(Contant.contactUI_item_id);
                Log.d(TAG, "nameList " + nameList.isEmpty());

                if (nameList != null && !nameList.isEmpty()) {
                    for (int i = 0; i < nameList.size(); i++) {
                        AccessibilityNodeInfo nodeInfo = nameList.get(i);
                        String nickname = nodeInfo.getText().toString();
                        Log.e(TAG, "TraversalAndFindContacts: wx name"+nickname );
                    }
                }

                if (nameList != null && !nameList.isEmpty()) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if(i==0) {
                            //必须在一个循环内
                            mRepeatCount = 0;
                        }
                        AccessibilityNodeInfo itemInfo = itemList.get(i);
                        AccessibilityNodeInfo nodeInfo = nameList.get(i);
                        String nickname = nodeInfo.getText().toString();
                        Log.d(TAG, "nickname " + nickname);
                        //Log.d(TAG, "name " + PerformClickUtils.NAME);
                        if (nickname.equals(PerformClickUtils.NAME)) {
                            return itemInfo;
                        }
                        if (!allNameList.contains(nickname)) {
                            allNameList.add(nickname);
                        }else if(allNameList.contains(nickname)){
                            Log.d(TAG,"contains(nickname) = "+nickname);
                            Log.d(TAG,"mRepeatCount = "+mRepeatCount);
                                /*
                                * 表示已经到底了,通过判断当前页是否已经包含了三个联系人
                                * 如果有三个，表示已经到底部了，否则会一直循环
                                * */
                            if(mRepeatCount==3){
                                //表示已经滑动到顶部了
                                if(toEnd){
                                    Log.d(TAG,"没有找到联系人");
                                    //此次发消息操作已经完成
                                    hasSend = true;
                                    return null;
                                }
                                toEnd = true;
                            }
                            mRepeatCount++;
                        }
                    }
                }

                if(!toEnd) {
                    //向下滚动
                    Log.d(TAG, "向下滚动");
                    listview.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                }
                else {
                    Log.d(TAG, "向上滚动");
                    if(firstExcScrollBackward) {
                        allNameList.clear();
                        firstExcScrollBackward = false;
                    }
                    //到底了还没找到向上滚动一遍
                    listview.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                }

                //必须等待一秒钟，因为需要等待滚动操作完成
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 寻找窗体中的“发送”按钮，并且点击。
     */
    @SuppressLint("NewApi")
    private void send() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByText("发送");
            if (list != null && list.size() > 0) {
                for (AccessibilityNodeInfo n : list) {
                    if(n.getClassName().equals("android.widget.Button") || n.getClassName().equals("android.widget.TextView") && n.isEnabled()){
                        n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        hasSend = true;  //把这里改成false 就会一直发送 可做成炸群android.widget.Buttonandroid.widget.TextView
                    }
                }

            } else {
                List<AccessibilityNodeInfo> liste = nodeInfo
                        .findAccessibilityNodeInfosByText("Send");
                if (liste != null && liste.size() > 0) {
                    for (AccessibilityNodeInfo n : liste) {
                        if(n.getClassName().equals("android.widget.Button") && n.isEnabled()){
                            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            hasSend = true;
                        }
                    }
                }
            }
            pressBackButton();
        }
    }

    /**
     * 模拟back按键
     */
    private void pressBackButton(){
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 拉起微信界面
     * @param event
     */
    private void sendNotifacationReply(AccessibilityEvent event) {
        hasAction = true;
        if (event.getParcelableData() != null
                && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event
                    .getParcelableData();
            String content = notification.tickerText.toString();
            String[] cc = content.split(":");
            name = cc[0].trim();
            scontent = cc[1].trim();

            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    private boolean fill() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        Log.e("xiaoxin", "fill: "+"xiaoxin" );
        if (rootNode != null) {
            return findEditText(rootNode, "正在忙,稍后回复你");
        }
        return false;
    }

    @SuppressLint("NewApi")
    private boolean fill(String edit,String content) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findEditTextSend(rootNode, edit,content);
        }
        return false;
    }

    private boolean fillSend(int number) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            //return findEditTextSend(rootNode, PerformClickUtils.CONTENT+number);
            return findEditTextSend(rootNode, PerformClickUtils.CONTENT);
        }
        return false;
    }


    /**
     * 通过rootNode 填充内容
     * @param rootNode
     * @param content
     * @return
     */
    private boolean findEditText(AccessibilityNodeInfo rootNode, String content) {
        int count = rootNode.getChildCount();

        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);
            if (nodeInfo == null) {
                continue;
            }
            if(nodeInfo.getContentDescription() != null){
                int nindex = nodeInfo.getContentDescription().toString().indexOf(name);
                int cindex = nodeInfo.getContentDescription().toString().indexOf(scontent);
                if(nindex != -1){
                    itemNodeinfo = nodeInfo;
                }
            }
            if ("android.widget.EditText".equals(nodeInfo.getClassName())) {
                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                        AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
                arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                        true);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                        arguments);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                // 创建一个剪贴数据集，把content数据集放进去
                ClipData clip = ClipData.newPlainText("label", content);
                // 获取系统剪贴板
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 把数据集设置（复制）到剪贴板
                clipboardManager.setPrimaryClip(clip);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                return true;
            }

            if (findEditText(nodeInfo, content)) {
                return true;
            }
        }

        return false;
    }

    private boolean findEditTextSend(AccessibilityNodeInfo rootNode,String content) {
        List<AccessibilityNodeInfo> editInfo = rootNode.findAccessibilityNodeInfosByViewId(Contant.chatUI_EditText_id);
        if(editInfo!=null&&!editInfo.isEmpty()){
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);
            editInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            return true;
        }
        return false;
    }

    private boolean findEditTextSend(AccessibilityNodeInfo rootNode, String edit,String content) {
        Log.e(TAG, "findEditTextSend: rootNode"+rootNode.toString() );
        List<AccessibilityNodeInfo> editInfo = rootNode.findAccessibilityNodeInfosByViewId(edit);

        if(editInfo!=null&&!editInfo.isEmpty()){
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);
            editInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Log.e("xiaoxin", "findEditTextSend: true");
            return true;
        }else {
            Log.e("xiaoxin", "findEditTextSend: null");
        }
        Log.e("xiaoxin", "findEditTextSend: false"+editInfo.isEmpty() );
        return false;
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 判断指定的应用是否在前台运行
     *
     * @param packageName
     * @return
     */
    private boolean isAppForeground(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }

        return false;
    }


    /**
     * 将当前应用运行到前台
     */
    private void bring2Front() {
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    /**
     * 回到系统桌面
     */
    private void back2Home() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    /**
     * 系统是否在锁屏状态
     *
     * @return
     */
    private boolean isScreenLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    private void wakeAndUnlock() {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire(1000);

        //得到键盘锁管理器对象
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();

    }

    private void release() {

        if (locked && kl != null) {
            Log.d("maptrix", "release the lock");
            //得到键盘锁管理器对象
            kl.reenableKeyguard();
            locked = false;
        }
    }
}
