package com.jqyd.aliveservice

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.support.v7.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import org.jetbrains.anko.async
import org.jetbrains.anko.startService
import org.jetbrains.anko.uiThread
import java.util.*
import java.util.logging.Logger

class ForegroundService : Service() {
    var bcr: BroadcastReceiver? = null

    companion object {
        val GRAY_SERVICE_ID = 10086
        var delay = 5 * 60 * 1000L
        var preTime = 0L;
        var currentThreadId = UUID.randomUUID()
        //        fun isServiceWork(context: Context, serviceName: String): Boolean {
        //            var isWork = false;
        //            var am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        //            var mylist = am.getRunningServices(40)
        //            if (mylist.size <= 0) {
        //                return false;
        //            }
        //            for (serviceInfo in mylist) {
        //                var name = serviceInfo.service.className
        //                if (name == serviceName) {
        //                    isWork = true;
        //                    break;
        //                }
        //            }
        //            return isWork
        //        }
    }

    override fun onBind(intent: Intent): IBinder? {

        return null;
    }

    override fun onCreate() {
        super.onCreate()
//        bcr = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                unregisterReceiver(bcr)
//                startService<ForegroundService>()
//            }
//        }
//        var mif = IntentFilter()
//        mif.addAction("listener")
//        registerReceiver(bcr, mif)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


//        var notification = NotificationCompat.Builder(this)
//                .setTicker("久其供应链正在运行")
//                .setContentInfo("contentInfo")
//                .setContentTitle("ContentTitle")
//                .setContentText("ContentText")
//                .setOngoing(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .build();
//
//        //如果 id 为 0 ，那么状态栏的 notification 将不会显示。
//        startForeground(10086, notification)

        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            var innerIntent = Intent(this, GrayInnerService::class.java)
            startService(innerIntent)
            startForeground(GRAY_SERVICE_ID, Notification())
        }

        currentThreadId = UUID.randomUUID()
        heartBeat(currentThreadId)

        return super.onStartCommand(intent, flags, startId)
    }

    fun heartBeat(threadId: UUID) {
//        Log.i("loc", "" + threadId + "," + currentThreadId)
        if (threadId != currentThreadId) return
        if (System.currentTimeMillis() - preTime >= delay) {
            var sp = getSharedPreferences("loc", MODE_PRIVATE);
            var truePreTime = sp.getLong("preTime", 0);
            if (System.currentTimeMillis() - truePreTime >= delay) {
                var pm = getSystemService(Context.POWER_SERVICE) as PowerManager
                var wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
                wakeLock.acquire()
                var intent = Intent()
                intent.action = "com.jqyd.broadcast.TimerReceiver"
                Log.i("loc", "" + threadId + "," + currentThreadId + ",broadcast" + Date().toString())
                sendBroadcast(intent)
                preTime = System.currentTimeMillis();
                var editor = sp.edit()
                editor.putLong("preTime", System.currentTimeMillis())
                editor.commit();
            }
        }
        delayAndRecall(threadId)
    }

    fun delayAndRecall(threadId: UUID) {
        async() {
            //            Thread.sleep(5 * 60 * 1000)
            Thread.sleep(1000)
            uiThread {
                heartBeat(threadId)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        var intent = Intent()
//        intent.action = "listener"
//        sendBroadcast(intent)
    }

}
