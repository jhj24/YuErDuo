package com.jqyd.amap;

import com.amap.api.location.AMapLocation;

/**
 * Created by zhangfan on 17-3-22.
 */

public interface LocationCallback {
    void onLocation(AMapLocation location);
}
