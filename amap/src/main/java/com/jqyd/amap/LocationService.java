package com.jqyd.amap;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 高德定位
 */
public class LocationService {


    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;

    private AMapLocationClientOption defaultOption = getDefaultOption();


    private final Object objLock = new Object();
    private LocationCallback listener;

    /***
     * @param locationContext
     */
    public LocationService(Context locationContext) {
        synchronized (objLock) {
            //初始化定位
            mLocationClient = new AMapLocationClient(locationContext);
            //设置定位回调监听
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null) {
                        if (listener != null) {
                            listener.onLocation(aMapLocation);
                        }
//                        if (aMapLocation.getErrorCode() == 0) { //0,成功
//                            //可在其中解析amapLocation获取相应内容。
//                            if (listener != null) {
//                                listener.onLocation(aMapLocation);
//                            }
//                        } else {
//                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
//                            Log.e("AmapError", "location Error, ErrCode:"
//                                    + aMapLocation.getErrorCode() + ", errInfo:"
//                                    + aMapLocation.getErrorInfo());
//
//                        }
                    }

                }
            });
        }
    }

    public void setListener(LocationCallback listener) {
        this.listener = listener;
    }

    private AMapLocationClientOption getDefaultOption() {
        //声明AMapLocationClientOption对象
//初始化AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        //获取一次定位结果：//该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，
        // 启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
//            mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。每次定位主动刷新WIFI模块会提升WIFI定位精度，但相应的会多付出一些电量消耗。setWifiScan
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
//            mLocationOption.setHttpTimeOut(20000);
        return mLocationOption;
    }

    public void start() {
        synchronized (objLock) {
//            if (client != null && !client.isStarted()) {
//                client.start();
//            }
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(defaultOption);
            //启动定位
            mLocationClient.startLocation();
        }
    }

    public void stop() {
        synchronized (objLock) {
//            mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
//            销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象。
//            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
//            if (client != null && client.isStarted()) {
//                client.stop();
//            }
        }
    }

}
