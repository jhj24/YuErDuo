package com.jqyd.aliveservice

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder

class GrayInnerService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(ForegroundService.GRAY_SERVICE_ID, Notification())
        stopForeground(true)
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
