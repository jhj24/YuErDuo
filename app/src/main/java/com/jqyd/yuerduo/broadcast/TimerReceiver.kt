package com.jqyd.yuerduo.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.jqyd.yuerduo.service.LocationService

/**
 * Created by zhangfan on 2016/4/19 0019.
 */
class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val intent1 = Intent(context, LocationService::class.java)
        intent1.action = LocationService.ACTION_SEND_LOC
        context.startService(intent1)
    }
}
