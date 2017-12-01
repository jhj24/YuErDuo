package com.jqyd.aliveservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WakeUpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, ForegroundService::class.java))
    }

}
