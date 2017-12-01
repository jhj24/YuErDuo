package com.jqyd.yuerduo.bean;

import java.io.Serializable;

/**
 * Created by liushiqi on 2016/12/16 0016.
 * 考勤地点
 */

public class AttendanceLocation extends BaseBean implements Serializable{
    public Integer id;		// 考勤地点ID
    public String name;		// 考勤地点名称
    public String address;		// 地址
    public String longitude;		// 经度
    public String latitude;		// 纬度

}
