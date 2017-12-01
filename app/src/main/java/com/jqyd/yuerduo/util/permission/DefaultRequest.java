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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 请求权限和回调。
 * Created by jianhaojie on 2017/5/24.
 */
class DefaultRequest implements
        Request,
        PermissionActivity.PermissionListener {


    private Activity target;

    private int mRequestCode;
    private String[] mPermissions;
    private PermissionListener mCallback;

    private String[] mDeniedPermissions;

    DefaultRequest(Activity target) {
        if (target == null)
            throw new IllegalArgumentException("The target can not be null.");
        this.target = target;
    }

    @Override
    public Request permission(String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    @Override
    public Request requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    @Override
    public Request callback(PermissionListener callback) {
        this.mCallback = callback;
        return this;
    }

    @Override
    public void start() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mCallback.onSucceed(mRequestCode, Arrays.asList(mPermissions));
        } else {
            mDeniedPermissions = getDeniedPermissions(target, mPermissions);
            if (mDeniedPermissions.length > 0) { //有权限被禁止
                resume();
            } else { // 所有权限都被允许
                mCallback.onSucceed(mRequestCode, Arrays.asList(mPermissions));
            }
        }
    }

    private static String[] getDeniedPermissions(Context context, String... permissions) {
        List<String> deniedList = new ArrayList<>(1);
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                deniedList.add(permission);
        return deniedList.toArray(new String[deniedList.size()]);
    }
    

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void resume() {
        PermissionActivity.setPermissionListener(this);
        Intent intent = new Intent(target, PermissionActivity.class);
        intent.putExtra(PermissionActivity.KEY_INPUT_PERMISSIONS, mDeniedPermissions);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        target.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult( String[] permissions, int[] grantResults) {
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++)
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                deniedList.add(permissions[i]);

        if (deniedList.isEmpty())
            mCallback.onSucceed(mRequestCode, Arrays.asList(mPermissions));
        else
            mCallback.onFailed(mRequestCode, deniedList);
    }

}