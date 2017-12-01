package com.jqyd.yuerduo.util;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * 高德地图编码工具类
 * getAddress（）：经纬度-->地址等
 * getLatLon（）：地址——>经纬度等
 * Created by jianhaojie on 2016/7/25.
 */
public class ParsePlaceUtil implements GeocodeSearch.OnGeocodeSearchListener {

    public interface OnParseAddressListener{
        void callback(GeocodeAddress result, int code);
    }

    public interface OnParseLatLonListener{
        void callback(RegeocodeAddress result,int code);
    }

    private OnParseAddressListener aListener;
    private OnParseLatLonListener mListener;
    private GeocodeSearch geocoderSearch;

    public ParsePlaceUtil(Context context) {
        geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 地理编码,查询经纬度
     * @param name 要查询的具体位置
     */
    public void getLatlon(final String name,OnParseAddressListener aListener) {
        this.aListener = aListener;
        // arg0:地址，arg1:城市
        GeocodeQuery query = new GeocodeQuery(name, "");
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 逆地理编码，查询地址
     */
    public void getAddress(final LatLonPoint latLonPoint,OnParseLatLonListener mListener) {
        this.mListener = mListener;
        // arg0:Latlng，arg1:范围，arg2:坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query); // 设置同步逆地理编码请求
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 1000){
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                aListener.callback(address,rCode);
            }
        }
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000){
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                mListener.callback(result.getRegeocodeAddress(),rCode);
            }
        }
    }
    /*
    1、adcode 区域编码
    2、building 建筑物名称
    3、city 城市名称
    4、district 区（县）
    5、formatAddress 地址
    6、latlonPoint 经纬度
    7、level 匹配级别
    8、neighborhood 社区名称
    9、province 省
    10、township 乡镇名称
    11、businessAreas 商圈列表
    12、crossroads 交叉路口列表
    13、roads 道路列表
    14、streetNumber 门牌信息
     */

}
