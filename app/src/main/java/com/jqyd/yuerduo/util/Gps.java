package com.jqyd.yuerduo.util;

/**
 * 经纬度坐标包装类
 * Created by zhangfan on 2016/12/1 0001.
 */

public class Gps {
    private double wgLat;
    private double wgLon;

    public Gps(double wgLat, double wgLon) {
        setWgLat(wgLat);
        setWgLon(wgLon);
    }

    public double getWgLat() {
        return wgLat;
    }

    public void setWgLat(double wgLat) {
        this.wgLat = wgLat;
    }

    public double getWgLon() {
        return wgLon;
    }

    public void setWgLon(double wgLon) {
        this.wgLon = wgLon;
    }

    @Override
    public String toString() {
        return wgLat + "," + wgLon;
    }
}
