package com.jqyd.yuerduo.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.amap.api.services.core.LatLonPoint
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.bean.*
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getDevicesID
import com.jqyd.yuerduo.net.*
import com.jqyd.yuerduo.util.MapUtil
import com.jqyd.yuerduo.util.ParsePlaceUtil
import com.jqyd.yuerduo.util.SystemEnv
import com.nightfarmer.androidcommon.device.DeviceInfo
import com.orhanobut.logger.Logger
import cz.msebera.android.httpclient.Header
import org.jetbrains.anko.onUiThread
import java.text.SimpleDateFormat
import java.util.*

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class LocationService : IntentService("LocationService") {
    var need_broad_cast = false;

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_SEND_LOC == action) {
                need_broad_cast = intent.getBooleanExtra(NEED_BROAD_CAST, false)
                onUiThread {
                    sendLocToServer()
                }
            }
        }
    }

    var amapLocListener = com.jqyd.amap.LocationCallback { amapLocation ->
        Log.e("123",amapLocation.errorCode.toString())
        //设备没有定位权限，返回错误码12（由于定位sdk版本不同错误码不同，防止sdk升级后出现错误，把设备没有定位权限的错误码都加上判断）
        if (amapLocation.locationType == 0 && (amapLocation.errorCode == 12 || amapLocation.errorCode == 13 || amapLocation.errorCode == 33)) {
            val locationBean = LocationBean(amapLocation.latitude, amapLocation.longitude, amapLocation.accuracy)
            locationBean.errorCode = 12
            broadcastLocation(locationBean)
        } else if (amapLocation.errorCode == 0) {
            val lon = amapLocation.longitude
            val lat = amapLocation.latitude
            val gcj02_To_Bd09 = MapUtil.gcj02_To_Bd09(lat, lon)
            if (amapLocation.address.isNullOrEmpty()) {
                ParsePlaceUtil(this).getAddress(LatLonPoint(lat, lon)) { result, code ->
                    val locationBean = LocationBean(gcj02_To_Bd09.wgLon, gcj02_To_Bd09.wgLat, amapLocation.accuracy)
                    locationBean.provice = result.province
                    locationBean.city = result.city
                    locationBean.district = result.district
                    locationBean.content = result.formatAddress
                    locationBean.lbstype = amapLocation.locationType
                    locationBean.errorCode = amapLocation.errorCode
                    locationBean.address = result.formatAddress
                    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    locationBean.createTime = df.format(Date(amapLocation.time))
                    if (locationBean.address.isNullOrEmpty()) {
                        locationBean.errorCode = 101
                    }
                    if (amapLocation.errorCode == 0) {
                        SystemEnv.saveLatestSuccessLocation(this, locationBean)
                    }
                    broadcastLocation(locationBean)
                }
                return@LocationCallback
            }
            val locationBean = LocationBean(gcj02_To_Bd09.wgLon, gcj02_To_Bd09.wgLat, amapLocation.accuracy)
            locationBean.provice = amapLocation.province
            locationBean.city = amapLocation.city
            locationBean.district = amapLocation.district
            val content = amapLocation.address.replaceFirst(amapLocation.province, "")
                    .replaceFirst(amapLocation.city, "").replaceFirst(amapLocation.district, "")
            locationBean.content = content
            locationBean.lbstype = amapLocation.locationType
            locationBean.errorCode = amapLocation.errorCode
            locationBean.address = amapLocation.address
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            locationBean.createTime = df.format(Date(amapLocation.time))
            if (amapLocation.errorCode == 0) {
                SystemEnv.saveLatestSuccessLocation(this, locationBean)
            }
            broadcastLocation(locationBean)
        } else {//其他原因导致定位失败
            val locationBean = LocationBean(amapLocation.latitude, amapLocation.longitude, amapLocation.accuracy)
            locationBean.errorCode = 101
            broadcastLocation(locationBean)
        }

    }

    fun broadcastLocation(locationBean: LocationBean) {
        if (need_broad_cast) {
            need_broad_cast = false
            val intent = Intent()
            intent.action = BROAD_CAST
            intent.putExtra("loc", locationBean)
            sendBroadcast(intent)
        } else {
            upLoadLoc(locationBean)
        }
    }

    private fun upLoadLoc(locationBean: LocationBean) {
        val currentMillis = System.currentTimeMillis()
        val locationStrategy: LocationStrategy? = SystemEnv.getLocationStrategy(this.applicationContext)
        val lastPositionUploadTime: Long? = SystemEnv.getUploadPositionTime(this.applicationContext)
        if (lastPositionUploadTime != null) {
            locationStrategy?.let { strategy ->
                val startMillis = getMillis(strategy.startTime)
                val endMillis = getMillis(strategy.endTime)

                if (currentMillis - strategy.lastGetStrategyTime > 60 * 60 * 1000) {
                    getLocationStrategy()
                }
                val isOutRange = currentMillis in startMillis..endMillis && currentMillis - lastPositionUploadTime < strategy.interval * 60 * 1000
                if (currentMillis !in startMillis..endMillis || isOutRange) {
                    return
                }
            }
        }

        SystemEnv.updateUploadPositionTime(this)
        val params = hashMapOf(
                "createTime" to locationBean.createTime.orEmpty(),
                "lon" to locationBean.lon.toString(),
                "lat" to locationBean.lat.toString(),
                "provice" to locationBean.provice.orEmpty(),
                "city" to locationBean.city.orEmpty(),
                "district" to locationBean.district.orEmpty(),
                "content" to locationBean.content.orEmpty(),
                "lbstype" to locationBean.lbstype.toString(),
                "radius" to locationBean.radius.toString(),
                "operType" to 1.toString(),
                "deviceId" to getDevicesID()
        )
        HttpCall.request(this.applicationContext, URLConstant.UPLOAD_LOCATION, params, object : GsonHttpCallback<BaseBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                Log.w("xxx", "上传失败")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                Log.w("xxx", "上传成功")
            }
        })
        //        locationService?.unregisterListener(locListener) //注销掉监听
        locationService?.stop() //停止定位服务
    }

    private var locationService: com.jqyd.amap.LocationService? = null

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun sendLocToServer() {

        locationService = (application as MyApplication).locationService
        locationService?.setListener(amapLocListener)
        //注册监听

//        locationService?.setLocationOption(locationService?.defaultLocationClientOption)

        // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
        locationService?.start()// 定位SDK

    }

    companion object {
        // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        val ACTION_SEND_LOC = "com.jqyd.yuerduo.service.action.SendLocToServer"
        val BROAD_CAST = "com.jqyd.yuerduo.service.billLoc"
        val NEED_BROAD_CAST = "com.jqyd.yuerduo.service.needBroadCost"

        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        //        fun startActionFoo(context: Context) {
        //            val intent = Intent(context, LocationService::class.java)
        //            intent.action = ACTION_SEND_LOC
        //            intent.putExtra("p1", "yoo1")
        //            intent.putExtra("p2", "yoo2")
        //            context.startService(intent)
        //        }
    }

    fun getLocationStrategy() {
        val user = SystemEnv.getLogin(this)
        val imsi = DeviceInfo.getIMSI(this)
        if (imsi.isNullOrEmpty()) {
            return
        }
        if (!user.password.isNullOrBlank()) {
            HttpCall.request(this, URLConstant.Login, ParamBuilder.Login(this, user.memberName.orEmpty(), user.password.orEmpty(), imsi.orEmpty()), object : GsonHttpCallback<UserBean>() {
                override fun onFailure(msg: String, errorCode: Int) {
                }

                override fun onSuccess(result: ResultHolder<UserBean>) {
                    if (result.data != null && result.data.locationStrategy != null) {
                        result.data.locationStrategy.lastGetStrategyTime = System.currentTimeMillis()
                        val locationStrategy = result.data.locationStrategy
                        SystemEnv.saveLocationStrategy(this@LocationService, locationStrategy)
                    }
                }
            })
        }
    }

    private fun getMillis(time: String): Long {
        val array = time.split(":")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, array[0].toInt())
        calendar.set(Calendar.MINUTE, array[1].toInt())
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND))
        return calendar.timeInMillis
    }

}
