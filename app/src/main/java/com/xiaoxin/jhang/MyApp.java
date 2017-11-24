package com.xiaoxin.jhang;

import android.app.Application;

import com.xiaoxin.jhang.crash.CrashHandler;

/**
 * @author xiaoxin
 * @date 2017/11/24
 * @describe ：
 * 修改内容
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(this);
    }
}
