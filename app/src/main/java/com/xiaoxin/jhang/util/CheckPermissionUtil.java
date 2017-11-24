package com.xiaoxin.jhang.util;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xiaoxin
 * @date 2017/11/24
 * @describe ：检测权限
 * 修改内容
 */

public class CheckPermissionUtil {

    /**
     * 检测是否有悬浮窗权限(只支持6.0以上)
     * @return
     */
    public static boolean checkAlertWindowAllowed(Context context) {
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        return AppOpsManager.MODE_ALLOWED == manager.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                Binder.getCallingUid(), context.getPackageName());
    }

    /**
     * 检测权限 支持4.4以上，通过反射
     * http://www.jianshu.com/p/cb936853cb80
     * @return
     */
    public static boolean isAlertWindowAllowed(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);

            if (method == null) {
                return false;
            }
            Object[] arrayOfObject = new Object[3];
            arrayOfObject[0] = Integer.valueOf(24);
            arrayOfObject[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param context
     * @param op  op 的值是 0 ~ 47，其中0代表粗略定位权限，1代表精确定位权限，24代表悬浮窗权限。
     * @return 返回 0 就代表有权限，1代表没有权限，-1函数出错
     */
    public static int checkOp(Context context, int op){
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19){
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

}
