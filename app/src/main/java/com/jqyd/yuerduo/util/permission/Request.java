/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jqyd.yuerduo.util.permission;


/**
 * 权限请求
 * Created by jianhaojie on 2017/5/24.
 */
public interface Request<T extends Request> {


    /**
     * 请求的权限
     *
     * @param permissions
     * @return
     */
    T permission(String... permissions);

    /**
     * 请求码
     *
     * @param requestCode
     * @return
     */
    T requestCode(int requestCode);


    /**
     * 结果回调
     *
     * @param callback PermissionListener
     * @return
     */
    T callback(PermissionListener callback);

    /**
     * 请求权限.
     */
    void start();

}
