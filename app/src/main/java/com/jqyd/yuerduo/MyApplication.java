package com.jqyd.yuerduo;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.jqyd.amap.LocationService;
import com.jqyd.yuerduo.activity.main.MainActivity;
import com.jqyd.yuerduo.bean.FunctionBean;
import com.jqyd.yuerduo.util.FileLogTool;
import com.jqyd.yuerduo.util.SystemEnv;
import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * application
 * Created by zhangfan on 15-12-30.
 */
//public class MyApplication extends Application {
public class MyApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    public LocationService locationService;

    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Logger.init("久其供应链")                 // default PRETTYLOGGER or use just init()
//                .methodCount(3)                 // default 2
//                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
//                .logLevel(LogLevel.NONE)        // default LogLevel.FULL
//                .methodOffset(2)                // default 0
                .logTool(new FileLogTool(this)); // custom log tool, optional

        TypefaceCollection typeface = new TypefaceCollection.Builder()
                .set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/HYQH.ttf"))
                .create();
        TypefaceHelper.init(typeface);

//        SDKInitializer.initialize(getApplicationContext());
        locationService = new LocationService(this);

//        PlatformConfig.setSinaWeibo("2117222028", "1468577ab748b2d87d6cdc92ac9e9da8");
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
//        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
//        PlatformConfig.setTencentWeibo("", "");


//        极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.setLatestNotificationNumber(this, Integer.MAX_VALUE);

//        图片加载
        initImageLoader(getApplicationContext());

//        bugly
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        //...在这里设置strategy的属性，在bugly初始化时传入
//        CrashReport.initCrashReport(this, "900035339", false, strategy);
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppChannel(BuildConfig.FLAVOR);
        Bugly.init(getApplicationContext(), "900035339", false, strategy);
        Beta.canShowUpgradeActs.add(MainActivity.class);


        Stetho.initializeWithDefaults(this);
    }

    private List<FunctionBean> allFunction;

    public List<FunctionBean> getAllFunctionCopies() {
        List<FunctionBean> functions = SystemEnv.getFunctions(this);
        if (functions != null) return functions;

        return new ArrayList<>();
    }

    public List<FunctionBean> getAllFunction() {
        if (allFunction == null) {
            allFunction = getAllFunctionCopies();
        }
        return allFunction;
    }

    public void initFunctions(List<FunctionBean> dataList) {
        allFunction = dataList;
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());
    }

}
