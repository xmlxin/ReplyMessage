package com.xiaoxin.jhang.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;
import java.util.List;

/**
 * @author xiaoxin
 * @date 2017/10/27
 * @describe ：
 * 修改内容
 */

public class SystemUtil {

    private static final String TAG = "SystemUtil";

    /**
     * 打开辅助功能
     */
    public static void openService(Context context){
        try {
            //打开系统设置中辅助功能
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
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
     * 回到系统桌面
     */
    private void back2Home(Context context) {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(home);
    }

    /**
     * 判断指定的应用是否在前台运行
     *
     * @param packageName
     * @return
     */
    private boolean isAppForeground(String packageName,Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
    private void bring2Front(Context context) {
        ActivityManager activtyManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (context.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    public int fromBitmapGetRgb(Bitmap bitmap) {
        //Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.img_video);
        Palette palette = Palette.from(bitmap).generate();
        //Palette.from(bitmap).maximumColorCount(32).addFilter();
        if (palette.getVibrantSwatch() != null) {
            Log.e(TAG, "fromBitmapGetRgb: 1"+palette.getVibrantSwatch().getRgb() );
            return palette.getVibrantSwatch().getRgb();
        }
        if (palette.getLightMutedSwatch() != null) {
            Log.e(TAG, "fromBitmapGetRgb: 2"+palette.getLightMutedSwatch().getRgb() );
            return palette.getLightMutedSwatch().getRgb();
        }
        if (palette.getDarkVibrantSwatch() != null) {
            Log.e(TAG, "fromBitmapGetRgb: 3"+palette.getDarkVibrantSwatch().getRgb() );
            return palette.getDarkVibrantSwatch().getRgb();
        }
        if (palette.getDarkMutedSwatch() != null) {
            Log.e(TAG, "fromBitmapGetRgb: 4"+palette.getDarkMutedSwatch().getRgb() );
            return palette.getDarkMutedSwatch().getRgb();
        }
        if (palette.getDominantSwatch() != null) {
            Log.e(TAG, "fromBitmapGetRgb: 5"+palette.getDominantSwatch().getRgb() );
            return palette.getDominantSwatch().getRgb();
        }
        if (palette.getLightVibrantSwatch() != null) {
            Log.e(TAG, "fromBitmapGetRgb: 6"+palette.getLightVibrantSwatch().getRgb() );
            return palette.getLightVibrantSwatch().getRgb();
        }
        return Color.parseColor("#3F51B5");
    }

}
