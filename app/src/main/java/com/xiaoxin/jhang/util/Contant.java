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
    public final static String MOBILEQQ_PNAME = "com.tencent.mobileqq";
    public final static String SINA_PNAME = "com.sina.weibo";

    public final static String SINA_CLASSNAME = "com.sina.weibo.feed.detail.composer.ComposerActivity";

    public static String mobileqq = "com.tencent.mobileqq.activity.SplashActivity";

    /** 6.5.16版本 */
    public static String contactUI_listview_id = selectPages  ? "com.tencent.mm:id/bt0" : "com.tencent.mm:id/i9";
    public static String contactUI_item_id = selectPages ? "com.tencent.mm:id/ajz" : "com.tencent.mm:id/io";
    public static String contactUI_name_id = selectPages ? "com.tencent.mm:id/ak1" : "com.tencent.mm:id/ir";
    public static String chatUI_EditText_id = "com.tencent.mm:id/a71";

    /** mobile 6.5.16版本 */
    public static String mobileqq_et = "com.tencent.mobileqq:id/input";

    /** sina 6.5.16版本 */
    public static String sina_et = "com.sina.weibo:id/et_message";
    public static String sina_reply_et = "com.sina.weibo:id/edit_view";

}
