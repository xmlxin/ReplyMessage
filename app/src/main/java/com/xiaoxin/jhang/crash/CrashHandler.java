package com.xiaoxin.jhang.crash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import com.xiaoxin.jhang.MainActivity;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xiaoxin
 * @date 2017/1/19
 * @describe ：异常处理类
 * 修改内容
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();

    private static final boolean DEBUG = true;
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/xiaoxin/logCrash/";
    private static final String FILE_NAME = "crash";

    //log文件的后缀名
    private static final String FILE_SUFFIX = ".log";

    private static CrashHandler mCrashHandler;

    //系统默认的异常处理(默认情况下，系统会终止当前的异常程序)
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    private Context mContext;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        if (mCrashHandler == null) {
            synchronized (CrashHandler.class) {
                if (mCrashHandler == null) {
                    mCrashHandler = new CrashHandler();
                }
            }
        }

        return mCrashHandler;
    }

    public void init(Context context) {
        //获取系统默认的异常处理器
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        try {
            //toast提示用户程序crash掉了
            //handleException(ex);
            //导出异常信息到SD卡中
            dumpExceptionToSDCard(ex);
            //这里可以通过网络上传异常信息到服务器，便于开发人员分析日志解决bug
            uploadExceptionToServer();
            SystemClock.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //打印出当前调用栈信息
        //ex.printStackTrace();

        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (mUncaughtExceptionHandler != null) {
            mUncaughtExceptionHandler.uncaughtException(t, ex);
        } else {
            Process.killProcess(Process.myPid());
            Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    mContext.getApplicationContext(), 0, intent,
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            //退出程序                                          
            AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    restartIntent); // 1秒钟后重启应用   
        }
        //showDialog();

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }

    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.e(TAG, "dumpExceptionToSDCard: ");
                return;
            }
        }

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                new Date(System.currentTimeMillis()));

        //以当前时间创建log文件
        //File file = new File(FILE_PATH + FILE_NAME + time + FILE_SUFFIX);
//        File file = new File(Environment.getExternalStorageDirectory()+"/MyApp/logCrash/",
//                "aaaaa.xml");
        File file1 = new File(FILE_PATH);
        File file = new File(FILE_PATH + FILE_NAME + time + FILE_SUFFIX);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            //导出发生异常的时间
            printWriter.println(time);

            //导出手机信息
            dumpPhoneInfo(printWriter);

            printWriter.println();
            //导出异常的调用栈信息
            ex.printStackTrace(printWriter);

            printWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    private void uploadExceptionToServer() {
        //TODO :如果需要把crash文件上传至服务器，在这里操作.

    }

    private void dumpPhoneInfo(PrintWriter printWriter) throws
            PackageManager.NameNotFoundException {

        //应用的版本名称和版本号
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(
                mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

        printWriter.print("App Version: ");
        printWriter.print(packageInfo.versionName);
        printWriter.print('_');
        printWriter.println(packageInfo.versionCode);

        //Android版本号
        printWriter.print("OS Version: ");
        printWriter.print(Build.VERSION.RELEASE);
        printWriter.print("_");
        printWriter.println(Build.VERSION.SDK_INT);

        //手机制造商
        printWriter.print("Vendor: ");
        printWriter.println(Build.MANUFACTURER);

        //手机型号
        printWriter.print("Model: ");
        printWriter.println(Build.MODEL);

        //CPU架构
        printWriter.print("CPU ABI: ");
        printWriter.println(Build.CPU_ABI);
    }
}
