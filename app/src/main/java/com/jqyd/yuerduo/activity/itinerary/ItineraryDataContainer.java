package com.jqyd.yuerduo.activity.itinerary;

import java.util.List;

/**
 * Created by liushiqi on 2017/9/29,0029.
 */

public interface ItineraryDataContainer {
    // 本地
    List<String> getUnUploadList();

    // 服务端获取
    List<String> getMonthList();
}
