package com.xiaoxin.jhang.util;

/**
 * @author xiaoxin
 * @date 2017/10/27
 * @describe ：
 * 修改内容
 */

public class Contant {

    public static final String  CONTACT = "contact";
    public static final String DEFAULT_REPLY = "正在忙,稍后回复你(机器人自动回复)";
    public static final String SP_REPLY = "wxReply";
    public static boolean isMOniSendMsg;
    public static boolean isAutoReply;
    public static boolean isAutoMatchReply;
    public static boolean selectPages;

    public final static String MM_PNAME = "com.tencent.mm";

    public static String mobileqq = "com.tencent.mobileqq.activity.SplashActivity";

    /** 6.5.16版本 */
    public static String contactUI_listview_id = selectPages  ? "com.tencent.mm:id/bt0" : "com.tencent.mm:id/i9";
    public static String contactUI_item_id = selectPages ? "com.tencent.mm:id/ajz" : "com.tencent.mm:id/io";
    public static String contactUI_name_id = selectPages ? "com.tencent.mm:id/ak1" : "com.tencent.mm:id/ir";
    public static String chatUI_EditText_id = "com.tencent.mm:id/a71";
}
