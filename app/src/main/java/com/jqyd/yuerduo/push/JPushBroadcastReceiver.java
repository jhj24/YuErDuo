package com.jqyd.yuerduo.push;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.jqyd.aliveservice.ForegroundService;
import com.jqyd.yuerduo.activity.ask.AskDetailActivity;
import com.jqyd.yuerduo.activity.leave.LeaveDetailActivity;
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent;
import com.jqyd.yuerduo.activity.message.MessageDetailActivity;
import com.jqyd.yuerduo.activity.order.OrderListActivity;
import com.jqyd.yuerduo.activity.travel.TravelDetailActivity;
import com.jqyd.yuerduo.activity.visit.VisitStoreInfoActivity;
import com.jqyd.yuerduo.constant.OrderListType;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhangfan on 2016/2/2.
 */
public class JPushBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        context.startService(new Intent(context, ForegroundService.class));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            String EXTRA_EXTRA = bundle.getString(JPushInterface.EXTRA_EXTRA);
            PushData pushData = new Gson().fromJson(EXTRA_EXTRA, PushData.class);
            PushEnum pushEnum = null;
            pushEnum = PushEnum.valueOf(pushData.type);
            String type = "-1";
            try {
                if (pushData.data != null) {
                    pushData.data = pushData.data.replace("&quot;", "\"");
                }
                JSONObject jsonObj = new JSONObject(pushData.data);
                Iterator it = jsonObj.keys();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    if (key.equals("type")) {
                        type = jsonObj.getString(key);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pushEnum != null) {
                EventBus.getDefault().post(new RefreshNumberEvent(String.valueOf(pushEnum), type));
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

//            String EXTRA_MESSAGE = bundle.getString(JPushInterface.EXTRA_MESSAGE);//这个是空的
            String EXTRA_EXTRA = bundle.getString(JPushInterface.EXTRA_EXTRA);
            PushData pushData = new Gson().fromJson(EXTRA_EXTRA, PushData.class);
            PushEnum pushEnum = null;
            try {
                pushEnum = PushEnum.valueOf(pushData.type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pushEnum != null) {
                Intent intent1 = new Intent(context, pushEnum.cls);
                if (pushData.type.equals("lottery")) {
                    intent1.putExtra("title", "我的送货单");
                    intent1.putExtra("type", "" + OrderListType.Shipping);
                }
                intent1.putExtra("data", "" + pushData.data);
                try {
                    if (pushData.data != null) {
                        pushData.data = pushData.data.replace("&quot;", "\"");
                    }
                    JSONObject jsonObj = new JSONObject(pushData.data);
                    Iterator it = jsonObj.keys();
                    while (it.hasNext()) {
                        String key = it.next().toString();
                        intent1.putExtra(key, jsonObj.getString(key));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent1);
            }

//            Logger.i(""+EXTRA_MESSAGE+"======"+EXTRA_EXTRA);
//            //打开自定义的Activity
//            Intent i = new Intent(context, TestActivity.class);
//            i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//            context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//        Logger.i("" + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//        Logger.i("" + extras);
//        {"msg_content":"notice","extras":{"content":"fucs","id":137,"noticetitle":"123","createTime":1455507522795,"creator":"admin"}}
        String message1 = "收到推送消息：" + message + "==>>" + extras;
        Logger.i(message1);
//        showNormal(context, message, extras);
//        Toast.makeText(context, "" + message1, Toast.LENGTH_SHORT).show();
    }

    private class PushData {
        public String type;
        public String data;
    }

    private enum PushEnum {
        notice(MessageDetailActivity.class),
        order(OrderListActivity.class),
        leave(LeaveDetailActivity.class),
        travel(TravelDetailActivity.class),
        ask(AskDetailActivity.class),
        visit(VisitStoreInfoActivity.class),
        lottery(OrderListActivity.class);//兑奖
        // TODO: 2016/4/7 0007 新增推送类型在这里加入


        Class<? extends Activity> cls;

        PushEnum(Class<? extends Activity> cls) {
            this.cls = cls;
        }

    }
//
//    private class PushMessage {
//        int id;
//        String title;
//        String noticetitle;
//        String content;
//        String creator;
//    }

//    private void showNormal(Context context, String type, String extras) {
//        Class cls = null;
//        if ("notice".equals(type)) {
//            cls = MessageListActivity.class;
//        }
//        if (cls == null) return;
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Calendar instance = Calendar.getInstance();
//
//        PushMessage pushMessage = new Gson().fromJson(extras, PushMessage.class);
//
//        Intent intent = new Intent(context, cls);
//        PendingIntent pd = PendingIntent.getActivity(context, 0, intent, 0);
////        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
////                R.drawable.logo_t);
//        Notification notification = new NotificationCompat.Builder(context)
////                .setLargeIcon(icon) //大图标
//                .setSmallIcon(R.drawable.icon_small) //小图标
//                .setTicker("收到新通知") //通知栏显示文本
//                .setContentInfo(pushMessage.creator) //右下角显示的小文本
//                .setContentTitle(pushMessage.noticetitle) //下拉栏显示标题
//                .setContentText(pushMessage.content) //下拉栏显示内容
//                .setNumber(++messageNum) //
//                .setContentIntent(pd) //点击跳转
//                .setAutoCancel(true) //点击通知跳转后是否自动取消掉该条通知
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setOngoing(true) //设置通知不能被用户清除
//                .setWhen(instance.getTimeInMillis()) //设置通知上所显示的日期时间
//                .build();
//        manager.notify(1, notification);
//    }
//
//    private static int messageNum = 0;

}
