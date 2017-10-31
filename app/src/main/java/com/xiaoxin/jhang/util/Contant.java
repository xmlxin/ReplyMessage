package com.xiaoxin.jhang.util;

/**
 * @author xiaoxin
 * @date 2017/10/27
 * @describe ：  未实现的想法，添加动态悬浮窗(这个悬浮窗可以根据手势自动缩放大小)
 * 修改内容
 */

public class Contant {

    public static final String  CONTACT = "contact";
    public static final String DEFAULT_REPLY = "正在忙,稍后回复你(机器人自动回复)";
    public static final String SP_REPLY = "wxReply";
    public static String reply;
    public static boolean isMOniSendMsg;//是否模拟发送消息
    public static boolean isAutoReply;//是否开启全部自动回复功能
    public static boolean isAutoMatchReply;//是否自定义回复(指定自动回复某人)
    public static boolean selectPages;//true 聊天列表  false 通讯录

    public final static String MM_PNAME = "com.tencent.mm";

    public static String mobileqq = "com.tencent.mobileqq.activity.SplashActivity";

//    public static String mobileqq_listview = "com.tencent.mobileqq:id/recent_chat_list";
//    public static String mobileqq_rl = "com.tencent.mobileqq:id/relativeItem";
//    public static String mobileqq_name = "com.tencent.mobileqq:id/title";
//    public static String mobileqq_et = "com.tencent.mobileqq:id/input";

    /** 6.5.16版本 通讯录列表 可用*/
//    public static String contactUI_listview_id = "com.tencent.mm:id/i9";
//    public static String contactUI_item_id = "com.tencent.mm:id/io";
//    public static String contactUI_name_id = "com.tencent.mm:id/ir";
//    public static String chatUI_EditText_id = "com.tencent.mm:id/a71";

    /** 6.5.16版本 wx聊天列表页面id*/
    public static String contactUI_listview_id = selectPages  ? "com.tencent.mm:id/bt0" : "com.tencent.mm:id/i9";
    public static String contactUI_item_id = selectPages ? "com.tencent.mm:id/ajz" : "com.tencent.mm:id/io";
    public static String contactUI_name_id = selectPages ? "com.tencent.mm:id/ak1" : "com.tencent.mm:id/ir";
    public static String chatUI_EditText_id = "com.tencent.mm:id/a71";

    /** 6.5.13版本 可用*/
//    public static String contactUI_listview_id = "com.tencent.mm:id/i2";  //聊天页面listview
//    public static String contactUI_item_id = "com.tencent.mm:id/ih";      //聊天页面rl
//    public static String contactUI_name_id = "com.tencent.mm:id/ik";      //聊天页面view
//    public static String chatUI_EditText_id = "com.tencent.mm:id/a6g";   //输入框




}
